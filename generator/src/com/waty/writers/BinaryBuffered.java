package com.waty.writers;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class BinaryBuffered extends BinaryNoBuffer {

    @Override
    public void open(String path) throws IOException {
        oos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(path)));
    }
}
