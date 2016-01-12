package com.waty.readers;

import com.waty.calculate.Edge;
import javafx.scene.paint.Color;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class TextNoBuffer implements IReader {
    protected Scanner scanner;

    @Override
    public void open(String path) throws IOException {
        scanner = new Scanner(new FileReader(path));
    }

    @Override
    public int readLevel() throws IOException {
        int lvl = scanner.nextInt();
        String line1 = scanner.nextLine();
        return lvl;
    }

    @Override
    public Edge readEdge() throws IOException {
        String[] parts = scanner.nextLine().split(" ");
        double x1 = Double.parseDouble(parts[0]);
        double y1 = Double.parseDouble(parts[1]);
        double x2 = Double.parseDouble(parts[2]);
        double y2 = Double.parseDouble(parts[3]);
        double red = Double.parseDouble(parts[4]);
        double green = Double.parseDouble(parts[5]);
        double blue = Double.parseDouble(parts[6]);

        return new Edge(x1, y1, x2, y2, new Color(red, green, blue, 1));
    }

    @Override
    public void close() {
        scanner.close();
    }
}
