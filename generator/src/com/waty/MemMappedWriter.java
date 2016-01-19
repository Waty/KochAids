package com.waty;

import com.waty.calculate.Edge;
import com.waty.calculate.KochFractal;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class MemMappedWriter implements AutoCloseable {
    static final int WRITE_SIZE = Double.BYTES * 7;

    public long NUMBER_OF_BYTES;
    private RandomAccessFile file;
    private MappedByteBuffer buffer;
    private FileChannel channel;

    public MemMappedWriter(String path) throws IOException {
        file = new RandomAccessFile(path, "rwd");
    }

    public void setLevel(int lvl) throws IOException {
        KochFractal kf = new KochFractal();
        kf.setLevel(lvl);

        NUMBER_OF_BYTES = Integer.BYTES + (kf.getNrOfEdges() * WRITE_SIZE);

        //Mapping a file into memory
        channel = file.getChannel();
        buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, NUMBER_OF_BYTES);
        try (FileLock ignored = channel.lock(0, Integer.BYTES, false)) {
            buffer.putInt(lvl);
        }
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
    }

    @Override
    public void close() throws IOException {
        channel.close();
        file.close();
    }
}
