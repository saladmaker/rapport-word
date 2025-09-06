package rpp.poi.model;

import io.helidon.builder.api.Prototype;

import java.util.List;
import java.util.Objects;

import io.helidon.builder.api.Option;

@Prototype.Blueprint
@Prototype.CustomMethods(ProgrammeCentreResponsibiliteBlueprint.CustomMethods.class)
interface ProgrammeCentreResponsibiliteBlueprint {

    @Option.Required
    String name();

    @Option.DefaultLong(0L)
    Long serviceCentraux();

    @Option.DefaultLong(0L)
    Long serviceDeconcentree();

    @Option.DefaultLong(0L)
    Long organismeSousTutelle();

    @Option.DefaultLong(0L)
    Long organismeTerritoriaux();

    final class CustomMethods {
        @Prototype.BuilderMethod
        static void repartition(ProgrammeCentreResponsibilite.BuilderBase<?, ?> builder, List<Long> repartition) {
            Objects.requireNonNull(repartition, "repartition can not be null!");
            if (repartition.size() > 4) {
                throw new IllegalArgumentException(
                        "repartition shoul be of size 4 repartition size : " + repartition.size());
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
                }
            }
        }

    }

}