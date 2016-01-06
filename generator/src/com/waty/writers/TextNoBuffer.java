package com.waty.writers;

import com.waty.calculate.Edge;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class TextNoBuffer implements IWriter {

    Writer fw;

    @Override
    public void open(String path) throws IOException {
        fw = new FileWriter(path);
    }

    @Override
    public void setLevel(int lvl) throws IOException {
        fw.write(lvl + System.lineSeparator());
    }

    @Override
    public void appendEdge(Edge e) throws IOException {
        fw.write(e.toString() + System.lineSeparator());
    }

    @Override
    public void close() throws IOException {
        fw.close();
    }
}
