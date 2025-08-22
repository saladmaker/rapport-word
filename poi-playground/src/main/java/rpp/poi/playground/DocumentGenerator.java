package rpp.poi.playground;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

public interface DocumentGenerator {
    void generate(XWPFDocument document) throws Exception;
}
