package mf.dgb.rpp.model;

import io.helidon.builder.api.Prototype;

import java.util.List;

import io.helidon.builder.api.Option;

@Prototype.Blueprint
interface FicheProgrammeBlueprint {

    RepartitionCentreResponsabiliteTitre repartitionCentreRespTitre();

    @Option.Singular
    List<RepartitionTitre> repartitionSousProgrammeTitres();

    @Option.Singular
    List<EvolutionDepense> sousProgrammeEvolutionDepenses();

    @Option.Access("")
    List<Projet> gpes();

    @Option.Access("")
    List<Projet> projets();

    @Option.Access("")
    List<NouveauProjet> nouveauGpes();

    @Option.Access("")
    List<NouveauProjet> nouveauProjets();

    PostOuvertsMassSalariales postMassSalariales();

    @Option.Singular
    List<EvolutionDepense> depensesOrganismesSousTutelles();

    List<EvolutionDepense> depensesTypeOrganismesSousTutelles();

}
