package mf.dgb.rpp.model;

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

    String FCHPORT_3_TABLE_1_PREFIX = "section1.ficheportefeuille.table.1.";


    String FCHPORT_4_DEMARCHE_TITLE_KEY = "section1.ficheportefeuille.demarche.title.text";
    String FCHPORT_4_DEMARCHE_TEXT_KEYS = "section1.ficheportefeuille.demarche.text.";

    String FCHPORT_5_TABLE_2_PREFIX = "section1.ficheportefeuille.table.2.";

    String FCHPORT_6_TABLE_3_PREFIX = "section1.ficheportefeuille.table.3.";

    String FCHPORT_7_TABLE_4_PREFIX = "section1.ficheportefeuille.table.4.";

    String FCHPORT_8_TABLE_5_PREFIX = "section1.ficheportefeuille.table.5.";

    String FCHPORT_9_TABLE_6_PREFIX = "section1.ficheportefeuille.table.6.";

    String FCHPORT_10_TABLE_7_PREFIX = "section1.ficheportefeuille.table.7.";


    @Option.Required
    Year targetYear();

    @Option.Singular
    List<ProgrammeRepartition> repartitionProgrammeVersionBs();

    @Option.Singular
    List<ProgrammeRepartition> repartitionProgrammes();

    @Option.Singular
    List<ProgrammeCentreResponsibilite> repartitionProgrammeCentreResps();

    @Option.Singular
    List<ProgrammeTitre> repartitionProgrammeTitres();

    PortefeuilleCentreResponsabiliteTitre repartitionCentreRespTitre();

    @Option.Singular
    List<ProgrammeEvolutionDepense> programmesEvolutionDepenses();


    @Option.Singular
    List<CentreRespEvoluPostes> postesEvolutions();

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
        writeRepartitionProgrammeB(document, context);
        writeDemarche(document, context);
        writeRepartitionProgramme(document, context);
        writeRepartitionProgrammeCTRRES(document, context);
        writeRepartitionProgrammeTitre(document, context);
        writeRepartitionCentreRespTitre(document, context);
        writeProgrammeAnnee(document, context);
        writeEvolutionPostesAnnee(document, context);
    }
    private static void writeTableTitle(XWPFDocument document, GenerationContext context, String titleKey) {
        final String tableTitleStyle = context.plainContent(STICKY_TITLE_STYLE_KEY);
        final XWPFParagraph table_1_title_para = document.createParagraph();
        table_1_title_para.setStyle(tableTitleStyle);
        table_1_title_para.createRun().setText(context.contextualizedContent(titleKey));

    }

    default void writeRepartitionProgrammeB(XWPFDocument document, GenerationContext context) {
        final String titleKey = FCHPORT_3_TABLE_1_PREFIX + "title";
        writeTableTitle(document, context, titleKey);

        List<ColumnExtractor<ProgrammeRepartition, ?>> extractors = ProgrammeRepartition.EXTRACTORS;
        List<ProgrammeRepartition> rows = repartitionProgrammeVersionBs();

        context.writeTable(
                document, FCHPORT_3_TABLE_1_PREFIX,
                rows, extractors, false);

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

    default void writeRepartitionProgramme(XWPFDocument document, GenerationContext context) {
        final String titleKey = FCHPORT_5_TABLE_2_PREFIX + "title";
        writeTableTitle(document, context, titleKey);

        List<ColumnExtractor<ProgrammeRepartition, ?>> extractors = ProgrammeRepartition.EXTRACTORS;
        List<ProgrammeRepartition> rows = repartitionProgrammes();
        context.writeTable(
                document, FCHPORT_3_TABLE_1_PREFIX,
                rows, extractors, false);

    }

    default void writeRepartitionProgrammeCTRRES(XWPFDocument document, GenerationContext context) {
        // table title
        final String titleKey = FCHPORT_6_TABLE_3_PREFIX + "title";
        writeTableTitle(document, context, titleKey);

        List<ProgrammeCentreResponsibilite> rows = repartitionProgrammeCentreResps();
        List<ColumnExtractor<ProgrammeCentreResponsibilite, ?>> extractors = ProgrammeCentreResponsibiliteBlueprint
                .extractor(rows);

        context.writeTable(
                document, FCHPORT_6_TABLE_3_PREFIX,
                rows, extractors, true);

    }

    default void writeRepartitionProgrammeTitre(XWPFDocument document, GenerationContext context) {
        if (LanguageDirection.LTR == context.getDirection()) {
            context.apply(PageLayout.LANDSCAPE);
        }
        // table title
        final String titleKey = FCHPORT_7_TABLE_4_PREFIX + "title";
        writeTableTitle(document, context, titleKey);


        List<ProgrammeTitre> rows = repartitionProgrammeTitres();
        List<ColumnExtractor<ProgrammeTitre, ?>> extractors = ProgrammeTitreBlueprint.extractors(rows);
        context.writeTable(
                document, FCHPORT_7_TABLE_4_PREFIX,
                rows, extractors, true);
    }

    default void writeRepartitionCentreRespTitre(XWPFDocument document, GenerationContext context) {

        // table title
        final String titleKey = FCHPORT_8_TABLE_5_PREFIX + "title";
        writeTableTitle(document, context, titleKey);
        
        var repartitions = repartitionCentreRespTitre();
        List<ColumnExtractor<CentreResponsabiliteTitre, ?>> extractors = repartitions.extractors();
        List<CentreResponsabiliteTitre> rows = repartitions.services();
        
       context.writeTable(
               document, FCHPORT_8_TABLE_5_PREFIX,
               rows, extractors, true);
    }

    default void writeProgrammeAnnee(XWPFDocument document, GenerationContext context) {

        // table title
        final String titleKey = FCHPORT_9_TABLE_6_PREFIX + "title";
        writeTableTitle(document, context, titleKey);

        List<ColumnExtractor<ProgrammeEvolutionDepense, ?>> extractors = ProgrammeEvolutionDepense.EXTRACTORS;
        List<ProgrammeEvolutionDepense> rows = programmesEvolutionDepenses();

        context.writeTable(
                document, FCHPORT_9_TABLE_6_PREFIX,
                rows, extractors, false);

    }

    default void writeEvolutionPostesAnnee(XWPFDocument document, GenerationContext context) {
        final String titleKey = FCHPORT_10_TABLE_7_PREFIX + "title";
        writeTableTitle(document, context, titleKey);

        List<ColumnExtractor<CentreRespEvoluPostes, ?>> extractors = CentreRespEvoluPostes.EXTRACTORS;
        List<CentreRespEvoluPostes> rows = postesEvolutions();

        context.writeTable(document, FCHPORT_10_TABLE_7_PREFIX, rows, extractors, false);

    }

}
