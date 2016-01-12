package com.waty.readers;

import com.waty.calculate.Edge;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class BinaryNoBuffer implements IReader {
    ObjectInputStream ois;

    @Override
    public void open(String path) throws IOException {
        ois = new ObjectInputStream(new FileInputStream(path));
    }

    @Override
    public int readLevel() throws IOException {
        return ois.readInt();
    }

    @Override
    public Edge readEdge() throws IOException {
        double x1 = ois.readDouble();
        double y1 = ois.readDouble();
        double x2 = ois.readDouble();
        double y2 = ois.readDouble();

        double red = ois.readDouble();
        double green = ois.readDouble();
        double blue = ois.readDouble();

        return new Edge(x1, y1, x2, y2, new Color(red, green, blue, 1));
    }

    @Override
    public void close() throws IOException {
        ois.close();
    }
}
