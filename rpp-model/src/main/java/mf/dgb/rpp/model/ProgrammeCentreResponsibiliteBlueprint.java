package mf.dgb.rpp.model;


import java.util.List;
import java.util.Objects;
import io.helidon.builder.api.Prototype;

import io.helidon.builder.api.Option;

@Prototype.Blueprint
@Prototype.CustomMethods(ProgrammeCentreResponsibiliteBlueprint.CustomMethods.class)
interface ProgrammeCentreResponsibiliteBlueprint {

    @Option.Required
    String name();

    @Option.DefaultBoolean(false)
    boolean specialCentreDeResponsabilite();

    @Option.DefaultLong(0L)
    Long serviceCentraux();

    @Option.DefaultLong(0L)
    Long serviceDeconcentree();

    @Option.DefaultLong(0L)
    Long organismeSousTutelle();

    @Option.DefaultLong(0L)
    Long autreOrganismeSousTutelle();

    @Option.DefaultLong(0L)
    Long organismeTerritoriaux();

    final class CustomMethods {
        @Prototype.BuilderMethod
        static void repartition(ProgrammeCentreResponsibilite.BuilderBase<?, ?> builder, List<Long> repartition) {
            Objects.requireNonNull(repartition, "repartition can not be null!");

            if (repartition.size() > 5) {
                throw new IllegalArgumentException(
                        "repartition shoul be of size 5 repartition size : " + repartition.size());
            }

            if(!builder.specialCentreDeResponsabilite() && (repartition.size() > 4)){
                throw new IllegalArgumentException(
                        "normal portefeuille shoul have only 4 repartition: " + repartition.size());
            }

            for (int i = 0; i < repartition.size(); i++) {
                switch (i) {
                    case 0 -> {
                        builder.serviceCentraux(repartition.get(i));
                    }
                    case 1 -> {
                        builder.serviceDeconcentree(repartition.get(i));
                    }
                    case 2 -> {
                        builder.organismeSousTutelle(repartition.get(i));
                    }
                    case 3 -> {
                        builder.organismeTerritoriaux(repartition.get(i));
                    }
                    case 4 ->{
                        builder.autreOrganismeSousTutelle(repartition.get(i));
                    }
                }
            }

        }

    }

}