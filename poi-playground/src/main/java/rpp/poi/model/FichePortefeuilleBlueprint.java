package rpp.poi.model;

import java.time.Year;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSimpleField;

import io.helidon.builder.api.Option;
import io.helidon.builder.api.Prototype;

@Prototype.Blueprint
interface FichePortefeuilleBlueprint extends Writable {
        String Style_1_NORMAL_BOLD = "NormalBold";
        String Style_1_protefeuille = "ficheportefeuille";
        String TEXT_1_TITLE = "Fiche Portefeuille";
        String TEXT_2_GESTIONNAIRE = "Gestionnaire responsable :";

        String TEXT_3_TABLE_1_TITLE = """
                        Répartition des crédits de paiements
                        et les autorisations d'engagement par programme
                        (en milliers de dinars) (version B)
                        """.replaceAll("\\R", " ");
        String TEXT_4_TABLE_1_HEADER_1 = "Programmes";
        String TEXT_4_TABLE_1_HEADER_2 = "Crédits de paiement";
        String TEXT_4_TABLE_1_HEADER_3 = "Autorisations d’engagement";
        String TEXT_4_TABLE_1_TOTAL = "Total des dépenses";

        String $TEXT_5_DEMARCHE = "Demarche adoptée pour le budget programme 2026";

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
        default void write(XWPFDocument document) {
                XWPFParagraph heading = document.createParagraph();
                heading.setStyle("Heading2");
                XWPFRun headingRun = heading.createRun();
                headingRun.setText(TEXT_1_TITLE);

                XWPFParagraph gest = document.createParagraph();
                gest.setStyle(Style_1_NORMAL_BOLD);
                XWPFRun gestRun = gest.createRun();
                gestRun.setText(TEXT_1_TITLE);

                XWPFParagraph table_1_title = document.createParagraph();
                table_1_title.setStyle(Style_1_NORMAL_BOLD);
                XWPFRun table_1_title_run = table_1_title.createRun();
                table_1_title_run.setText(TEXT_3_TABLE_1_TITLE);

                var programmeCount = table_1().size();
                XWPFTable table_1 = document.createTable(programmeCount + 1 + 1, 3);// programme count + header + total
                applyTableStylePortrait(table_1, Style_1_protefeuille);
                XWPFTableRow headerRow = table_1.getRow(0);
                headerRow.getCell(0).setText(TEXT_4_TABLE_1_HEADER_1);
                headerRow.getCell(1).setText(TEXT_4_TABLE_1_HEADER_2);
                headerRow.getCell(2).setText(TEXT_4_TABLE_1_HEADER_3);

                int rowIndex = 1;
                for (var p : table_1()) {
                        var pRow = table_1.getRow(rowIndex);

                        pRow.getCell(0).setText(p.name());
                        pRow.getCell(1).setText(p.cp().toString());
                        pRow.getCell(2).setText(p.ae().toString());
                        rowIndex++;
                }

                // total row
                var totalRow = table_1.getRow(programmeCount + 1); // last row
                totalRow.getCell(0).setText(TEXT_4_TABLE_1_TOTAL);

                // cp sum
                String cpFormula = String.format("=SUM(B2:B%d)", programmeCount + 1);
                insertFormula(totalRow.getCell(1), cpFormula);

                // ae sum
                String aeFormula = String.format("=SUM(C2:C%d)", programmeCount + 1);
                insertFormula(totalRow.getCell(2), aeFormula);

                XWPFParagraph demarchTitle = document.createParagraph();
                demarchTitle.setStyle(Style_1_NORMAL_BOLD);
                demarchTitle.createRun().setText($TEXT_5_DEMARCHE);

        }

        private static void insertFormula(XWPFTableCell cell, String formula) {
                cell.removeParagraph(0); // remove empty paragraph
                XWPFParagraph p = cell.addParagraph();

                // Add formula field
                CTSimpleField field = p.getCTP().addNewFldSimple();
                field.setInstr(formula);

                // Add visible placeholder text (Word replaces when updating fields)
                CTR ctr = field.addNewR();
                ctr.addNewT().setStringValue(formula);
        }

}
