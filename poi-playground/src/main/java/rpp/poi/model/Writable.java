package rpp.poi.model;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

public interface Writable {

    String PARAGRAPH_DEFAULT_STYLE_KEY = "paragraph.default.style";
    String HEADING_1_STYLE_KEY = "heading1.style";
    String HEADING_2_STYLE_KEY = "heading2.style";
    String HEADING_3_STYLE_KEY = "heading3.style";
    String MULTI_LEVEL_LIST_KEY = "multilevel.list.id";
    String LONG_PARAGRAPH_STYLE_KEY = "paragraph.text.style";
    String BOLD_TEXT_STYLE_KEY = "bold.text.style";
    String IMG_STYLE_KEY = "image.style";
    String STICKY_TITLE_STYLE_KEY = "sticky.title.style";

    void write(XWPFDocument document, GenerationContext context);

}