package readers;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class BinaryBuffered extends BinaryNoBuffer {

    @Override
    public void open(String path) throws IOException {
        ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(path)));
    }
}
