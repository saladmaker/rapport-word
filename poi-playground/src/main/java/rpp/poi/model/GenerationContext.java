package rpp.poi.model;

import java.util.Map;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class GenerationContext {
    XWPFDocument document;
    PageLayoutManager layoutManager;
    Map<String,String> config;

    public GenerationContext(Map<String, String> config, XWPFDocument document, PageLayoutManager layoutManager) {
        this.config = config;
        this.document = document;
        this.layoutManager = layoutManager;
    }
    
}
