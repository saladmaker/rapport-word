package rpp.poi.model;

import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;

import io.helidon.builder.api.Option;
import io.helidon.builder.api.Prototype;

@Prototype.Blueprint
interface CartographieProgrammesPortefeuilleBlueprint extends Writable {
    String CARTO_1_TITLE_KEY = "section1.cartographie.title.text";

    String CARTO_2_TABLE_STYLE_KEY = "section1.cartographie.table.style";
    String CARTO_2_TABLE_HEADER_1_KEY = "section1.cartographie.table.headers.1";
    String CARTO_2_TABLE_HEADER_2_KEY = "section1.cartographie.table.headers.2";

    @Option.Singular
    List<ProgrammeStructure> programmeStructures();

    @Override
    default void write(XWPFDocument document, GenerationContext context) {
        context.apply(PageLayout.PORTRAIT);
        // Title
        String heading2Style = context.plainContent(HEADING_2_STYLE_KEY);
        XWPFParagraph title = document.createParagraph();
        title.setStyle(heading2Style);
        title.createRun().setText(context.contextualizedContent(CARTO_1_TITLE_KEY));

        // Table
        XWPFTable progStrtable = document.createTable();
        context.applyTableStyle(progStrtable, CARTO_2_TABLE_STYLE_KEY);

        // Header row (make sure it has exactly 2 cells)
        XWPFTableRow headerRow = progStrtable.getRow(0);
        headerRow.getCell(0).setText(context.contextualizedContent(CARTO_2_TABLE_HEADER_1_KEY));
        if (headerRow.getTableCells().size() < 2) {
            headerRow.addNewTableCell();
        }
        headerRow.getCell(1).setText(context.contextualizedContent(CARTO_2_TABLE_HEADER_2_KEY));

        for (var programmeStructure : programmeStructures()) {

            // --- Row 1 (with name + SC) ---
            XWPFTableRow row0 = progStrtable.createRow();
            XWPFTableCell nameCell = row0.getCell(0);
            clearCell(nameCell);
            nameCell.setText(programmeStructure.name());
            setVMerge(nameCell, STMerge.RESTART);

            clearCell(row0.getCell(1));
            row0.getCell(1).setText(context.contextualize(String.join(", ", programmeStructure.sc())));

            // --- Row 2 (SDC) ---
            XWPFTableRow row1 = progStrtable.createRow();
            setVMerge(row1.getCell(0), STMerge.CONTINUE);
            clearCell(row1.getCell(1));
            row1.getCell(1).setText(context.contextualize(String.join(", ", programmeStructure.sdc())));

            // --- Row 3 (OST) ---
            XWPFTableRow row2 = progStrtable.createRow();
            setVMerge(row2.getCell(0), STMerge.CONTINUE);
            clearCell(row2.getCell(1));
            row2.getCell(1).setText(context.contextualize(String.join(", ", programmeStructure.ost())));

            // --- Row 4 (OT) ---
            XWPFTableRow row3 = progStrtable.createRow();
            setVMerge(row3.getCell(0), STMerge.CONTINUE);
            clearCell(row3.getCell(1));
            row3.getCell(1).setText(context.contextualize(String.join(", ", programmeStructure.ot())));
        }
    }

    /**
     * Helper: safely apply vertical merge to a cell
     */
    private static void setVMerge(XWPFTableCell cell, STMerge.Enum mergeVal) {
        CTTcPr tcPr = cell.getCTTc().isSetTcPr() ? cell.getCTTc().getTcPr() : cell.getCTTc().addNewTcPr();
        CTVMerge vMerge = tcPr.isSetVMerge() ? tcPr.getVMerge() : tcPr.addNewVMerge();
        vMerge.setVal(mergeVal);
    }

    /**
     * Helper: remove the default empty paragraph before setting text
     */
    private static void clearCell(XWPFTableCell cell) {
        if (!cell.getParagraphs().isEmpty()) {
            cell.removeParagraph(0);
        }
    }

}
