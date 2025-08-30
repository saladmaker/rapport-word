package rpp.poi.playground.generation;

import java.math.BigInteger;
import java.util.Random;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblLook;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

import rpp.poi.playground.DocumentGenerator;

public class TableStyles_2 implements DocumentGenerator{
    private static final String[] TABLE_HEADERS = {
            "Category", "Region", "Department", "Quarter", "Year"
    };

    private static final String[] TABLE_ROW_LABELS = {
            "North", "South", "East", "West", "Total"
    };

    @Override
    public void generate(XWPFDocument doc) throws Exception {
        addRandomFichePortefeuilleTable(doc);
    }
  
    public static void addRandomFichePortefeuilleTable(XWPFDocument doc) {
        int rows = TABLE_ROW_LABELS.length + 1; // +1 for header row
        int cols = TABLE_HEADERS.length;

        // Create the table with the required dimensions
        XWPFTable table = doc.createTable(rows, cols);

        applyStyle(table);

        // Fill the header row (row 0)
        XWPFTableRow headerRow = table.getRow(0);
        for (int c = 0; c < cols; c++) {
            headerRow.getCell(c).removeParagraph(0);
            headerRow.getCell(c).setText(TABLE_HEADERS[c]);
        }

        // Fill the first column with row labels, rest with random integers
        for (int r = 1; r < rows; r++) {
            XWPFTableRow row = table.getRow(r);

            for (int c = 0; c < cols; c++) {
                row.getCell(c).removeParagraph(0);
                if (c == 0) {
                    // First column: labels
                    row.getCell(c).setText(TABLE_ROW_LABELS[r - 1]);
                } else {
                    // Random integer between 10 and 999
                    row.getCell(c).setText(String.valueOf(new Random().nextInt(990) + 10));
                }
            }
        }
    }

    private static void applyStyle(XWPFTable table) {
        // Apply the existing style from the template
        table.getCTTbl().getTblPr().addNewTblStyle().setVal("myStyle");

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
        CTTblLayoutType layoutType = tblPr.isSetTblLayout() ? tblPr.getTblLayout() :
        tblPr.addNewTblLayout();
        layoutType.setType(STTblLayoutType.FIXED);

        // Set full-page width (assuming 1" margins on A4/Letter)
        CTTblWidth tblWidth = tblPr.isSetTblW() ? tblPr.getTblW() :
        tblPr.addNewTblW();
        tblWidth.setW(BigInteger.valueOf(9360)); // ~6.5 inches
        tblWidth.setType(STTblWidth.DXA);

    }
}
