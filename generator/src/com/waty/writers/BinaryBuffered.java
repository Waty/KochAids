package com.waty.writers;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class BinaryBuffered extends BinaryNoBuffer {

    @Override
    public void open(String path) throws IOException {
        oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(path)));
    }
}
