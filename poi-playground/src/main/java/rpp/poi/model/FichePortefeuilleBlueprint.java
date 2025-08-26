package rpp.poi.model;

import java.time.Year;
import java.util.List;
import java.util.Map;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import io.helidon.builder.api.Option;
import io.helidon.builder.api.Prototype;

@Prototype.Blueprint
interface FichePortefeuilleBlueprint extends Writable {

    String FCHPORT_1_TITLE_KEY = "section1.ficheportefeuille.title.text";
    String FCHPORT_1_TITLE_STYLE_KEY = "section1.ficheportefeuille.title.style";
    String FCHPORT_2_GEST_KEY = "section1.ficheportefeuille.gestionnaire.text";
    String FCHPORT_2_GEST_STYLE_KEY = "section1.ficheportefeuille.gestionnaire.style";

    String FCHPORT_3_TABLE_1_STYLE_KEY = "section1.ficheportefeuille.table1.style";
    String FCHPORT_3_TABLE_1_TITLE_KEY = "section1.ficheportefeuille.table1.title.text";
    String FCHPORT_3_TABLE_1_TITLE_STYLE_KEY = "section1.ficheportefeuille.table1.title.style";

    String FCHPORT_3_TABLE_1_HEADER_1_KEY = "section1.ficheportefeuille.table1.headers.1";
    String FCHPORT_3_TABLE_1_HEADER_2_KEY = "section1.ficheportefeuille.table1.headers.2";
    String FCHPORT_3_TABLE_1_HEADER_3_KEY = "section1.ficheportefeuille.table1.headers.3";

    String FCHPORT_4_DEMARCHE_KEY = "section1.ficheportefeuille.demarche.text";
    String FCHPORT_4_DEMARCHE_STYLE_KEY = "section1.ficheportefeuille.demarche.style";

    @Option.Required
    Year targetYear();

    List<Programme_AE_CP> table_1();

    List<String> demarches();

    List<Programme_AE_CP> table_2();

    List<Programme_CTRES> table_3();

    @Override
    default void write(XWPFDocument document, Map<String, String> config, PageLayoutManager plm) {  
        plm.apply(PageLayout.PORTRAIT);
        XWPFParagraph heading = document.createParagraph();
        heading.setStyle(config.get(FCHPORT_1_TITLE_STYLE_KEY));
        XWPFRun headingRun = heading.createRun();
        headingRun.setText(config.get(FCHPORT_1_TITLE_KEY));

        XWPFParagraph gest = document.createParagraph();
        gest.setStyle(config.get(FCHPORT_2_GEST_STYLE_KEY));
        gest.createRun().setText(config.get(FCHPORT_2_GEST_KEY));

        //table 1 version B
        writeTable1(document, config);
        writeDemarche(document, config);
        

    }

    default void writeTable1(XWPFDocument document, Map<String, String> config) {
        XWPFParagraph table_1_title = document.createParagraph();
        table_1_title.setStyle(config.get(FCHPORT_3_TABLE_1_TITLE_STYLE_KEY));
        table_1_title.createRun().setText(config.get(FCHPORT_3_TABLE_1_TITLE_KEY));

        var programmeCount = table_1().size();
        XWPFTable table_1 = document.createTable(programmeCount + 1 + 1, 3);// programme count + header + total
        applyTableStylePortrait(table_1, config.get(FCHPORT_3_TABLE_1_STYLE_KEY));
        XWPFTableRow headerRow = table_1.getRow(0);
        headerRow.getCell(0).setText(config.get(FCHPORT_3_TABLE_1_HEADER_1_KEY));
        headerRow.getCell(1).setText(config.get(FCHPORT_3_TABLE_1_HEADER_2_KEY));
        headerRow.getCell(2).setText(config.get(FCHPORT_3_TABLE_1_HEADER_3_KEY));

        int rowIndex = 1;
        for (var p : table_1()) {
            var pRow = table_1.getRow(rowIndex);

            pRow.getCell(0).setText(p.name());
            pRow.getCell(1).setText(p.cp().toString());
            pRow.getCell(2).setText(p.ae().toString());
            rowIndex++;
        }
        var configs = List.of(
                new ColumnConfig(true, () -> table_1().stream().mapToLong(Programme_AE_CP::cp).sum()),
                new ColumnConfig(true, () -> table_1().stream().mapToLong(Programme_AE_CP::ae).sum())
        );

        addTotals(table_1, true, false, configs, () -> "wrong");

    }

    default void writeDemarche(XWPFDocument document, Map<String, String> config) {
        XWPFParagraph demarchTitle = document.createParagraph();
        demarchTitle.setStyle(config.get(FCHPORT_4_DEMARCHE_STYLE_KEY));
        demarchTitle.createRun().setText(config.get(FCHPORT_4_DEMARCHE_KEY));
        for (var demarcheTxt : demarches()) {
            XWPFParagraph demarcheParagraph = document.createParagraph();
            demarcheParagraph.setStyle("Normal");
            addParagraphWithManualBreaks(demarcheParagraph, demarcheTxt);
        }

    }

}
