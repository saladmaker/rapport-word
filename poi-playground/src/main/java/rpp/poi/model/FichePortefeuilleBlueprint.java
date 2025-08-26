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

    String $_1_TITLE_KEY = "section1.ficheportefeuille.title.text";
    String $_1_TITLE_STYLE_KEY = "section1.ficheportefeuille.title.style";
    String $_2_GEST_KEY = "section1.ficheportefeuille.gestionnaire.text";
    String $_2_GEST_STYLE_KEY = "section1.ficheportefeuille.gestionnaire.style";

    String $_3_TABLE_1_STYLE_KEY = "section1.ficheportefeuille.table1.style";
    String $_3_TABLE_1_TITLE_KEY = "section1.ficheportefeuille.table1.title.text";
    String $_3_TABLE_1_TITLE_STYLE_KEY = "section1.ficheportefeuille.table1.title.style";

    String $_3_TABLE_1_HEADER_1_KEY = "section1.ficheportefeuille.table1.headers.1";
    String $_3_TABLE_1_HEADER_2_KEY = "section1.ficheportefeuille.table1.headers.2";
    String $_3_TABLE_1_HEADER_3_KEY = "section1.ficheportefeuille.table1.headers.3";

    String $_4_DEMARCHE_KEY = "section1.ficheportefeuille.demarche.text";
    String $_4_DEMARCHE_STYLE_KEY = "section1.ficheportefeuille.demarche.style";
    
    String $_6_REPARTITION_VERSION_A = """
            Répartition des crédits de paiements
            et des autorisations d'engagement par programme
            (en milliers de dinars)
            """.replaceAll("\\R", " ");

    String $_7_PROGRAMME_CTRES = """
            Répartition des crédits des programmes par
            type de centre de responsabilitées (en dinars)
            """.replaceAll("\\R", " ");

    String $_8_PROGRAMME_TTR = """
            Répartition des crédits des programmes et
            titre-année %1 (en milliers de dinars)
            """.replaceAll("\\R", " ");

    String $_9_CTRES_TTR = """
            Répartition du portefeuille par titre et type
            de centre de responsabilité année %1 (en milliers de dinars)
            """.replaceAll("\\R", " ");

    String $_10_EVL_DEPS_PROGR = """
            Évolutions des dépense par programme (en milliers de dinars)
            """.replaceAll("\\R", " ");

    String $_11_EVL_PST_CTES = """
            Évolutions des postes ouverts par type de service
            """.replaceAll("\\R", " ");

    @Option.Required
    Year targetYear();

    List<Programme_AE_CP> table_1();

    List<String> demarches();

    List<Programme_AE_CP> table_2();

    List<Programme_CTRES> table_3();

    @Override
    default void write(XWPFDocument document, Map<String, String> config) {
        XWPFParagraph heading = document.createParagraph();
        heading.setStyle(config.get($_1_TITLE_STYLE_KEY));
        XWPFRun headingRun = heading.createRun();
        headingRun.setText(config.get($_1_TITLE_KEY));

        XWPFParagraph gest = document.createParagraph();
        gest.setStyle(config.get($_2_GEST_STYLE_KEY));
        gest.createRun().setText(config.get($_2_GEST_KEY));

        XWPFParagraph table_1_title = document.createParagraph();
        table_1_title.setStyle(config.get($_3_TABLE_1_TITLE_STYLE_KEY));
        table_1_title.createRun().setText(config.get($_3_TABLE_1_TITLE_KEY));

        var programmeCount = table_1().size();
        XWPFTable table_1 = document.createTable(programmeCount + 1 + 1, 3);// programme count + header + total
        applyTableStylePortrait(table_1, config.get($_3_TABLE_1_STYLE_KEY));
        XWPFTableRow headerRow = table_1.getRow(0);
        headerRow.getCell(0).setText(config.get($_3_TABLE_1_HEADER_1_KEY));
        headerRow.getCell(1).setText(config.get($_3_TABLE_1_HEADER_2_KEY));
        headerRow.getCell(2).setText(config.get($_3_TABLE_1_HEADER_3_KEY));

        int rowIndex = 1;
        for (var p : table_1()) {
            var pRow = table_1.getRow(rowIndex);

            pRow.getCell(0).setText(p.name());
            pRow.getCell(1).setText(p.cp().toString());
            pRow.getCell(2).setText(p.ae().toString());
            rowIndex++;
        }
        var configs = List.of(new ColumnConfig(true, () -> table_1().stream().mapToLong(Programme_AE_CP::cp).sum()), new ColumnConfig(true, () -> table_1().stream().mapToLong(Programme_AE_CP::ae).sum()));

        addTotals(table_1, true, false, configs, () -> "wrong");

        XWPFParagraph demarchTitle = document.createParagraph();
        demarchTitle.setStyle(config.get($_4_DEMARCHE_STYLE_KEY));
        demarchTitle.createRun().setText(config.get($_4_DEMARCHE_KEY));

    }

}
