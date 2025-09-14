package mf.dgb.rpp.model;

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
            nameCell.setText(programmeStructure.name());
            setVMerge(nameCell, STMerge.RESTART);

            row0.getCell(1).setText(context.contextualize(String.join(", ", programmeStructure.serviceCentres())));

            // --- Row 2 (SDC) ---
            XWPFTableRow row1 = progStrtable.createRow();
            setVMerge(row1.getCell(0), STMerge.CONTINUE);
            row1.getCell(1).setText(context.contextualize(String.join(", ", programmeStructure.serviceDeconcentrees())));

            // --- Row 3 (OST) ---
            XWPFTableRow row2 = progStrtable.createRow();
            setVMerge(row2.getCell(0), STMerge.CONTINUE);
            row2.getCell(1).setText(context.contextualize(String.join(", ", programmeStructure.organismeSousTutelles())));

            // --- Row 4 (OT) ---
            XWPFTableRow row3 = progStrtable.createRow();
            setVMerge(row3.getCell(0), STMerge.CONTINUE);
            row3.getCell(1).setText(context.contextualize(String.join(", ", programmeStructure.orgamismeTerris())));
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


}
