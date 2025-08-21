package rpp.poi.playground.generation;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import rpp.poi.playground.DocumentGenerator;

public class ParagraphStyles_1 implements DocumentGenerator {

    private static final String[] HEADING_TEXTS = {
            "Introduction", "Methodology", "Results", "Discussion", "Conclusion"
    };

    private static final String[] NORMAL_TEXTS = {
            "This section contains important information.",
            "The data shows significant trends.",
            "Further research is needed in this area.",
            "These findings align with previous studies.",
            "The results demonstrate clear patterns."
    };

    @Override
    public void generate(InputStream theme, OutputStream out, String... args) throws Exception {
        try (XWPFDocument doc = new XWPFDocument(theme)) {
            addRandomContent(doc);
            doc.write(out);
        }
    }

    private void addRandomContent(XWPFDocument document) {
        Random random = new Random();
        int sections = 3 + random.nextInt(3); // 3-5 sections

        for (int i = 0; i < sections; i++) {
            // Add Heading1
            XWPFParagraph heading1 = document.createParagraph();
            heading1.setStyle("Heading1");
            heading1.createRun().setText(HEADING_TEXTS[random.nextInt(HEADING_TEXTS.length)]);

            // Add some normal text
            addNormalText(document, 1 + random.nextInt(3));

            // Add Heading2
            XWPFParagraph heading2 = document.createParagraph();
            heading2.setStyle("Heading2");
            heading2.createRun().setText(HEADING_TEXTS[random.nextInt(HEADING_TEXTS.length)]);

            // Add some normal text
            addNormalText(document, 1 + random.nextInt(3));

            // Add Heading3
            XWPFParagraph heading3 = document.createParagraph();
            heading3.setStyle("Heading3");
            heading3.createRun().setText(HEADING_TEXTS[random.nextInt(HEADING_TEXTS.length)]);

            // Add some normal text
            addNormalText(document, 1 + random.nextInt(3));

            // Add Heading4
            XWPFParagraph heading4 = document.createParagraph();
            heading4.setStyle("Heading4");
            heading4.createRun().setText(HEADING_TEXTS[random.nextInt(HEADING_TEXTS.length)]);

            // Add some normal text
            addNormalText(document, 1 + random.nextInt(3));
        }
    }

    private void addNormalText(XWPFDocument document, int paragraphs) {
        Random random = new Random();
        for (int i = 0; i < paragraphs; i++) {
            XWPFParagraph paragraph = document.createParagraph();
            paragraph.setStyle("Normal");
            paragraph.createRun().setText(NORMAL_TEXTS[random.nextInt(NORMAL_TEXTS.length)]);
        }
    }
}
