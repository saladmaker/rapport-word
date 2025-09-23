package mf.dgb.rpp.model;

import io.helidon.builder.api.Prototype;
import io.helidon.builder.api.Prototype.CustomMethods;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Prototype.Blueprint
@Prototype.CustomMethods(PostOuvertsMassSalarialesBlueprint.CustomMethods.class)
interface PostOuvertsMassSalarialesBlueprint extends Writable{

    String TABLE_STYLE_KEY = "section2.table";

    List<PostOuvertsMassSalariale> services();

    @Override
    default void write(XWPFDocument document, GenerationContext context){
        var size = services().size();
        if(0 == size){
            return;
        }
        context.apply(PageLayout.LANDSCAPE);
        //create table size
        var table = document.createTable(size + 5, 10);
        //apply style and proportions
        final double[] proportions = {0.15,0.10,0.10,0.10,0.08,0.08,0.13,0.13,0.13,0.10};
        context.applyTableStyle(table, TABLE_STYLE_KEY, proportions);
        mergeRegion(table, 0, 3, 0, 0); // Services (rows 0..4, col 0)
        mergeRegion(table, 0, 1, 1, 5); // Postes ouverts (rows 0..1, cols 1..5)
        mergeRegion(table, 0, 1, 6, 9); // Masse salariale (rows 0..1, cols 6..9)

        mergeRegion(table, 2, 3, 1, 3); // Nombre (rows 2..3, cols 1..3)
        mergeRegion(table, 2, 3, 4, 5); // Variation (under Postes) (rows 2..3, cols 4..5)
        mergeRegion(table, 2, 3, 6, 8); // Montant (rows 2..3, cols 6..8)
        mergeRegion(table, 2, 3, 9, 9); // Variation (under Masse) (rows 2..3, col 9)
        setCellTextCentered(table.getRow(0).getCell(1), "Postes ouverts");
        setCellTextCentered(table.getRow(0).getCell(6), "Masse salariale");

        setCellTextCentered(table.getRow(2).getCell(1), "Nombre");
        setCellTextCentered(table.getRow(2).getCell(4), "Variation");
        setCellTextCentered(table.getRow(2).getCell(6), "Montant");
        setCellTextCentered(table.getRow(2).getCell(9), "Variation");
        //set start row for filling function
        //configure headers values
        //create extractors
        context.fillTableContentWithStartingRow(table, services(), PostOuvertsMassSalariale.EXTRACTORS, TABLE_STYLE_KEY+".", 4);

    }

    final class CustomMethods{
        @Prototype.BuilderMethod
        static void service(PostOuvertsMassSalariales.BuilderBase<?,?> builderBase, PostOuvertsMassSalariale poms){
            Objects.requireNonNull(poms, "poms can't be null!");

            List<PostOuvertsMassSalariale> services = new ArrayList<>(builderBase.services());

            for(var p: services){
                if(p.serviceType() == poms.serviceType()){
                    throw  new IllegalArgumentException("service type " + p.serviceType() + " already exist!");
                }
            }

            services.add(poms);

            //replaces old collection
            builderBase.services(services);
        }
    }
    private static void mergeRegion(XWPFTable table, int fromRow, int toRow, int fromCol, int toCol) {
        for (int r = fromRow; r <= toRow; r++) {
            for (int c = fromCol; c <= toCol; c++) {
                XWPFTableCell cell = table.getRow(r).getCell(c);
                CTTcPr tcPr = getTcPr(cell);

                // Horizontal merge: restart at left-most column else continue
                if (c == fromCol) {
                    tcPr.addNewHMerge().setVal(STMerge.RESTART);
                } else {
                    tcPr.addNewHMerge().setVal(STMerge.CONTINUE);
                }

                // Vertical merge: restart at top row else continue
                if (r == fromRow) {
                    tcPr.addNewVMerge().setVal(STMerge.RESTART);
                } else {
                    tcPr.addNewVMerge().setVal(STMerge.CONTINUE);
                }
            }
        }
    }
    private static void setCellTextCentered(XWPFTableCell cell, String text) {
        cell.removeParagraph(0);
        XWPFParagraph p = cell.addParagraph();
        XWPFRun run = p.createRun();
        run.setBold(true);
        run.setText(text);
    }
    private static CTTcPr getTcPr(XWPFTableCell cell) {
        CTTc cttc = cell.getCTTc();
        return cttc.isSetTcPr() ? cttc.getTcPr() : cttc.addNewTcPr();
    }

}
