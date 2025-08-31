package rpp.poi.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
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
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHdrFtr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STSectionMark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

public class GenerationContext {

    static final String FOOTER_TEXT_KEY = "footer.text";
    static final String FOOTER_TEXT_STYLE_KEY = "bold.text.style";

    private final XWPFDocument doc;
    private final LanguageDirection direction; // immutable
    private PageLayout currentLayout = PageLayout.PORTRAIT;
    private final Map<String, String> config;

    public GenerationContext(XWPFDocument doc, Map<String, String> config, LanguageDirection direction) {
        this.doc = doc;
        this.config = Map.copyOf(config);
        this.direction = direction;
        var s = contextualizedContent(FOOTER_TEXT_KEY);
        addFooterWithTextAndPageNumber(doc, s);
    }

    public Optional<String> optionalText(String key) {
        var value = config.get(key);
        if (null == value) {
            return Optional.empty();
        }
        return Optional.of(contextualize(value));
    }

    public String contextualizedContent(String key) {
        return contextualize(plainContent(key));
    }

    public String plainContent(String key) {
        var value = config.get(key);
        if (null == value)
            throw new IllegalStateException("value doesn't exist \"" + key + "\"");
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

    public <T> XWPFTable writeTable(
            XWPFDocument document,
            String tableStyleKey,
            String headerPrefix,
            List<T> rows,
            List<Function<? super T, String>> extractors,
            boolean addTotalRow,
            boolean addTotalColumn) {
        // ---- 1. Collect headers dynamically ----
        List<String> headers = new ArrayList<>();
        int idx = 0;
        while (true) {
            Optional<String> opt = optionalText(headerPrefix + idx);
            if (opt.isEmpty()) {
                break;
            }
            headers.add(opt.get());
            idx++;
        }

        // ---- 2. Validation ----
        if (headers.size() != extractors.size()) {
            throw new IllegalArgumentException(
                    "Headers count (" + headers.size() +
                            ") does not match extractors count (" + extractors.size() + ")");
        }

        // ---- 3. Compute table dimensions ----
        int rowCount = rows.size() + 1; // +1 for header
        if (addTotalRow) {
            rowCount++;
        }

        int colCount = headers.size();
        if (addTotalColumn) {
            colCount++;
        }

        // ---- 4. Create table with exact size ----
        XWPFTable table = document.createTable(rowCount, colCount);
        applyTableStyle(table, tableStyleKey);

        // ---- 5. Fill Header Row ----
        XWPFTableRow headerRow = table.getRow(0);
        for (int i = 0; i < headers.size(); i++) {
            headerRow.getCell(i).setText(headers.get(i));
        }
        // leave extra total column cell empty if addTotalColumn == true

        // ---- 6. Fill Data Rows ----
        int rowIndex = 1;
        for (T row : rows) {
            XWPFTableRow tableRow = table.getRow(rowIndex);
            for (int col = 0; col < extractors.size(); col++) {
                String text = extractors.get(col).apply(row);
                tableRow.getCell(col).setText(contextualize(text));
            }
            // leave extra total column cell empty if addTotalColumn == true
            rowIndex++;
        }

        return table;
    }

    public void applyTableStyle(XWPFTable table, String styleKey) {
        var styleId = plainContent(styleKey);
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

    /**
     * Adds text to the paragraph, preserving manual line breaks.
     */
    public void addParagraphWithManualBreaks(XWPFParagraph paragraph, String text) {
        XWPFRun run = paragraph.createRun();
        String[] lines = text.split("\n", -1); // keep empty lines
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) {
                run.addBreak(); // manual line break
            }
            run.setText(lines[i]);
        }
    }

    public String contextualize(String text) {
        if (requireRTL(text)) {
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

    public void addFooterWithTextAndPageNumber(XWPFDocument doc, String footerText) {
        // Get or create section properties
        CTSectPr sectPr = doc.getDocument().getBody().isSetSectPr()
                ? doc.getDocument().getBody().getSectPr()
                : doc.getDocument().getBody().addNewSectPr();

        // Create footer policy
        XWPFHeaderFooterPolicy policy = new XWPFHeaderFooterPolicy(doc, sectPr);

        // Create footer
        XWPFFooter footer = policy.createFooter(STHdrFtr.DEFAULT);

        // --- First paragraph: custom text ---
        String footerTextStyle = "Footer";
        XWPFParagraph footerPara = footer.createParagraph();
        footerPara.setStyle(footerTextStyle);
        addParagraphWithManualBreaks(footerPara, footerText);

        // --- Second paragraph: PAGE field ---
        XWPFParagraph p2 = footer.createParagraph();
        p2.setAlignment(ParagraphAlignment.CENTER);

        // Begin field
        XWPFRun run = p2.createRun();
        run.getCTR().addNewFldChar().setFldCharType(STFldCharType.BEGIN);

        // Field instruction
        run = p2.createRun();
        run.getCTR().addNewInstrText().setStringValue("PAGE");

        // End field
        run = p2.createRun();
        run.getCTR().addNewFldChar().setFldCharType(STFldCharType.END);
    }

}
