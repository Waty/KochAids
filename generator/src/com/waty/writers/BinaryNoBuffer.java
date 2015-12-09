package com.waty.writers;

import com.waty.calculate.Edge;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class BinaryNoBuffer implements IWriter {
    DataOutputStream oos;

    @Override
    public void open(String path) throws IOException {
        oos = new DataOutputStream(new FileOutputStream(path));
    }

    @Override
    public void writeLevel(int lvl) throws IOException {
        oos.writeInt(lvl);
    }

    @Override
    public void appendEdge(Edge e) throws IOException {
        oos.writeDouble(e.X1);
        oos.writeDouble(e.Y1);
        oos.writeDouble(e.X2);
        oos.writeDouble(e.Y2);
        oos.writeDouble(e.color.getRed());
        oos.writeDouble(e.color.getGreen());
        oos.writeDouble(e.color.getBlue());
    }

    @Override
    public void close() throws IOException {
        oos.close();
    }
}
