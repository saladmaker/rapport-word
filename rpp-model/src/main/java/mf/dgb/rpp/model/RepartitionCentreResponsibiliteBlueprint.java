package mf.dgb.rpp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import io.helidon.builder.api.Prototype;

import io.helidon.builder.api.Option;

@Prototype.Blueprint
@Prototype.CustomMethods(RepartitionCentreResponsibiliteBlueprint.CustomMethods.class)
interface RepartitionCentreResponsibiliteBlueprint {

    List<ColumnExtractor<RepartitionCentreResponsibilite, ?>> FIXED_EXTRACTORS = List.of(
            ColumnExtractor.ofUnsummable(RepartitionCentreResponsibilite::name),
            ColumnExtractor.ofSummable(RepartitionCentreResponsibilite::servicesCentraux),
            ColumnExtractor.ofSummable(RepartitionCentreResponsibilite::servicesDeconcentres),
            ColumnExtractor.ofSummable(RepartitionCentreResponsibilite::organismesSousTutelle),
            ColumnExtractor.ofSummable(RepartitionCentreResponsibilite::organesTerritoriaux));

    ColumnExtractor<RepartitionCentreResponsibilite, ?> AUTRE_OST_EXTRACTOR = ColumnExtractor
            .ofSummable(RepartitionCentreResponsibilite::autreOrganismesSousTutelle);

    public static List<ColumnExtractor<RepartitionCentreResponsibilite, ?>> extractor(
            List<RepartitionCentreResponsibilite> data) {

        List<ColumnExtractor<RepartitionCentreResponsibilite, ?>> extractors = new ArrayList<>();
        extractors.addAll(FIXED_EXTRACTORS);
        boolean specialAutreOrganismeSousTutelle = data.stream()
                .anyMatch(RepartitionCentreResponsibilite::specialCentreDeResponsabilite);
        if (specialAutreOrganismeSousTutelle) {
            extractors.add(AUTRE_OST_EXTRACTOR);
        }
        return extractors;
    }

    @Option.Required
    String name();

    @Option.DefaultBoolean(false)
    boolean specialCentreDeResponsabilite();

    @Option.DefaultLong(0L)
    Long servicesCentraux();

    @Option.DefaultLong(0L)
    Long servicesDeconcentres();

    @Option.DefaultLong(0L)
    Long organismesSousTutelle();

    @Option.DefaultLong(0L)
    Long autreOrganismesSousTutelle();

    @Option.DefaultLong(0L)
    Long organesTerritoriaux();

    final class CustomMethods {

        @Prototype.FactoryMethod
        static RepartitionCentreResponsibilite create(String name, List<Long> repartition) {
            return RepartitionCentreResponsibilite.builder()
                    .name(name)
                    .repartition(repartition)
                    .build();
        }

        @Prototype.BuilderMethod
        static void repartition(RepartitionCentreResponsibilite.BuilderBase<?, ?> builder, List<Long> repartition) {
            Objects.requireNonNull(repartition, "repartition can not be null!");

            if (repartition.size() > 5) {
                throw new IllegalArgumentException(
                        "repartition shoul be of size 5 repartition size : " + repartition.size());
            }

            if (!builder.specialCentreDeResponsabilite() && (repartition.size() > 4)) {
                throw new IllegalArgumentException(
                        "normal portefeuille shoul have only 4 repartition: " + repartition.size());
            }

            for (int i = 0; i < repartition.size(); i++) {
                switch (i) {
                    case 0 -> {
                        builder.servicesCentraux(repartition.get(i));
                    }
                    case 1 -> {
                        builder.servicesDeconcentres(repartition.get(i));
                    }
                    case 2 -> {
                        builder.organismesSousTutelle(repartition.get(i));
                    }
                    case 3 -> {
                        builder.organesTerritoriaux(repartition.get(i));
                    }
                    case 4 -> {
                        builder.autreOrganismesSousTutelle(repartition.get(i));
                    }
                }
            }

        }

    }

}