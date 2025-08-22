package rpp.poi.playground.generation;

import java.math.BigInteger;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STSectionMark;

import rpp.poi.playground.DocumentGenerator;

public class SectionLayout_3 implements DocumentGenerator {

    @Override
    public void generate(XWPFDocument document) throws Exception {

        // reusing my old tests to simulate an actual rpp
        var pargraphStyle = new ParagraphStyles_1();
        var tableStyle = new TableStyles_2();
        pargraphStyle.generate(document);
        tableStyle.generate(document);
        tableStyle.generate(document);
        tableStyle.generate(document);
        pargraphStyle.generate(document);
        insertSectionBreakA4Landscape2cm(document);
        pargraphStyle.generate(document);
        pargraphStyle.generate(document);
    }

    public static void insertSectionBreakA4Landscape2cm(XWPFDocument document) {
        final int TWIPS_PER_CM = 567; // 1 cm ≈ 567 twips
        final int MARGIN_2CM = 2 * TWIPS_PER_CM; // 1134 twips

        // A4 dimensions in twips (portrait); landscape = swap
        final int A4_W_PORTRAIT = 11906;
        final int A4_H_PORTRAIT = 16838;
        final int A4_W_LANDSCAPE = A4_H_PORTRAIT; // 16838
        final int A4_H_LANDSCAPE = A4_W_PORTRAIT; // 11906

        // Get or create body-level sectPr (represents the last/current section)
        CTBody body = document.getDocument().getBody();
        CTSectPr bodySectPr = body.isSetSectPr() ? body.getSectPr() : body.addNewSectPr();

        // Insert a paragraph that *closes* the current section:
        XWPFParagraph breakPara = document.createParagraph();
        CTP p = breakPara.getCTP();
        CTPPr pPr = p.isSetPPr() ? p.getPPr() : p.addNewPPr();

        // Create paragraph-level sectPr and copy existing body sectPr into it
        CTSectPr paraSectPr = pPr.isSetSectPr() ? pPr.getSectPr() : pPr.addNewSectPr();
        paraSectPr.set(bodySectPr);

        // Set this paragraph-level type to NEXT_PAGE (no CTSectPr.Type class — use
        // getType()/addNewType())
        if (paraSectPr.isSetType()) {
            paraSectPr.getType().setVal(STSectionMark.NEXT_PAGE);
        } else {
            paraSectPr.addNewType().setVal(STSectionMark.NEXT_PAGE);
        }

        // Now change the BODY sectPr to describe the new following section (A4
        // landscape, 2cm margins)
        CTPageSz pageSz = bodySectPr.isSetPgSz() ? bodySectPr.getPgSz() : bodySectPr.addNewPgSz();
        pageSz.setW(BigInteger.valueOf(A4_W_LANDSCAPE));
        pageSz.setH(BigInteger.valueOf(A4_H_LANDSCAPE));
        pageSz.setOrient(STPageOrientation.LANDSCAPE);

        CTPageMar pageMar = bodySectPr.isSetPgMar() ? bodySectPr.getPgMar() : bodySectPr.addNewPgMar();
        BigInteger m = BigInteger.valueOf(MARGIN_2CM);
        pageMar.setTop(m);
        pageMar.setBottom(m);
        pageMar.setLeft(m);
        pageMar.setRight(m);
    }

}
