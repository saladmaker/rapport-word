package rpp.poi.model;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSimpleField;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblLook;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

public interface Writable{

    public record ColumnConfig(boolean shouldSum, java.util.function.Supplier<Object> placeholderSupplier) {
    }

    void write(GenerationContext context);

    
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

    default void addTotals(XWPFTable table, boolean sumRows, boolean sumColumns, List<ColumnConfig> columnConfigs, Supplier<String> lastColumnPlaceholder) {
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

    
}