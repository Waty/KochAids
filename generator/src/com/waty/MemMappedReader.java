package com.waty;

import com.waty.calculate.Edge;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class MemMappedReader implements AutoCloseable {
    private static final int READ_SIZE = 7 * Double.BYTES;
    private RandomAccessFile file;
    private MappedByteBuffer buffer;
    private FileChannel channel;

    public MemMappedReader(String path) throws IOException {
        file = new RandomAccessFile(path, "r");
        channel = file.getChannel();
        buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
    }

    public int readLevel() throws IOException {
        try (FileLock ignored = channel.lock(0, Integer.BYTES, true)) {
            return buffer.getInt();
        }
    }

    public Edge readEdge() throws IOException {
        try (FileLock ignored = channel.lock(buffer.position(), READ_SIZE, true)) {
            double x1 = buffer.getDouble();
            double y1 = buffer.getDouble();
            double x2 = buffer.getDouble();
            double y2 = buffer.getDouble();

            double red = buffer.getDouble();
            double green = buffer.getDouble();
            double blue = buffer.getDouble();

            return new Edge(x1, y1, x2, y2, new Color(red, green, blue, 1));
        }
    }

    @Override
    public void close() throws IOException {
        channel.close();
        file.close();
    }
}
