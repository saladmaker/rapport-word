package rpp.poi.model;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
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
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STSectionMark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public interface Writable {

    public record ColumnConfig(boolean shouldSum, java.util.function.Supplier<Object> placeholderSupplier) {
    }

    void write(XWPFDocument document);

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

    default void applyTableStylePortrait(XWPFTable table, String styleId) {
        // Apply the existing style from the template
        table.getCTTbl().getTblPr().addNewTblStyle().setVal(styleId);

        CTTblPr tblPr = table.getCTTbl().getTblPr();
        CTTblLook look = tblPr.isSetTblLook() ? tblPr.getTblLook() : tblPr.addNewTblLook();
        look.setFirstRow(true);
        look.setFirstRow(true); // keep if you want header styling from the style
        look.setLastRow(true); // <-- this is the key for your shaded total row
        look.setFirstColumn(true); // optional
        look.setLastColumn(true); // optional
        look.setNoHBand(false); // optional (enable banding from style if present)
        look.setNoVBand(false);

        // Force fixed layout so width is honored
        CTTblLayoutType layoutType = tblPr.isSetTblLayout() ? tblPr.getTblLayout() : tblPr.addNewTblLayout();
        layoutType.setType(STTblLayoutType.FIXED);

        // Set full-page width (assuming 1" margins on A4/Letter)
        CTTblWidth tblWidth = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();
        tblWidth.setW(BigInteger.valueOf(9360)); // ~6.5 inches
        tblWidth.setType(STTblWidth.DXA);
    }

    default void addTotals(XWPFTable table, boolean sumRows, boolean sumColumns, List<ColumnConfig> columnConfigs,
            Supplier<String> lastColumnPlaceholder) {
        int rowCount = table.getNumberOfRows();
        XWPFTableRow totalRow = table.getRow(rowCount - 1); // last row
        totalRow.getCell(0).setText("Total"); // handle first column manually

        if (sumRows) {
            for (int i = 0; i < columnConfigs.size(); i++) {
                int colIndex = i + 1; // shift by 1 because first column is text
                ColumnConfig config = columnConfigs.get(i);

                if (config.shouldSum()) {
                    insertFormula(totalRow.getCell(colIndex), "=SUM(ABOVE)", config.placeholderSupplier().get().toString());
                } else {
                    totalRow.getCell(colIndex).setText("--");
                }
            }
        }

        if (sumColumns) {
            // Optional: handle totals per column
        }

        // If table has an extra column beyond configs (e.g., placeholder for last col)
        if (table.getRow(0).getTableCells().size() > columnConfigs.size() + 1) {
            int lastColIndex = table.getRow(0).getTableCells().size() - 1;
            totalRow.getCell(lastColIndex).setText(lastColumnPlaceholder.get());
        }
    }

    private void insertFormula(XWPFTableCell cell, String formula, String placeholder) {
        cell.removeParagraph(0);
        XWPFParagraph p = cell.addParagraph();
        CTSimpleField field = p.getCTP().addNewFldSimple();
        field.setInstr(formula);
        CTR ctr = field.addNewR();
        ctr.addNewT().setStringValue(placeholder);
    }

    private static STPageOrientation.Enum readOrientation(CTSectPr sectPr) {
        if (sectPr != null && sectPr.isSetPgSz()) {
            CTPageSz sz = sectPr.getPgSz();
            STPageOrientation.Enum o = sz.getOrient();
            if (o != null)
                return o;
            if (sz.isSetW() && sz.isSetH()) {
                return ((Comparable) sz.getW()).compareTo((Comparable) sz.getH()) > 0
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