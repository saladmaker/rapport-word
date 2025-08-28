package rpp.poi.model;

import java.math.BigInteger;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STSectionMark;

public class PageLayoutManager {
    private final XWPFDocument doc;
    private final LanguageDirection direction; // immutable
    private PageLayout currentLayout = PageLayout.PORTRAIT;

    public PageLayoutManager(XWPFDocument doc, LanguageDirection direction) {
        this.doc = doc;
        this.direction = direction;
    }

    public void apply(PageLayout layout) {
        if (layout == currentLayout)
            return;

        CTBody body = doc.getDocument().getBody();
        CTSectPr sectPr = body.isSetSectPr() ? body.getSectPr() : body.addNewSectPr();

        // Add a section break (copy old sectPr)
        XWPFParagraph breaker = doc.createParagraph();
        CTPPr ppr = breaker.getCTP().isSetPPr() ? breaker.getCTP().getPPr() : breaker.getCTP().addNewPPr();
        CTSectPr prevSectPr = ppr.isSetSectPr() ? ppr.getSectPr() : ppr.addNewSectPr();
        prevSectPr.set(sectPr.copy());

        // Apply new layout
        CTPageSz sz = sectPr.isSetPgSz() ? sectPr.getPgSz() : sectPr.addNewPgSz();
        sz.setW(layout.width);
        sz.setH(layout.height);
        sz.setOrient(layout.orientation);

        CTPageMar mar = sectPr.isSetPgMar() ? sectPr.getPgMar() : sectPr.addNewPgMar();
        mar.setTop(layout.margin);
        mar.setBottom(layout.margin);
        mar.setLeft(layout.margin);
        mar.setRight(layout.margin);

        CTSectType type = sectPr.isSetType() ? sectPr.getType() : sectPr.addNewType();
        type.setVal(STSectionMark.NEXT_PAGE);

        currentLayout = layout;
    }

    public BigInteger getScaledUsableWidth(double factor) {
        return currentLayout.scaledWidth(factor);
    }

    public LanguageDirection getDirection() {
        return direction;
    }
}
