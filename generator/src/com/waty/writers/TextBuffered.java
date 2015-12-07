package com.waty.writers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TextBuffered extends TextNoBuffer {

    @Override
    public void open(String path) throws IOException {
        fw = new BufferedWriter(new FileWriter(path));
    }
}
