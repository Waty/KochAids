package com.waty;

import com.waty.calculate.Edge;
import javafx.scene.paint.Color;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class MemMappedTest {
    private static final String PATH = "unittest.data";

    @Before
    public void setUp() throws Exception {
        new File(PATH).deleteOnExit();
    }

    @Test
    public void readWriteTest() {

        try (MemMappedWriter writer = new MemMappedWriter(PATH);
             MemMappedReader reader = new MemMappedReader(PATH)) {
            writer.setLevel(6);
            assertEquals(6, reader.getLevel());

            assertFalse(reader.canReadEdge());
            Edge e = new Edge(0.1, 0.2, 0.3, 0.4, Color.BLACK);
            writer.appendEdge(e);
            assertTrue(reader.canReadEdge());
            assertEquals(e, reader.readEdge());
            assertFalse(reader.canReadEdge());

        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}