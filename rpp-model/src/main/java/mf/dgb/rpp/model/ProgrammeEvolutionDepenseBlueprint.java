package mf.dgb.rpp.model;

import java.util.List;
import java.util.Objects;

import io.helidon.builder.api.Prototype;
import io.helidon.builder.api.Option;

@Prototype.Blueprint
@Prototype.CustomMethods(ProgrammeEvolutionDepenseBlueprint.CustomMethods.class)
interface ProgrammeEvolutionDepenseBlueprint {

    List<ColumnExtractor<ProgrammeEvolutionDepense, ?>> EXTRACTORS = List.of(
                ColumnExtractor.ofUnsummable(ProgrammeEvolutionDepense::name),
                ColumnExtractor.ofSummable(ProgrammeEvolutionDepense::AnneeMoins2),
                ColumnExtractor.ofSummable(ProgrammeEvolutionDepense::AnneeMoins1),
                ColumnExtractor.ofSummable(ProgrammeEvolutionDepense::Annee),
                ColumnExtractor.ofSummable(ProgrammeEvolutionDepense::AnneePlus1),
                ColumnExtractor.ofSummable(ProgrammeEvolutionDepense::AnneePlus2));

    @Option.Required
    String name();

    @Option.DefaultLong(0L)
    Long AnneeMoins2();

    @Option.DefaultLong(0L)
    Long AnneeMoins1();

    @Option.DefaultLong(0L)
    Long Annee();

    @Option.DefaultLong(0L)
    Long AnneePlus1();

    @Option.DefaultLong(0L)
    Long AnneePlus2();

    final class CustomMethods {
        @Prototype.BuilderMethod
        static void evolution(ProgrammeEvolutionDepense.BuilderBase<?, ?> builderBase, List<Long> depenses) {

            Objects.requireNonNull(builderBase);
            Objects.requireNonNull(depenses);

            var size = depenses.size();

            if (size < 3 || size > 5) {
                throw new IllegalArgumentException("evolution des depenses should be of size 3 to 5, but got:" + depenses.size());
            }
            
            // set depense from y+2 to y-2
            switch (size) {
                case 3 -> {
                    builderBase.Annee(depenses.get(0));
                    builderBase.AnneePlus1(depenses.get(1));
                    builderBase.AnneePlus2(depenses.get(2));
                }
                case 4 -> {
                    builderBase.AnneeMoins1(depenses.get(0));
                    builderBase.Annee(depenses.get(1));
                    builderBase.AnneePlus1(depenses.get(2));
                    builderBase.AnneePlus2(depenses.get(3));

                }
                case 5 -> {
                    builderBase.AnneeMoins1(depenses.get(0));
                    builderBase.AnneeMoins2(depenses.get(1));
                    builderBase.Annee(depenses.get(2));
                    builderBase.AnneePlus1(depenses.get(3));
                    builderBase.AnneePlus2(depenses.get(4));
                }
            }

        }

    }

}