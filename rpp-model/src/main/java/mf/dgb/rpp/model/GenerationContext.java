package mf.dgb.rpp.model;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
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
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHdrFtr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STSectionMark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

public final class GenerationContext {

    static final String FOOTER_TEXT_KEY = "footer.text";
    static final String DEFAULT_TOTAL_LABEL = "default.total";

    private final XWPFDocument doc;
    private final LanguageDirection direction;
    private PageLayout currentLayout = PageLayout.PORTRAIT;
    private final Map<String, String> config;

    private GenerationContext(XWPFDocument doc, Map<String, String> config, LanguageDirection direction) {
        this.doc = doc;
        this.config = Map.copyOf(config);
        this.direction = direction;
        var footerText = contextualizedContent(FOOTER_TEXT_KEY);
        addFooterAndPageNumber(doc, footerText);
    }

    public static GenerationContext of(XWPFDocument doc, LanguageDirection languageDirection) {
        Objects.requireNonNull(doc);
        Objects.requireNonNull(languageDirection);
        Map<String, String> config = new HashMap<>();
        String resource = null;
        if (LanguageDirection.LTR == languageDirection) {
            resource = "french.properties";
        }else{
            resource = "arab.properties";
        }
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
                InputStreamReader r = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            Properties props = new Properties();
            if (is == null) {
                throw new FileNotFoundException("Resource not found: " + resource);
            }
            props.load(r);
            for (String name : props.stringPropertyNames()) {
                config.put(name, props.getProperty(name));
            }
            return new GenerationContext(doc, config, languageDirection);

        } catch (Exception e) {
            throw new IllegalStateException("can't load resource");
        }
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
        if (null == value) {
            throw new IllegalStateException("value doesn't exist \"" + key + "\"");
        }
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

    public <T> void writeTable(
            XWPFDocument document,
            final String tableStyleKey,
            final String contentPrefix,
            List<T> rows,
            List<ColumnExtractor<T, ?>> extractors,
            boolean addTotalRow) {

        final String headersPrefix = contentPrefix + "headers.";
        final String totalColumnLabelKey = contentPrefix + "total.column";
        final String totalRowLabelKey = contentPrefix + "total.row";

        // sum that dependes on language direction
        String lastColumnFormula = "=SUM(LEFT)";
        if (LanguageDirection.RTL == getDirection()) {
            lastColumnFormula = "=SUM(RIGHT)";
        }

        // calculate and create a proper size table
        int nCols = extractors.size() + (addTotalRow ? 1 : 0);
        XWPFTable table = document.createTable(rows.size() + 1 + 1, nCols);

        // apply table level style
        applyTableStyle(table, tableStyleKey);

        // apply cell style when it's configured
        final String cellStyleKey = tableStyleKey + ".cell";
        var cellStyleOpt = optionalText(cellStyleKey);
        if (cellStyleOpt.isPresent()) {
            var cellStyle = cellStyleOpt.get();
            for (int i = 0; i < table.getRows().size(); i++) {
                var row = table.getRow(i);
                for (int j = 0; j < row.getTableICells().size(); j++) {
                    row.getCell(j).getParagraphs().get(0).setStyle(cellStyle);
                }
            }
        }

        // ---- Header ----
        XWPFTableRow headerRow = table.getRow(0);
        preventRowSplit(headerRow);
        for (int i = 0; i < extractors.size(); i++) {
            headerRow.getCell(i).setText(contextualizedContent(headersPrefix + i));
        }
        if (addTotalRow) {
            var totalRowLabel = totalLabel(totalRowLabelKey);
            headerRow.getCell(extractors.size()).setText(totalRowLabel);
        }

        // ---- Data rows ----
        for (int r = 0; r < rows.size(); r++) {
            XWPFTableRow row = table.getRow(r + 1);
            preventRowSplit(row);
            T rowData = rows.get(r);

            for (int c = 0; c < extractors.size(); c++) {
                Object data = extractors.get(c).apply(rowData);
                if(data instanceof Enum<?> constant){
                    data = contextualizedContent(constant.name());
                }
                String value = String.valueOf(data);
                row.getCell(c).setText(value);
            }

            // add total row if configured
            if (addTotalRow) {
                XWPFTableCell totalCell = row.getCell(extractors.size());
                insertFormula(totalCell, lastColumnFormula, "0");
            }
        }

        XWPFTableRow totalRow = table.getRow(table.getNumberOfRows() - 1);
        preventRowSplit(totalRow);
        var totalColumnLabel = totalLabel(totalColumnLabelKey);
        totalRow.getCell(0).setText(totalColumnLabel);

        for (int c = 1; c < extractors.size(); c++) {
            XWPFTableCell totalCell = totalRow.getCell(c);
            if (extractors.get(c) instanceof ColumnExtractor.SummableColumnExtractor) {
                insertFormula(totalCell, "=SUM(ABOVE)", "0");
            } else {
                totalCell.setText("-");
            }
        }

        if (addTotalRow) {
            XWPFTableCell grandTotal = totalRow.getCell(extractors.size());
            insertFormula(grandTotal, lastColumnFormula, "0");
        }

    }

    private String totalLabel(final String totalRowLabelKey) {
        Objects.requireNonNull(totalRowLabelKey, "totalRowLabelKey can not be null");
        var totalRowLabelOpt = optionalText(totalRowLabelKey);
        if (totalRowLabelOpt.isPresent()) {
            return contextualize(totalRowLabelOpt.get());
        } else {
            return contextualizedContent(DEFAULT_TOTAL_LABEL);
        }
    }

    private void preventRowSplit(XWPFTableRow row) {
        CTTrPr trPr = row.getCtRow().isSetTrPr() ? row.getCtRow().getTrPr() : row.getCtRow().addNewTrPr();
        trPr.addNewCantSplit(); // Minimal POI use
    }

    private void insertFormula(XWPFTableCell cell, String formula, String placeholder) {
        cell.removeParagraph(0);
        XWPFParagraph p = cell.addParagraph();
        CTSimpleField field = p.getCTP().addNewFldSimple();
        field.setInstr(formula);
        CTR ctr = field.addNewR();
        ctr.addNewT().setStringValue(placeholder);
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

        // Dynamic width: 99% of usable width
        CTTblWidth tblWidth = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();
        tblWidth.setW(getScaledUsableWidth(0.99));
        tblWidth.setType(STTblWidth.DXA);

        // --- Direction from PageLayoutManager
        if (getDirection() == LanguageDirection.RTL) {
            tblPr.addNewBidiVisual().setVal(STOnOff1.ON);
        } else {
            tblPr.addNewBidiVisual().setVal(STOnOff1.OFF);
        }
    }

    public void addFooterAndPageNumber(XWPFDocument doc, String footerText) {
        // Get or create section properties
        CTSectPr sectPr = doc.getDocument().getBody().isSetSectPr()
                ? doc.getDocument().getBody().getSectPr()
                : doc.getDocument().getBody().addNewSectPr();

        // Create footer policy
        XWPFHeaderFooterPolicy policy = new XWPFHeaderFooterPolicy(doc, sectPr);

        XWPFFooter footer = policy.createFooter(STHdrFtr.DEFAULT);

        // footer paragraph
        String footerTextStyle = "Footer";
        XWPFParagraph footerPara = footer.createParagraph();
        footerPara.setStyle(footerTextStyle);
        addParagraphWithManualBreaks(footerPara, footerText);

        // page number
        XWPFParagraph p2 = footer.createParagraph();
        p2.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = p2.createRun();
        run.getCTR().addNewFldChar().setFldCharType(STFldCharType.BEGIN);
        run = p2.createRun();
        run.getCTR().addNewInstrText().setStringValue("PAGE");
        run = p2.createRun();
        run.getCTR().addNewFldChar().setFldCharType(STFldCharType.END);
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

}
