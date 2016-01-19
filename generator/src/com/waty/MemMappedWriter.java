package com.waty;

import com.waty.calculate.Edge;
import com.waty.calculate.KochFractal;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class MemMappedWriter implements AutoCloseable {
    public static final int LEVEL_POS = 0;
    public static final int MAX_READ_POS = 4;
    public static final int DATA_BEGIN_POS = 8;

    public static final int WRITE_SIZE = Double.BYTES * 7;
    private RandomAccessFile file;
    private MappedByteBuffer buffer;
    private FileChannel channel;

    public MemMappedWriter(String path) throws IOException {
        file = new RandomAccessFile(path, "rw");
        channel = file.getChannel();
        buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, DATA_BEGIN_POS);
    }

    public static long fileSize(int level) {
        KochFractal kf = new KochFractal();
        kf.setLevel(level);
        return DATA_BEGIN_POS + (kf.getNrOfEdges() * WRITE_SIZE);
    }

    public void setLevel(int lvl) throws IOException {

        //Mapping a file into memory
        buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, fileSize(lvl));
        try (FileLock ignored = channel.lock(LEVEL_POS, Integer.BYTES * 2, false)) {
            buffer.putInt(LEVEL_POS, lvl);
            buffer.putInt(MAX_READ_POS, Integer.BYTES * 2);
        }

        buffer.position(DATA_BEGIN_POS);
    }

    public void appendEdge(Edge e) throws IOException {
        // the try is to make sure the lock is always released
        try (FileLock ignored = channel.lock(buffer.position(), WRITE_SIZE, false)) {
            buffer.putDouble(e.X1);
            buffer.putDouble(e.Y1);
            buffer.putDouble(e.X2);
            buffer.putDouble(e.Y2);
            buffer.putDouble(e.color.getRed());
            buffer.putDouble(e.color.getGreen());
            buffer.putDouble(e.color.getBlue());

        }

        //update max read pos
        try (FileLock ignored = channel.lock(MAX_READ_POS, Integer.BYTES, false)) {
            buffer.putInt(MAX_READ_POS, buffer.position());
        }
    }

    @Override
    public void close() throws IOException {
        channel.close();
        file.close();
    }
}
