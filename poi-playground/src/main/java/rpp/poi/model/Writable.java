package rpp.poi.model;

import java.util.List;
import java.util.function.Supplier;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSimpleField;


public interface Writable {

    String PARAGRAPH_DEFAULT_STYLE_KEY = "paragraph.default.style";
    String HEADING_1_STYLE_KEY = "heading1.style";
    String HEADING_2_STYLE_KEY = "heading2.style";
    String HEADING_3_STYLE_KEY = "heading3.style";
    String MULTI_LEVEL_LIST_KEY = "multilevel.list.id";
    String LONG_PARAGRAPH_STYLE_KEY = "paragraph.text.style";
    String BOLD_TEXT_STYLE_KEY = "bold.text.style";
    String IMG_STYLE_KEY = "image.style";
    String STICKY_TITLE_STYLE_KEY = "sticky.title.style";

    
    public record ColumnConfig(boolean shouldSum, java.util.function.Supplier<Object> placeholderSupplier) {
    }

    void write(XWPFDocument document, GenerationContext context);
    

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
                    insertFormula(totalRow.getCell(colIndex), "=SUM(ABOVE)",
                            config.placeholderSupplier().get().toString());
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