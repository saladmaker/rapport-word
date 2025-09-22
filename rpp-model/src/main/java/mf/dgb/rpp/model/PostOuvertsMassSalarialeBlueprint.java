package mf.dgb.rpp.model;


import io.helidon.builder.api.Option;
import io.helidon.builder.api.Prototype;

import java.util.List;
import java.util.Objects;

@Prototype.Blueprint
@Prototype.CustomMethods(PostOuvertsMassSalarialeBlueprint.CustomMethods.class)
interface PostOuvertsMassSalarialeBlueprint {
    List<ColumnExtractor<PostOuvertsMassSalariale, ?>> EXTRACTORS = List.of(
            ColumnExtractor.ofConstable(PostOuvertsMassSalariale::serviceType),
            ColumnExtractor.ofSummable(PostOuvertsMassSalariale::postesAnneeMoins2),
            ColumnExtractor.ofSummable(PostOuvertsMassSalariale::postesAnneeMoins1),
            ColumnExtractor.ofSummable(PostOuvertsMassSalariale::postesAnnee),
            ColumnExtractor.ofSummable(PostOuvertsMassSalariale::nombre),
            ColumnExtractor.ofUnsummable(p -> p.variationPostes().toString()),
            ColumnExtractor.ofSummable()
    );

    @Option.Required
    CentreResponsabilite serviceType();

    @Option.DefaultLong(0L)
    Long postesAnneeMoins2();

    @Option.DefaultLong(0L)
    Long postesAnneeMoins1();

    @Option.Required
    Long postesAnnee();

    @Option.DefaultLong(0L)
    Long salairAnneeMoins2();

    @Option.DefaultLong(0L)
    Long salairAnneeMoins1();

    @Option.Required
    Long salairAnnee();

    default Long nombre() {
        return postesAnnee() - postesAnneeMoins1();
    }

    default Double variationPostes() {
        return nombre().doubleValue() * 100 / postesAnnee();
    }

    default Long variationSalarial(){
        return salairAnnee() - salairAnneeMoins1();
    }


    final class CustomMethods {
        @Prototype.BuilderMethod
        static void evolutionPostes(PostOuvertsMassSalariale.BuilderBase<?, ?> builderBase, List<Long> postes) {
            //invariants check
            Objects.requireNonNull(postes);
            int size = postes.size();
            if ((0 == size) || size > 3) {
                throw new IllegalArgumentException("evolution des postes size must be of 1-3, found" + size);
            }

            switch (size) {
                case 3 -> {
                    builderBase.postesAnneeMoins2(postes.get(0));
                    builderBase.postesAnneeMoins1(postes.get(1));
                    builderBase.postesAnnee(postes.get(2));

                }
                case 2 -> {
                    builderBase.postesAnneeMoins1(postes.get(0));
                    builderBase.postesAnnee(postes.get(1));
                }
                case 1 -> {
                    builderBase.postesAnnee(postes.getFirst());
                }
            }
        }

        @Prototype.BuilderMethod
        static void evolutionSalarial(PostOuvertsMassSalariale.BuilderBase<?, ?> builderBase, List<Long> salaires) {
            //invariants check
            Objects.requireNonNull(salaires);
            int size = salaires.size();
            if ((0 == size) || size > 3) {
                throw new IllegalArgumentException("evolution des postes size must be of 1-3, found" + size);
            }

            switch (size) {
                case 3 -> {
                    builderBase.salairAnneeMoins2(salaires.get(0));
                    builderBase.salairAnneeMoins1(salaires.get(1));
                    builderBase.salairAnnee(salaires.get(2));

                }
                case 2 -> {
                    builderBase.salairAnneeMoins1(salaires.get(0));
                    builderBase.salairAnnee(salaires.get(1));
                }
                case 1 -> {
                    builderBase.salairAnnee(salaires.getFirst());
                }
            }
        }

        @Prototype.FactoryMethod
        static PostOuvertsMassSalariale create(CentreResponsabilite centreResponsabilite, List<Long> postes, List<Long> salaires) {

            return PostOuvertsMassSalariale.builder()
                    .serviceType(centreResponsabilite)
                    .evolutionPostes(postes)
                    .evolutionSalarial(salaires)
                    .build();
        }
    }
}
