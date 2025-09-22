package mf.dgb.rpp.model;

import java.util.List;
import java.util.Objects;

import io.helidon.builder.api.Prototype;
import io.helidon.builder.api.Option;

@Prototype.Blueprint
@Prototype.CustomMethods(EvolutionDepenseBlueprint.CustomMethods.class)
interface EvolutionDepenseBlueprint {

    List<ColumnExtractor<EvolutionDepense, ?>> EXTRACTORS = List.of(
                ColumnExtractor.ofUnsummable(EvolutionDepense::name),
                ColumnExtractor.ofSummable(EvolutionDepense::anneeMoins2),
                ColumnExtractor.ofSummable(EvolutionDepense::anneeMoins1),
                ColumnExtractor.ofSummable(EvolutionDepense::annee),
                ColumnExtractor.ofSummable(EvolutionDepense::anneePlus1),
                ColumnExtractor.ofSummable(EvolutionDepense::anneePlus2));

    @Option.Required
    String name();

    @Option.DefaultLong(0L)
    Long anneeMoins2();

    @Option.DefaultLong(0L)
    Long anneeMoins1();

    @Option.DefaultLong(0L)
    Long annee();

    @Option.DefaultLong(0L)
    Long anneePlus1();

    @Option.DefaultLong(0L)
    Long anneePlus2();

    final class CustomMethods {
        @Prototype.BuilderMethod
        static void evolution(EvolutionDepense.BuilderBase<?, ?> builderBase, List<Long> depenses) {

            Objects.requireNonNull(builderBase);
            Objects.requireNonNull(depenses);

            var size = depenses.size();

            if ((size < 3) || (size > 5)) {
                throw new IllegalArgumentException("evolution des depenses should be of size 3 to 5, but got:" + depenses.size());
            }
            
            // set depense from y+2 to y-2
            switch (size) {
                case 3 -> {
                    builderBase.annee(depenses.get(0));
                    builderBase.anneePlus1(depenses.get(1));
                    builderBase.anneePlus2(depenses.get(2));
                }
                case 4 -> {
                    builderBase.anneeMoins1(depenses.get(0));
                    builderBase.annee(depenses.get(1));
                    builderBase.anneePlus1(depenses.get(2));
                    builderBase.anneePlus2(depenses.get(3));

                }
                case 5 -> {
                    builderBase.anneeMoins1(depenses.get(0));
                    builderBase.anneeMoins2(depenses.get(1));
                    builderBase.annee(depenses.get(2));
                    builderBase.anneePlus1(depenses.get(3));
                    builderBase.anneePlus2(depenses.get(4));
                }
            }

        }

    }

}