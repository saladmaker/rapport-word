package rpp.poi.model;

import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblLook;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STSectionMark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

public class GenerationContext {

    private final XWPFDocument doc;
    private final LanguageDirection direction; // immutable
    private PageLayout currentLayout = PageLayout.PORTRAIT;
    private final Map <String,String> config;

    public GenerationContext(XWPFDocument doc, Map<String,String> config, LanguageDirection direction) {
        this.doc = doc;
        this.config = Map.copyOf(config);
        this.direction = direction;
    }

    public Optional<String> optionalText(String key){
        var value = config.get(key);
        if(null == value){
            return Optional.empty();
        }
        return Optional.of(contextualize(value));
    }
    public String contextualizedContent(String key){
        return contextualize(plainContent(key));
    }
    public String plainContent(String key){
         var value = config.get(key);
        if(null == value) throw new IllegalStateException("value doesn't exist \"" + key + "\"");
        return value; 
    }
    public void apply(PageLayout layout) {
        if (layout == currentLayout) {
            return;
        }

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

    public void applyTableStyle(XWPFTable table, String styleKey) {
        var styleId =  plainContent(styleKey);
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        if (tblPr == null) {
            tblPr = table.getCTTbl().addNewTblPr();
        }

        // Apply style
        tblPr.addNewTblStyle().setVal(styleId);

        // Table look
        CTTblLook look = tblPr.isSetTblLook() ? tblPr.getTblLook() : tblPr.addNewTblLook();
        look.setFirstRow(true);
        look.setLastRow(true);
        look.setFirstColumn(true);
        look.setLastColumn(true);
        look.setNoHBand(false);
        look.setNoVBand(false);

        // Fixed layout
        CTTblLayoutType layoutType = tblPr.isSetTblLayout() ? tblPr.getTblLayout() : tblPr.addNewTblLayout();
        layoutType.setType(STTblLayoutType.FIXED);

        // Dynamic width: 96% of usable width
        CTTblWidth tblWidth = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();
        tblWidth.setW(getScaledUsableWidth(0.96));
        tblWidth.setType(STTblWidth.DXA);

        // --- Direction from PageLayoutManager
        if (getDirection() == LanguageDirection.RTL) {
            tblPr.addNewBidiVisual().setVal(STOnOff1.ON);
        } else {
            tblPr.addNewBidiVisual().setVal(STOnOff1.OFF);
        }
    }
    public BigInteger getScaledUsableWidth(double factor) {
        return currentLayout.scaledWidth(factor);
    }

    public LanguageDirection getDirection() {
        return direction;
    }

    public String contextualize(String text){
        if(requireRTL(text)){
            return text + "\u061C";
        }
        return text;
    }
    private boolean requireRTL(String text) {
        if (text == null || text.isEmpty() || (direction == LanguageDirection.LTR)) {
            return false;
        }

        // Get last code point of the string
        int cp = text.codePointBefore(text.length());
        byte dir = Character.getDirectionality(cp);

        // Check if it's neutral according to Unicode bidi
        return switch (dir) {
            case // , . / :
            Character.DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR, // + - 
            Character.DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR, // . , 
            Character.DIRECTIONALITY_COMMON_NUMBER_SEPARATOR, // quotes, (), …
            Character.DIRECTIONALITY_OTHER_NEUTRALS ->
                true;
            default ->
                false;
        };
    }

}
