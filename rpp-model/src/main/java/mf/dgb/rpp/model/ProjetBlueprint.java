package mf.dgb.rpp.model;

import io.helidon.builder.api.Option;
import io.helidon.builder.api.Prototype;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Prototype.Blueprint
interface ProjetBlueprint {

    List<ColumnExtractor<Projet, ?>> EXTRACTORS = List.of(
            ColumnExtractor.ofUnsummable(Projet::name),
            ColumnExtractor.ofUnsummable(p->p.dateDebut().format(DateTimeFormatter.ISO_LOCAL_DATE)),
            ColumnExtractor.ofUnsummable(p->p.dateFin().format(DateTimeFormatter.ISO_LOCAL_DATE)),
            ColumnExtractor.ofSummable(Projet::coutEstGlobal),
            ColumnExtractor.ofUnsummable(p -> p.tauxAvancement().toString()),
            ColumnExtractor.ofConstable(p-> p.respectEcheanciers()? Bool.YES: Bool.NO),
            ColumnExtractor.ofSummable(Projet::AEreevaluationDemande),
            ColumnExtractor.ofSummable(Projet::cpAnnee),
            ColumnExtractor.ofSummable(Projet::cpAnneePlus1),
            ColumnExtractor.ofSummable(Projet::cpSuivant),
            ColumnExtractor.ofSummable(Projet::chargesRecurrentes)
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

    @Option.DefaultDouble(0d)
    Double tauxAvancement();

    @Option.DefaultBoolean(true)
    Boolean respectEcheanciers();

    @Option.DefaultLong(0L)
    Long AEreevaluationDemande();

    @Option.DefaultLong(0L)
    Long cpAnnee();

    @Option.DefaultLong(0L)
    Long cpAnneePlus1();

    @Option.DefaultLong(0L)
    Long cpSuivant();

    @Option.DefaultLong(0L)
    Long chargesRecurrentes();
}
