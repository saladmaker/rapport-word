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
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.impl.xb.xmlschema.SpaceAttribute;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFldChar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSimpleField;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblLook;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
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

    @SuppressWarnings("unchecked")
    public <T> void writeTable(
            XWPFDocument document,
            String tableStyleKey,
            String headerPrefix,
            List<T> rows,
            List<ColumnExtractor<? super T, ?>> extractors,
            String totalRowLabel,
            boolean addTotalColumn,
            String totalColumnLabel) {
        String totalFormula = "=SUM(LEFT)";
        if (LanguageDirection.RTL == getDirection()) {
            totalFormula = "=SUM(RIGHT)";
        }
        int nCols = extractors.size() + (addTotalColumn ? 1 : 0);
        XWPFTable table = document.createTable(rows.size() + 1 + (totalRowLabel != null ? 1 : 0), nCols);
        applyTableStyle(table, tableStyleKey);

        // ---- Header ----
        XWPFTableRow headerRow = table.getRow(0);
        for (int i = 0; i < extractors.size(); i++) {
            headerRow.getCell(i).setText(
                    optionalText(headerPrefix + i).orElse("Col" + (i + 1)));
        }
        if (addTotalColumn) {
            headerRow.getCell(extractors.size()).setText(totalColumnLabel);
        }

        // ---- Data rows ----
        for (int r = 0; r < rows.size(); r++) {
            XWPFTableRow row = table.getRow(r + 1);
            T rowData = rows.get(r);

            for (int c = 0; c < extractors.size(); c++) {
                String value = String.valueOf(extractors.get(c).apply(rowData));
                row.getCell(c).setText(value);
            }

            // Row total only if there are summable columns
            if (addTotalColumn) {

                boolean hasSummable = extractors.stream()
                        .anyMatch(e -> e instanceof ColumnExtractor.SummableColumnExtractor);
                XWPFTableCell totalCell = row.getCell(extractors.size());
                if (hasSummable) {
                    insertFormula(totalCell, totalFormula, "0");
                } else {
                    totalCell.setText("-"); // or "-"
                }
            }
        }

        // ---- Total row ----
        if (totalRowLabel != null) {
            XWPFTableRow totalRow = table.getRow(table.getNumberOfRows() - 1);
            totalRow.getCell(0).setText(totalRowLabel);

            for (int c = 1; c < extractors.size(); c++) {
                XWPFTableCell totalCell = totalRow.getCell(c);
                if (extractors.get(c) instanceof ColumnExtractor.SummableColumnExtractor) {
                    insertFormula(totalCell, "=SUM(ABOVE)", "0");
                } else {
                    totalCell.setText("-"); // unsummable column
                }
            }

            if (addTotalColumn) {
                boolean hasSummable = extractors.stream()
                        .anyMatch(e -> e instanceof ColumnExtractor.SummableColumnExtractor);
                XWPFTableCell grandTotal = totalRow.getCell(extractors.size());
                if (hasSummable) {
                    insertFormula(grandTotal, totalFormula, "0");
                } else {
                    grandTotal.setText("");
                }
            }
        }
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

    private void insertFormula(XWPFTableCell cell, String formula, String fallback) {
        cell.removeParagraph(0); // clear default paragraph
        XWPFParagraph p = cell.addParagraph();
        CTP ctp = p.getCTP();

        // BEGIN field
        CTR rBegin = ctp.addNewR();
        CTFldChar fldBegin = rBegin.addNewFldChar();
        fldBegin.setFldCharType(STFldCharType.BEGIN);

        // INSTRUCTION text (the actual formula)
        CTR rInstr = ctp.addNewR();
        CTText instrText = rInstr.addNewInstrText();
        instrText.setStringValue(formula);
        instrText.setSpace(SpaceAttribute.Space.PRESERVE);

        // SEPARATE (marks the switch between formula and display text)
        CTR rSep = ctp.addNewR();
        CTFldChar fldSep = rSep.addNewFldChar();
        fldSep.setFldCharType(STFldCharType.SEPARATE);

        // RESULT (fallback display text)
        CTR rResult = ctp.addNewR();
        rResult.addNewT().setStringValue(fallback);

        // END field
        CTR rEnd = ctp.addNewR();
        CTFldChar fldEnd = rEnd.addNewFldChar();
        fldEnd.setFldCharType(STFldCharType.END);
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
