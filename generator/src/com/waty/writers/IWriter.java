package com.waty.writers;

import com.waty.calculate.Edge;

import java.io.IOException;

public interface IWriter extends AutoCloseable {
    IWriter[] writers = new IWriter[]{new TextNoBuffer(), new BinaryNoBuffer(), new TextBuffered(), new BinaryBuffered(), new BinaryMemoryMapped()};

    void open(String path) throws IOException;

    void setLevel(int lvl) throws IOException;

    void appendEdge(Edge e) throws IOException;

    void close() throws IOException;
}

