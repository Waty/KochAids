package com.waty;

import com.waty.calculate.Edge;
import com.waty.calculate.KochFractal;
import com.waty.writers.IWriter;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    private static final String PATH = "out/test.txt";

    public static void main(String[] args) {
        System.out.println("What lvl needs to be generated?\n");
        Scanner s = new Scanner(System.in);
        int lvl = s.nextInt();
        KochFractal kf = new KochFractal();
        kf.setLevel(lvl);

        System.out.println();
        System.out.println("Which writer should be used?");
        System.out.println("(1) Text, no buffer");
        System.out.println("(2) Binary, no buffer");
        System.out.println("(3) Text, buffered");
        System.out.println("(4) Binary, buffered\n");

        int idx = s.nextInt() - 1;

        System.out.println("Writing to file " + new File(PATH).getAbsolutePath());
        try (final IWriter writer = IWriter.writers[idx]) {
            writer.open(PATH);
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
