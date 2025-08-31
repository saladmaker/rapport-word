package rpp.poi.model;

import java.time.Year;
import java.util.List;
import java.util.Optional;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import io.helidon.builder.api.Option;
import io.helidon.builder.api.Prototype;

@Prototype.Blueprint
interface FichePortefeuilleBlueprint extends Writable {

    String FCHPORT_1_TITLE_KEY = "section1.ficheportefeuille.title.text";
    String FCHPORT_2_GEST_KEY = "section1.ficheportefeuille.gestionnaire.text";

    String FCHPORT_3_TABLE_1_STYLE_KEY = "section1.ficheportefeuille.table1.style";
    String FCHPORT_3_TABLE_1_TITLE_KEY = "section1.ficheportefeuille.table1.title.text";

    String FCHPORT_3_TABLE_1_HEADER_1_KEY = "section1.ficheportefeuille.table1.headers.1";
    String FCHPORT_3_TABLE_1_HEADER_2_KEY = "section1.ficheportefeuille.table1.headers.2";
    String FCHPORT_3_TABLE_1_HEADER_3_KEY = "section1.ficheportefeuille.table1.headers.3";

    String FCHPORT_4_DEMARCHE_TITLE_KEY = "section1.ficheportefeuille.demarche.title.text";

    String FCHPORT_4_DEMARCHE_TEXT_KEYS = "section1.ficheportefeuille.demarche.text.";



    @Option.Required
    Year targetYear();

    @Option.Singular
    List<ProgrammeRepartition> repartitionProgrammeVersionBs();

    @Option.Singular
    List<ProgrammeRepartition> repartitionProgrammes();

    @Option.Singular
    List<RepartitionProgrammeCentreResp> repartitionProgrammeCentreResps();

    @Override
    default void write(XWPFDocument document, GenerationContext context) {
        //enforce portrait mode
        context.apply(PageLayout.PORTRAIT);

        //title
        String heading2Style = context.plainContent(HEADING_2_STYLE_KEY);
        XWPFParagraph heading = document.createParagraph();
        heading.setStyle(heading2Style);
        heading.createRun().setText(context.contextualizedContent(FCHPORT_1_TITLE_KEY));

        //gestionnaire
        String boldStyle = context.plainContent(BOLD_TEXT_STYLE_KEY);
        XWPFParagraph gest = document.createParagraph();
        gest.setStyle(boldStyle);
        gest.createRun().setText(context.contextualizedContent(FCHPORT_2_GEST_KEY));

        //table 1 version B
        writeTable1(document, context);
        writeDemarche(document, context);

    }

    default void writeTable1(XWPFDocument document, GenerationContext context) {

        //table title
        String tableTitleStyle = context.plainContent(STICKY_TITLE_STYLE_KEY);
        XWPFParagraph table_1_title_para = document.createParagraph();
        table_1_title_para.setStyle(tableTitleStyle);
        table_1_title_para.createRun().setText(context.contextualizedContent(FCHPORT_3_TABLE_1_TITLE_KEY));

        //determine data row count
        var programmeCount = repartitionProgrammeVersionBs().size();
        XWPFTable table_1 = document.createTable(programmeCount + 1 + 1, 3);// programme count + header + total
        context.applyTableStyle(table_1, FCHPORT_3_TABLE_1_STYLE_KEY);
        XWPFTableRow headerRow = table_1.getRow(0);

        headerRow.getCell(0).setText(context.contextualizedContent(FCHPORT_3_TABLE_1_HEADER_1_KEY));
        headerRow.getCell(1).setText(context.contextualizedContent(FCHPORT_3_TABLE_1_HEADER_2_KEY));
        headerRow.getCell(2).setText(context.contextualizedContent(FCHPORT_3_TABLE_1_HEADER_3_KEY));

        int rowIndex = 1;
        for (var p : repartitionProgrammeVersionBs()) {
            var pRow = table_1.getRow(rowIndex);
            pRow.getCell(0).setText(context.contextualize(p.name()));
            pRow.getCell(1).setText(context.contextualize(p.cp().toString()));
            pRow.getCell(2).setText(context.contextualize(p.ae().toString()));
            rowIndex++;
        }
        var configs = List.of(
                new ColumnConfig(true, () -> repartitionProgrammeVersionBs().stream().mapToLong(ProgrammeRepartition::cp).sum()),
                new ColumnConfig(true, () -> repartitionProgrammeVersionBs().stream().mapToLong(ProgrammeRepartition::ae).sum())
        );

        addTotals(table_1, true, false, configs, () -> "wrong");

    }

    default void writeDemarche(XWPFDocument document, GenerationContext context) {
        // demarche title
        final String titleStyle = context.plainContent(STICKY_TITLE_STYLE_KEY);
        XWPFParagraph demarchTitle = document.createParagraph();
        demarchTitle.setStyle(titleStyle);
        demarchTitle.createRun().setText(context.contextualizedContent(FCHPORT_4_DEMARCHE_TITLE_KEY));

        //varying paragraphs
        final String paragraphStyle = context.plainContent(LONG_PARAGRAPH_STYLE_KEY);
        int i = 0;
        Optional<String> demarchParaText = context.optionalText(FCHPORT_4_DEMARCHE_TEXT_KEYS + String.valueOf(i));
        while (demarchParaText.isPresent()) {
            XWPFParagraph demarcheParagraph = document.createParagraph();
            demarcheParagraph.setStyle(paragraphStyle);
            context.addParagraphWithManualBreaks(demarcheParagraph, demarchParaText.get());
            i++;
            demarchParaText = context.optionalText(FCHPORT_4_DEMARCHE_TEXT_KEYS + String.valueOf(i));
        }
    }

}
