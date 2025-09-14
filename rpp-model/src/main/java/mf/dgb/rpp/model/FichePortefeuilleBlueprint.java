package mf.dgb.rpp.model;

import java.time.Year;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.logging.log4j.util.Strings;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import io.helidon.builder.api.Option;
import io.helidon.builder.api.Prototype;

@Prototype.Blueprint
interface FichePortefeuilleBlueprint extends Writable {

    String FCHPORT_1_TITLE_KEY = "section1.ficheportefeuille.title.text";
    String FCHPORT_2_GEST_KEY = "section1.ficheportefeuille.gestionnaire.text";

    String FCHPORT_3_TABLE_1_STYLE_KEY = "section1.ficheportefeuille.table.styles.1";
    String FCHPORT_3_TABLE_1_TITLE_KEY = "section1.ficheportefeuille.table.1.title";
    String FCHPORT_3_TABLE_1_CONTENT = "section1.ficheportefeuille.table.1.";

    String FCHPORT_4_DEMARCHE_TITLE_KEY = "section1.ficheportefeuille.demarche.title.text";
    String FCHPORT_4_DEMARCHE_TEXT_KEYS = "section1.ficheportefeuille.demarche.text.";

    String FCHPORT_5_TABLE_2_STYLE_KEY = FCHPORT_3_TABLE_1_STYLE_KEY;// same style
    String FCHPORT_5_TABLE_2_TITLE_KEY = "section1.ficheportefeuille.table.2.title";
    String FCHPORT_5_TABLE_2_HEADER_KEY = FCHPORT_3_TABLE_1_CONTENT;// same headers

    String FCHPORT_6_TABLE_3_STYLE_KEY = "section1.ficheportefeuille.table.styles.2";
    String FCHPORT_6_TABLE_3_TITLE_KEY = "section1.ficheportefeuille.table.3.title";
    String FCHPORT_6_TABLE_3_CONTENT = "section1.ficheportefeuille.table.3.";

    String FCHPORT_7_TABLE_4_STYLE_KEY = FCHPORT_6_TABLE_3_STYLE_KEY; // same style
    String FCHPORT_7_TABLE_4_TITLE_KEY = "section1.ficheportefeuille.table.4.title";
    String FCHPORT_7_TABLE_4_CONTENT = "section1.ficheportefeuille.table.4.";

    String FCHPORT_8_TABLE_5_STYLE_KEY = FCHPORT_6_TABLE_3_STYLE_KEY; // same style
    String FCHPORT_8_TABLE_5_TITLE_KEY = "section1.ficheportefeuille.table.5.title";
    String FCHPORT_8_TABLE_5_CONTENT = FCHPORT_7_TABLE_4_CONTENT; // same headers, total

    String FCHPORT_9_TABLE_6_STYLE_KEY = FCHPORT_3_TABLE_1_STYLE_KEY; // same style
    String FCHPORT_9_TABLE_6_TITLE_KEY = "section1.ficheportefeuille.table.6.title";
    String FCHPORT_9_TABLE_6_CONTENT = "section1.ficheportefeuille.table.6.";

    public enum CentreDeReponsibilte {

        SERVICE_CENTRAUX,

        SERVICE_DECONCENTREE,

        ORGANISMES_SOUS_TUTELLE,

        ORGANISMES_TERRITORIAUX,

        AUTRE_ORGANISMES_SOUS_TUTELLE;

    }

    public record ProgrammeRepartition(String name, Long ae, Long cp) {
        
        //checking invariant (name, ae, cp)
        public ProgrammeRepartition {

            Objects.requireNonNull(name, "name can not be null!");
            Objects.requireNonNull(ae, "ae can't be null!");
            Objects.requireNonNull(cp, "cp can't be null!");

            if (Strings.isBlank(name)) {
                throw new IllegalArgumentException("name should not be blank name: " + name);
            }
            if (ae < 0 || cp < 0) {
                throw new IllegalArgumentException("ae and cp should not be negative! ae: "
                        + ae + " cp: " + cp);
            }

        }
    }

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

    @Option.Singular
    List<ProgrammeTitreCentreResponsibilite> repartitionTitreCentreResps();

    @Option.Singular
    List<ProgrammeEvolutionDepense> ProgrammesEvolutionDepenses();

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
        writeRepartitionProgrammeTTR(document, context);
        writeRepartitionTTRCTRRES(document, context);
        writeProgrammeAnnee(document, context);
    }

    default void writeRepartitionProgrammeB(XWPFDocument document, GenerationContext context) {

        String tableTitleStyle = context.plainContent(STICKY_TITLE_STYLE_KEY);
        XWPFParagraph table_1_title_para = document.createParagraph();
        table_1_title_para.setStyle(tableTitleStyle);
        table_1_title_para.createRun().setText(context.contextualizedContent(FCHPORT_3_TABLE_1_TITLE_KEY));
        List<ColumnExtractor<ProgrammeRepartition, ?>> extractors = List.of(
                ColumnExtractor.ofUnsummable(ProgrammeRepartition::name),
                ColumnExtractor.ofSummable(ProgrammeRepartition::ae),
                ColumnExtractor.ofSummable(ProgrammeRepartition::cp));
        List<ProgrammeRepartition> rows = repartitionProgrammeVersionBs();
        context.writeTable(
                document, FCHPORT_3_TABLE_1_STYLE_KEY, FCHPORT_3_TABLE_1_CONTENT,
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

        // table title
        String tableTitleStyle = context.plainContent(STICKY_TITLE_STYLE_KEY);
        XWPFParagraph table_title_para = document.createParagraph();
        table_title_para.setStyle(tableTitleStyle);
        table_title_para.createRun().setText(context.contextualizedContent(FCHPORT_5_TABLE_2_TITLE_KEY));

        List<ColumnExtractor<ProgrammeRepartition, ?>> extractors = List.of(
                ColumnExtractor.ofUnsummable(ProgrammeRepartition::name),
                ColumnExtractor.ofSummable(ProgrammeRepartition::ae),
                ColumnExtractor.ofSummable(ProgrammeRepartition::cp));
        List<ProgrammeRepartition> rows = repartitionProgrammes();
        context.writeTable(
                document, FCHPORT_3_TABLE_1_STYLE_KEY, FCHPORT_3_TABLE_1_CONTENT,
                rows, extractors, false);

    }

    default void writeRepartitionProgrammeCTRRES(XWPFDocument document, GenerationContext context) {

        // table title
        String tableTitleStyle = context.plainContent(STICKY_TITLE_STYLE_KEY);
        XWPFParagraph table_title_para = document.createParagraph();
        table_title_para.setStyle(tableTitleStyle);
        table_title_para.createRun().setText(context.contextualizedContent(FCHPORT_6_TABLE_3_TITLE_KEY));

        List<ColumnExtractor<ProgrammeCentreResponsibilite, ?>> extractors = List.of(
                ColumnExtractor.ofUnsummable(ProgrammeCentreResponsibilite::name),
                ColumnExtractor.ofSummable(r -> r.serviceCentraux()),
                ColumnExtractor.ofSummable(r -> r.serviceDeconcentree()),
                ColumnExtractor.ofSummable(r -> r.organismeSousTutelle()),
                ColumnExtractor.ofSummable(r -> r.organismeTerritoriaux()));
        List<ProgrammeCentreResponsibilite> rows = repartitionProgrammeCentreResps();
        context.writeTable(
                document, FCHPORT_6_TABLE_3_STYLE_KEY, FCHPORT_6_TABLE_3_CONTENT,
                rows, extractors, true);

    }

    default void writeRepartitionProgrammeTTR(XWPFDocument document, GenerationContext context) {
        if (LanguageDirection.LTR == context.getDirection()) {
            context.apply(PageLayout.LANDSCAPE);
        }
        // table title
        String tableTitleStyle = context.plainContent(STICKY_TITLE_STYLE_KEY);
        XWPFParagraph table_title_para = document.createParagraph();
        table_title_para.setStyle(tableTitleStyle);
        table_title_para.createRun().setText(context.contextualizedContent(FCHPORT_7_TABLE_4_TITLE_KEY));

        List<ProgrammeTitre> rows = repartitionProgrammeTitres();
        List<ColumnExtractor<ProgrammeTitre, ?>> extractors = rows.stream()
                .map(ProgrammeTitre::extractors)
                .findAny()
                .orElseThrow();
        context.writeTable(
                document, FCHPORT_7_TABLE_4_STYLE_KEY, FCHPORT_7_TABLE_4_CONTENT,
                rows, extractors, true);
    }

    default void writeRepartitionTTRCTRRES(XWPFDocument document, GenerationContext context) {

        // table title
        String tableTitleStyle = context.plainContent(STICKY_TITLE_STYLE_KEY);
        XWPFParagraph table_title_para = document.createParagraph();
        table_title_para.setStyle(tableTitleStyle);
        table_title_para.createRun().setText(context.contextualizedContent(FCHPORT_8_TABLE_5_TITLE_KEY));

        List<ColumnExtractor<ProgrammeTitreCentreResponsibilite, ?>> extractors = List.of(
                ColumnExtractor.ofUnsummable(ProgrammeTitreCentreResponsibilite::name),
                ColumnExtractor.ofSummable(r -> r.ttrs().get(0)),
                ColumnExtractor.ofSummable(r -> r.ttrs().get(1)),
                ColumnExtractor.ofSummable(r -> r.ttrs().get(2)),
                ColumnExtractor.ofSummable(r -> r.ttrs().get(3)));
        List<ProgrammeTitreCentreResponsibilite> rows = repartitionTitreCentreResps();
        context.writeTable(
                document, FCHPORT_8_TABLE_5_STYLE_KEY, FCHPORT_8_TABLE_5_CONTENT,
                rows, extractors, true);
    }

    default void writeProgrammeAnnee(XWPFDocument document, GenerationContext context) {

        // table title
        String tableTitleStyle = context.plainContent(STICKY_TITLE_STYLE_KEY);
        XWPFParagraph table_title_para = document.createParagraph();
        table_title_para.setStyle(tableTitleStyle);
        table_title_para.createRun().setText(context.contextualizedContent(FCHPORT_9_TABLE_6_TITLE_KEY));

        List<ColumnExtractor<ProgrammeEvolutionDepense, ?>> extractors = List.of(
                ColumnExtractor.ofUnsummable(ProgrammeEvolutionDepense::name),
                ColumnExtractor.ofSummable(ProgrammeEvolutionDepense::AnneeMoins2),
                ColumnExtractor.ofSummable(ProgrammeEvolutionDepense::AnneeMoins1),
                ColumnExtractor.ofSummable(ProgrammeEvolutionDepense::Annee),
                ColumnExtractor.ofSummable(ProgrammeEvolutionDepense::AnneePlus1),
                ColumnExtractor.ofSummable(ProgrammeEvolutionDepense::AnneePlus2));
        List<ProgrammeEvolutionDepense> rows = ProgrammesEvolutionDepenses();
        context.writeTable(
                document, FCHPORT_9_TABLE_6_STYLE_KEY, FCHPORT_9_TABLE_6_CONTENT,
                rows, extractors, false);

    }

}
