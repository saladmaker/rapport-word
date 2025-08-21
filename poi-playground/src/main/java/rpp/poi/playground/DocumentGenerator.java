package rpp.poi.playground;

import java.io.InputStream;
import java.io.OutputStream;

public interface DocumentGenerator {
    void generate(InputStream theme, OutputStream out, String... args) throws Exception;
}
