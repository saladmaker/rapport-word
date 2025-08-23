package rpp.poi.model;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;

import java.math.BigInteger;

public interface Writable {

    void write(XWPFDocument document);

    /**
     * Adds text to the paragraph, preserving manual line breaks.
     */
    default void addParagraphWithManualBreaks(XWPFParagraph paragraph, String text) {
        XWPFRun run = paragraph.createRun();
        String[] lines = text.split("\n", -1); // keep empty lines
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) {
                run.addBreak(); // manual line break
            }
            run.setText(lines[i]);
        }
    }

    /**
     * Ensures the page layout matches the desired orientation (A4 + 2cm margins).
     * If current orientation differs, adds a new section break with the desired settings.
     */
    default void ensureOrientation(XWPFDocument document, STPageOrientation.Enum desired) {
        CTSectPr lastSectPr = document.getDocument().getBody().getSectPr();
        STPageOrientation.Enum current = STPageOrientation.PORTRAIT; // default
        if (lastSectPr != null && lastSectPr.isSetPgSz()) {
            current = lastSectPr.getPgSz().getOrient();
        }

        if (current != desired) {
            XWPFParagraph sectionBreak = document.createParagraph();
            CTSectPr sectPr = sectionBreak.getCTP().addNewPPr().addNewSectPr();

            // Set A4 size
            CTPageSz pageSize = sectPr.addNewPgSz();
            if (desired == STPageOrientation.LANDSCAPE) {
                pageSize.setW(BigInteger.valueOf(16840)); // width in twips
                pageSize.setH(BigInteger.valueOf(11900)); // height in twips
            } else {
                pageSize.setW(BigInteger.valueOf(11900));
                pageSize.setH(BigInteger.valueOf(16840));
            }
            pageSize.setOrient(desired);

            // Margins: 2cm ≈ 1134 twips
            CTPageMar pageMar = sectPr.addNewPgMar();
            BigInteger margin = BigInteger.valueOf(1134);
            pageMar.setTop(margin);
            pageMar.setBottom(margin);
            pageMar.setLeft(margin);
            pageMar.setRight(margin);
        }
    }
}

