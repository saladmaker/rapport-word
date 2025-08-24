package rpp.poi.model;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STSectionMark;

import java.math.BigInteger;
import java.util.Objects;

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
    default void ensureOrientation(XWPFDocument doc, STPageOrientation.Enum desired) {
        Objects.requireNonNull(doc, "doc");

        CTBody body = doc.getDocument().getBody();
        CTSectPr finalSectPr = body.isSetSectPr() ? body.getSectPr() : body.addNewSectPr();

        STPageOrientation.Enum current = readOrientation(finalSectPr);

        if (current == null || !current.equals(desired)) {
            // copy old sectPr to a paragraph sectPr = closes current section
            XWPFParagraph breaker = doc.createParagraph();
            CTPPr ppr = breaker.getCTP().isSetPPr() ? breaker.getCTP().getPPr() : breaker.getCTP().addNewPPr();
            CTSectPr prevSectPr = ppr.isSetSectPr() ? ppr.getSectPr() : ppr.addNewSectPr();
            if (body.isSetSectPr()) {
                prevSectPr.set(finalSectPr.copy());
            }

            // apply new orientation to the final section
            applyA4WithMargins(finalSectPr, desired);

            // section break type
            CTSectType type = finalSectPr.isSetType() ? finalSectPr.getType() : finalSectPr.addNewType();
            type.setVal(STSectionMark.NEXT_PAGE);
        }
    }

    private static STPageOrientation.Enum readOrientation(CTSectPr sectPr) {
        if (sectPr != null && sectPr.isSetPgSz()) {
            CTPageSz sz = sectPr.getPgSz();
            STPageOrientation.Enum o = sz.getOrient();
            if (o != null) return o;
            if (sz.isSetW() && sz.isSetH()) {
                return ((Comparable)sz.getW()).compareTo((Comparable)sz.getH()) > 0
                        ? STPageOrientation.LANDSCAPE
                        : STPageOrientation.PORTRAIT;
            }
        }
        return STPageOrientation.PORTRAIT;
    }

    private static void applyA4WithMargins(CTSectPr sectPr, STPageOrientation.Enum desired) {
        final BigInteger A4_W = BigInteger.valueOf(11906); // portrait width
        final BigInteger A4_H = BigInteger.valueOf(16838); // portrait height
        final BigInteger MARGIN_2CM = BigInteger.valueOf(1134);

        CTPageSz sz = sectPr.isSetPgSz() ? sectPr.getPgSz() : sectPr.addNewPgSz();
        if (desired == STPageOrientation.LANDSCAPE) {
            sz.setW(A4_H); // swap w/h
            sz.setH(A4_W);
            sz.setOrient(STPageOrientation.LANDSCAPE);
        } else {
            sz.setW(A4_W);
            sz.setH(A4_H);
            sz.setOrient(STPageOrientation.PORTRAIT);
        }

        CTPageMar mar = sectPr.isSetPgMar() ? sectPr.getPgMar() : sectPr.addNewPgMar();
        mar.setTop(MARGIN_2CM);
        mar.setBottom(MARGIN_2CM);
        mar.setLeft(MARGIN_2CM);
        mar.setRight(MARGIN_2CM);
    }

}