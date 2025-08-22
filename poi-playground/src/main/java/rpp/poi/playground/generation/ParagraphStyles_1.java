package rpp.poi.playground.generation;

import java.util.Random;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import rpp.poi.playground.DocumentGenerator;

public class ParagraphStyles_1 implements DocumentGenerator {

    private static final String[] HEADING_TEXTS = {
            "Introduction", "Methodology", "Results", "Discussion", "Conclusion"
    };

    private static final String[] NORMAL_TEXTS = {
            """
                    This section contains important information.
                    It should be read carefully, as it provides
                    essential background and context for the
                    remainder of the document.
                    """,
            """
                    The data shows significant trends across
                    multiple years. In particular, note the
                    steady increase in adoption rates.

                    However, several anomalies appear in 2020
                    that require further explanation.
                    """,
            """
                    Further research is needed in this area.
                    Preliminary studies suggest promising
                    outcomes, but larger sample sizes
                    are required to validate these results.
                    """,
            """
                    These findings align with previous studies,
                    reinforcing the reliability of the conclusions.
                    Still, additional replication in different
                    environments would strengthen the case.
                    """,
            """
                    The results demonstrate clear patterns.
                    Most notably, Group A consistently
                    outperformed Group B.

                    This consistency suggests that the
                    intervention was effective.
                    """
    };

    @Override
    public void generate(XWPFDocument doc) throws Exception {
        addRandomContent(doc);
    }

    private void addRandomContent(XWPFDocument document) {
        Random random = new Random();
        int sections = 3 + random.nextInt(3);

        for (int i = 0; i < sections; i++) {

            XWPFParagraph heading1 = document.createParagraph();
            heading1.setStyle("Heading1");
            heading1.createRun().setText(HEADING_TEXTS[random.nextInt(HEADING_TEXTS.length)]);

            addNormalText(document, 1 + random.nextInt(3));

            XWPFParagraph heading2 = document.createParagraph();
            heading2.setStyle("Heading2");
            heading2.createRun().setText(HEADING_TEXTS[random.nextInt(HEADING_TEXTS.length)]);

            addNormalText(document, 1 + random.nextInt(3));

            XWPFParagraph heading3 = document.createParagraph();
            heading3.setStyle("Heading3");
            heading3.createRun().setText(HEADING_TEXTS[random.nextInt(HEADING_TEXTS.length)]);

            addNormalText(document, 1 + random.nextInt(3));

            XWPFParagraph heading4 = document.createParagraph();
            heading4.setStyle("Heading4");
            heading4.createRun().setText(HEADING_TEXTS[random.nextInt(HEADING_TEXTS.length)]);

            addNormalText(document, 1 + random.nextInt(3));
        }
    }

    private void addNormalText(XWPFDocument document, int paragraphs) {
        Random random = new Random();
        for (int i = 0; i < paragraphs; i++) {

            XWPFParagraph paragraph = document.createParagraph();
            paragraph.setStyle("Normal");

            String text = NORMAL_TEXTS[random.nextInt(NORMAL_TEXTS.length)];

            addParagraphWithManualBreaks(paragraph, text);
        }
    }

    /**
     * Adds text with manual line breaks (Shift+Enter) into the given paragraph.
     *
     * @param paragraph The paragraph to add the text to
     * @param text      The text to insert, lines separated by '\n'
     */
    public static void addParagraphWithManualBreaks(XWPFParagraph paragraph, String text) {
        XWPFRun run = paragraph.createRun();

        String[] lines = text.split("\n", -1); // keep empty lines too
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) {
                run.addBreak(); // Shift+Enter equivalent
            }
            run.setText(lines[i]);
        }
    }
}
