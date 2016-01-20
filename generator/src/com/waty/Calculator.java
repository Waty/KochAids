package com.waty;

import com.waty.calculate.Edge;
import com.waty.calculate.KochFractal;

import java.io.IOException;
import java.util.Scanner;

public class Calculator {

    public static final String PATH = "C:\\KochData\\data.idk";

    public static void main(String[] args) {
        System.out.println("What lvl needs to be generated?\n");
        Scanner s = new Scanner(System.in);
        int lvl = s.nextInt();
        KochFractal kf = new KochFractal();
        kf.setLevel(lvl);

        System.out.println();

        System.out.println("Writing to file " + PATH);
        try (MemMappedWriter writer = new MemMappedWriter(PATH)) {
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
