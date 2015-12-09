package com.waty.readers;

import com.waty.calculate.Edge;
import com.waty.calculate.KochFractal;
import com.waty.writers.IWriter;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TextBufferedTest {
    @Test
    public void test() {
        List<Edge> edges = new ArrayList<>();
        KochFractal kf = new KochFractal();
        kf.setLevel(5);
        kf.addObserver((observable1, o1) -> edges.add((Edge) o1));
        kf.generateRightEdge();
        kf.generateLeftEdge();
        kf.generateBottomEdge();

        for (int i = 0; i < IWriter.writers.length; ++i) {
            try (IWriter writer = IWriter.writers[i]) {
                writer.open("test");
                writer.writeLevel(5);
                for (Edge edge : edges) writer.appendEdge(edge);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try (IReader reader = IReader.readers[i]) {
                reader.open("test");
                assertEquals(5, reader.readLevel());
                for (Edge edge : edges) assertEquals(edge, reader.readEdge());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}