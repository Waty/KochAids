package com.waty.writers;

import com.waty.calculate.Edge;

import java.io.FileWriter;
import java.io.IOException;

public class TextNoBuffer extends Writer {

    private FileWriter fileWriter;

    @Override
    public void open(String path) throws IOException {
        fileWriter = new FileWriter(path);
    }

    @Override
    public void appendEdge(Edge e) throws IOException {
        fileWriter.write(e.toString() + "\n");
    }

    @Override
    public void close() throws IOException {
        fileWriter.close();
    }
}
