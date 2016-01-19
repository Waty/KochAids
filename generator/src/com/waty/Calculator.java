package com.waty;

import com.waty.calculate.Edge;
import com.waty.calculate.KochFractal;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

public class Calculator {

    public static final String PATH = "C:\\KochData\\";
    private static final String FILE_NAME = "data.idk";

    public static void main(String[] args) {
        System.out.println("What lvl needs to be generated?\n");
        Scanner s = new Scanner(System.in);
        int lvl = s.nextInt();
        KochFractal kf = new KochFractal();
        kf.setLevel(lvl);

        System.out.println();

        String absPath = Paths.get(PATH, FILE_NAME).toAbsolutePath().toString();
        System.out.println("Writing to file " + absPath);
        try (MemMappedWriter writer = new MemMappedWriter(absPath)) {
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
