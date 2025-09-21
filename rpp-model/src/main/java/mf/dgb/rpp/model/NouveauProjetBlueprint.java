package mf.dgb.rpp.model;

import io.helidon.builder.api.Option;
import io.helidon.builder.api.Prototype;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Prototype.Blueprint
interface NouveauProjetBlueprint {

    List<ColumnExtractor<NouveauProjet, ?>> EXTRACTORS = List.of(
            ColumnExtractor.ofUnsummable(NouveauProjet::name),
            ColumnExtractor.ofUnsummable(p->p.dateDebut().format(DateTimeFormatter.ISO_LOCAL_DATE)),
            ColumnExtractor.ofUnsummable(p->p.dateFin().format(DateTimeFormatter.ISO_LOCAL_DATE)),
            ColumnExtractor.ofSummable(NouveauProjet::coutEstGlobal),
            ColumnExtractor.ofSummable(NouveauProjet::AEDemande),
            ColumnExtractor.ofSummable(NouveauProjet::cpAnnee),
            ColumnExtractor.ofSummable(NouveauProjet::cpAnneePlus1),
            ColumnExtractor.ofSummable(NouveauProjet::cpSuivant),
            ColumnExtractor.ofSummable(NouveauProjet::chargesRecurrentes)
    );
    @Option.Required
    TypeProjet type();

    @Option.Required
    String name();

    @Option.Required
    LocalDate dateDebut();

    @Option.Required
    LocalDate dateFin();

    @Option.DefaultLong(0L)
    Long coutEstGlobal();

    @Option.DefaultLong(0L)
    Long AEDemande();

    @Option.DefaultLong(0L)
    Long cpAnnee();

    @Option.DefaultLong(0L)
    Long cpAnneePlus1();

    @Option.DefaultLong(0L)
    Long cpSuivant();

    @Option.DefaultLong(0L)
    Long chargesRecurrentes();
}
