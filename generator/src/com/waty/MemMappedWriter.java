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
    private RandomAccessFile memoryMappedFile;
    private MappedByteBuffer out;
    private FileChannel fc;

    public MemMappedWriter(String path) throws IOException {
        memoryMappedFile = new RandomAccessFile(path, "rwd");
    }

    public void setLevel(int lvl) throws IOException {
        KochFractal kf = new KochFractal();
        kf.setLevel(lvl);

        NUMBER_OF_BYTES = Integer.BYTES + (kf.getNrOfEdges() * WRITE_SIZE);

        //Mapping a file into memory
        fc = memoryMappedFile.getChannel();
        out = fc.map(FileChannel.MapMode.READ_WRITE, 0, NUMBER_OF_BYTES);
        try (FileLock ignored = fc.lock(0, Integer.BYTES, false)) {
            out.putInt(lvl);
        }
    }

    public void appendEdge(Edge e) throws IOException {
        // the try is to make sure the lock is always released
        try (FileLock ignored = fc.lock(out.position(), WRITE_SIZE, false)) {
            out.putDouble(e.X1);
            out.putDouble(e.Y1);
            out.putDouble(e.X2);
            out.putDouble(e.Y2);
            out.putDouble(e.color.getRed());
            out.putDouble(e.color.getGreen());
            out.putDouble(e.color.getBlue());
        }
    }

    @Override
    public void close() throws IOException {
        memoryMappedFile.close();
    }
}
