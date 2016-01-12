package com.waty;

import com.waty.calculate.Edge;
import com.waty.calculate.KochFractal;
import com.waty.writers.IWriter;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Calculator {

    public static final String PATH = "C:\\KochData\\";
    private static final String FILE_NAME = "data";

    public static String getPath(int idx) {
        String extension;
        if (idx == 0 || idx == 2) extension = "txt";
        else if (idx == 1 || idx == 3) extension = "bin";
        else extension = "idk";

        return new File(PATH + FILE_NAME + "." + extension).getAbsolutePath();
    }

    public static void main(String[] args) {
        System.out.println("What lvl needs to be generated?\n");
        Scanner s = new Scanner(System.in);
        int lvl = s.nextInt();
        KochFractal kf = new KochFractal();
        kf.setLevel(lvl);

        System.out.println();
        System.out.println("Which writer should be used?");
        for (int i = 0; i < IWriter.writers.length; i++) {
            System.out.printf("(%d) %s%n", i + 1, IWriter.writers[i].toString());
        }
        System.out.println();

        int idx = s.nextInt() - 1;

        String absPath = getPath(idx);
        System.out.println("Writing to file " + absPath);
        try (final IWriter writer = IWriter.writers[idx]) {
            writer.open(absPath);
            writer.setLevel(lvl);
            kf.addObserver((observable, o) -> {
                try {
                    writer.appendEdge((Edge) o);
                } catch (IOException e) {
                    e.printStackTrace();
                    kf.cancel();
                }
            });

            kf.generateBottomEdge();
            kf.generateLeftEdge();
            kf.generateRightEdge();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Finished generating edges");
    }

}
