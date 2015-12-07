package readers;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class TextBuffered extends TextNoBuffer {
    @Override
    public void open(String path) throws IOException {
        new Scanner(new BufferedInputStream(new FileInputStream(path)));
    }
}
