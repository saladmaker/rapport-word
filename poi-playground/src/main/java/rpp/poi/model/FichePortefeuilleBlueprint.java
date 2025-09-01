package rpp.poi.model;

import java.time.Year;
import java.util.List;
import java.util.Optional;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import io.helidon.builder.api.Option;
import io.helidon.builder.api.Prototype;

@Prototype.Blueprint
interface FichePortefeuilleBlueprint extends Writable {

    String FCHPORT_1_TITLE_KEY = "section1.ficheportefeuille.title.text";
    String FCHPORT_2_GEST_KEY = "section1.ficheportefeuille.gestionnaire.text";

    String FCHPORT_3_TABLE_1_STYLE_KEY = "section1.ficheportefeuille.table1.style";
    String FCHPORT_3_TABLE_1_TITLE_KEY = "section1.ficheportefeuille.table1.title.text";
    String FCHPORT_3_TABLE_1_HEADER_KEYS = "section1.ficheportefeuille.table1.headers.";

    String FCHPORT_4_DEMARCHE_TITLE_KEY = "section1.ficheportefeuille.demarche.title.text";
    String FCHPORT_4_DEMARCHE_TEXT_KEYS = "section1.ficheportefeuille.demarche.text.";

    String FCHPORT_5_TABLE_2_STYLE_KEY = "section1.ficheportefeuille.table1.style";
    String FCHPORT_5_TABLE_2_TITLE_KEY = "section1.ficheportefeuille.table2.title.text";

    String FCHPORT_6_TABLE_3_STYLE_KEY = "section1.ficheportefeuille.table.styles.2";
    String FCHPORT_6_TABLE_3_TITLE_KEY = "section1.ficheportefeuille.table3.title.text";
    String FCHPORT_6_TABLE_3_HEADER_KEYS = "section1.ficheportefeuille.table3.headers.";

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
        // enforce portrait mode
        context.apply(PageLayout.PORTRAIT);

        // title
        String heading2Style = context.plainContent(HEADING_2_STYLE_KEY);
        XWPFParagraph heading = document.createParagraph();
        heading.setStyle(heading2Style);
        heading.createRun().setText(context.contextualizedContent(FCHPORT_1_TITLE_KEY));

        // gestionnaire
        String boldStyle = context.plainContent(BOLD_TEXT_STYLE_KEY);
        XWPFParagraph gest = document.createParagraph();
        gest.setStyle(boldStyle);
        gest.createRun().setText(context.contextualizedContent(FCHPORT_2_GEST_KEY));

        // table 1 version B
        writeTable1(document, context);
        writeDemarche(document, context);
        writeTable2(document, context);
        writeTable3(document, context);
    }

    default void writeTable1(XWPFDocument document, GenerationContext context) {

        String tableTitleStyle = context.plainContent(STICKY_TITLE_STYLE_KEY);
        XWPFParagraph table_1_title_para = document.createParagraph();
        table_1_title_para.setStyle(tableTitleStyle);
        table_1_title_para.createRun().setText(context.contextualizedContent(FCHPORT_3_TABLE_1_TITLE_KEY));
        List<ColumnExtractor<? super ProgrammeRepartition, ?>> extractors = List.of(
                ColumnExtractor.ofUnsummable(ProgrammeRepartition::name),
                ColumnExtractor.ofSummable(ProgrammeRepartition::ae),
                ColumnExtractor.ofSummable(ProgrammeRepartition::cp));
        List<ProgrammeRepartition> rows = repartitionProgrammeVersionBs();
        context.writeTable(document, FCHPORT_3_TABLE_1_STYLE_KEY, FCHPORT_3_TABLE_1_HEADER_KEYS, rows, extractors, "Total",
                false, "");

    }

    default void writeDemarche(XWPFDocument document, GenerationContext context) {
        // demarche title
        final String titleStyle = context.plainContent(STICKY_TITLE_STYLE_KEY);
        XWPFParagraph demarchTitle = document.createParagraph();
        demarchTitle.setStyle(titleStyle);
        demarchTitle.createRun().setText(context.contextualizedContent(FCHPORT_4_DEMARCHE_TITLE_KEY));

        // varying paragraphs
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

    default void writeTable2(XWPFDocument document, GenerationContext context) {

        // table title
        String tableTitleStyle = context.plainContent(STICKY_TITLE_STYLE_KEY);
        XWPFParagraph table_title_para = document.createParagraph();
        table_title_para.setStyle(tableTitleStyle);
        table_title_para.createRun().setText(context.contextualizedContent(FCHPORT_5_TABLE_2_TITLE_KEY));

        List<ColumnExtractor<? super ProgrammeRepartition, ?>> extractors = List.of(
                ColumnExtractor.ofUnsummable(ProgrammeRepartition::name),
                ColumnExtractor.ofSummable(ProgrammeRepartition::ae),
                ColumnExtractor.ofSummable(ProgrammeRepartition::cp));
        List<ProgrammeRepartition> rows = repartitionProgrammes();
        context.writeTable(document, FCHPORT_3_TABLE_1_STYLE_KEY, FCHPORT_3_TABLE_1_HEADER_KEYS, rows, extractors, "Total",
                false, "");

    }
    default void writeTable3(XWPFDocument document, GenerationContext context) {

        // table title
        String tableTitleStyle = context.plainContent(STICKY_TITLE_STYLE_KEY);
        XWPFParagraph table_title_para = document.createParagraph();
        table_title_para.setStyle(tableTitleStyle);
        table_title_para.createRun().setText(context.contextualizedContent(FCHPORT_6_TABLE_3_TITLE_KEY));

        List<ColumnExtractor<? super RepartitionProgrammeCentreResp, ?>> extractors = List.of(
                ColumnExtractor.ofUnsummable(RepartitionProgrammeCentreResp::name),
                ColumnExtractor.ofSummable(r -> r.ctres().get(0)),
                ColumnExtractor.ofSummable(r -> r.ctres().get(1)),
                ColumnExtractor.ofSummable(r -> r.ctres().get(2)),
                ColumnExtractor.ofSummable(r -> r.ctres().get(3))
                );
        List<RepartitionProgrammeCentreResp> rows = repartitionProgrammeCentreResps();
        context.writeTable(document, FCHPORT_6_TABLE_3_STYLE_KEY, FCHPORT_6_TABLE_3_HEADER_KEYS, rows, extractors, "Total",
                true, "Total");

    }


}
