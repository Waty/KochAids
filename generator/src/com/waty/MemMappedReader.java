package com.waty;

import com.waty.calculate.Edge;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import static com.waty.MemMappedWriter.*;

public class MemMappedReader implements AutoCloseable {
    private RandomAccessFile file;
    private MappedByteBuffer buffer;
    private FileChannel channel;

    public MemMappedReader(String path) throws IOException {
        file = new RandomAccessFile(path, "r");
        channel = file.getChannel();
        long size = channel.size();
        if (size < DATA_BEGIN_POS) size = DATA_BEGIN_POS;
        buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, size);
        buffer.position(DATA_BEGIN_POS);
    }

    public int getLevel() throws IOException {
        try (FileLock ignored = channel.lock(LEVEL_POS, Integer.BYTES, true)) {
            int lvl = buffer.getInt(LEVEL_POS);
            buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, fileSize(lvl));
            buffer.position(DATA_BEGIN_POS);
            return lvl;
        }
    }

    public boolean canReadEdge() throws IOException {
        try (FileLock ignored = channel.lock(MAX_READ_POS, Integer.SIZE, true)) {
            return buffer.position() + WRITE_SIZE <= buffer.getInt(MAX_READ_POS);
        }
    }

    public Edge readEdge() throws IOException {
        while (!canReadEdge()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {
            }
        }

        try (FileLock ignored = channel.lock(buffer.position(), WRITE_SIZE, true)) {
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
