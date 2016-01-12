package com.waty.readers;

import com.waty.calculate.Edge;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class BinaryMemoryMapped implements IReader {
    private RandomAccessFile file;
    private MappedByteBuffer buffer;

    @Override
    public void open(String path) throws IOException {
        file = new RandomAccessFile(path, "rw");
        buffer = file.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
    }

    @Override
    public int readLevel() throws IOException {
        return buffer.getInt();
    }

    @Override
    public Edge readEdge() throws IOException {
        double x1 = buffer.getDouble();
        double y1 = buffer.getDouble();
        double x2 = buffer.getDouble();
        double y2 = buffer.getDouble();

        double red = buffer.getDouble();
        double green = buffer.getDouble();
        double blue = buffer.getDouble();

        return new Edge(x1, y1, x2, y2, new Color(red, green, blue, 1));
    }

    @Override
    public void close() throws IOException {
        file.close();
    }
}
