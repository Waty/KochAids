package com.waty;

import com.waty.calculate.Edge;
import com.waty.calculate.KochFractal;
import com.waty.writers.Writer;

import java.io.IOException;
import java.util.Observable;
import java.util.Scanner;

public class Main {

    private static Writer writer;
    private static KochFractal kf;

    public static void main(String[] args) {
        System.out.println("What lvl needs to be generated?\n");
        Scanner s = new Scanner(System.in);
        int lvl = s.nextInt();
        System.out.println();

        System.out.println("Which writer should be used?");
        System.out.println("(1) Text, no buffer");
        System.out.println("(2) Binary, no buffer");
        System.out.println("(3) Text, buffered");
        System.out.println("(4) Binary, buffered\n");

        int idx = s.nextInt() - 1;
        writer = Writer.writers[idx];
        try {
            writer.open("output.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        kf = new KochFractal();
        kf.setLevel(lvl);
        kf.addObserver(Main::writeEdgeToFile);

        kf.generateBottomEdge();
        kf.generateLeftEdge();
        kf.generateRightEdge();
        System.out.println("Finished generating edges");
    }

    private static void writeEdgeToFile(Observable observable, Object o) {
        try {
            writer.appendEdge((Edge) o);
        } catch (IOException e) {
            e.printStackTrace();
            kf.cancel();
        }
    }
}
