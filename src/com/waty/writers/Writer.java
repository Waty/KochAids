package com.waty.writers;

import com.waty.calculate.Edge;

import java.io.IOException;

/**
 * Created by warta on 02-Dec-15.
 */
public abstract class Writer implements AutoCloseable{
    public static Writer[] writers = new Writer[]{new TextNoBuffer()};

    public abstract void open(String path) throws IOException;

    public abstract void appendEdge(Edge e) throws IOException;

    public abstract void close() throws IOException;
}

