package com.waty.readers;

import com.waty.calculate.Edge;

import java.io.IOException;

public interface IReader extends AutoCloseable {
    IReader[] readers = new IReader[]{new TextNoBuffer(), new BinaryNoBuffer(), new TextBuffered(), new BinaryBuffered(), new BinaryMemoryMapped()};

    void open(String path) throws IOException;

    int readLevel() throws IOException;

    Edge readEdge() throws IOException;

    void close() throws IOException;
}
