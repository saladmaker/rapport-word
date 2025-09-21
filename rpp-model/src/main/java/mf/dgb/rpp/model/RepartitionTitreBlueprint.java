package mf.dgb.rpp.model;

import io.helidon.builder.api.Prototype;

import java.util.List;
import java.util.Objects;

import io.helidon.builder.api.Option;
import java.util.ArrayList;

@Prototype.Blueprint(decorator = RepartitionTitreBlueprint.ProgrammeTitreDecorator.class)
@Prototype.CustomMethods(RepartitionTitreBlueprint.CustomMethods.class)
interface RepartitionTitreBlueprint {

    List<ColumnExtractor<RepartitionTitre, ?>> FIXED_EXTRACTORS = List.of(
            ColumnExtractor.ofUnsummable(RepartitionTitreBlueprint::name),
            ColumnExtractor.ofSummable(RepartitionTitreBlueprint::titre1),
            ColumnExtractor.ofSummable(RepartitionTitreBlueprint::titre2),
            ColumnExtractor.ofSummable(RepartitionTitreBlueprint::titre3),
            ColumnExtractor.ofSummable(RepartitionTitreBlueprint::titre4)
    );
    
    List<ColumnExtractor<RepartitionTitre, ?>> MF_EXTRACTOR = List.of(
            ColumnExtractor.ofSummable(RepartitionTitreBlueprint::titre5),
            ColumnExtractor.ofSummable(RepartitionTitreBlueprint::titre6),
            ColumnExtractor.ofSummable(RepartitionTitreBlueprint::titre7)
    );
    
    static List<ColumnExtractor<RepartitionTitre, ?>> extractors(List<RepartitionTitre> data){
        List<ColumnExtractor<RepartitionTitre, ?>> extractors = new ArrayList<>();
        extractors.addAll(FIXED_EXTRACTORS);
        boolean hasMF = data.stream().anyMatch(RepartitionTitre::isMF);
        if(hasMF){
            extractors.addAll(MF_EXTRACTOR);
        }
        return extractors;
    }

    @Option.Required
    String name();

    @Option.DefaultBoolean(false)
    boolean isMF();

    @Option.DefaultLong(0L)
    Long titre1();

    @Option.DefaultLong(0L)
    Long titre2();

    @Option.DefaultLong(0L)
    Long titre3();

    @Option.DefaultLong(0L)
    Long titre4();

    @Option.DefaultLong(0L)
    Long titre5();

    @Option.DefaultLong(0L)
    Long titre6();

    @Option.DefaultLong(0L)
    Long titre7();

 

    final class CustomMethods {

        @Prototype.BuilderMethod
        static void repartition(RepartitionTitre.BuilderBase<?, ?> builder, List<Long> repartitions) {
            Objects.requireNonNull(repartitions, "repartitions can not be null!");

            if (repartitions.size() > 7) {
                throw new IllegalArgumentException(
                        "repartition shoul be of size 7 repartition, found size: " + repartitions.size());
            }

            if (!builder.isMF() && (repartitions.size() > 4)) {
                throw new IllegalArgumentException(
                        "normal portefeuille should have only 4 titre : " + repartitions.size());
            }

            for (int i = 0; i < repartitions.size(); i++) {
                switch (i) {
                    case 0 -> {
                        builder.titre1(repartitions.get(i));
                    }
                    case 1 -> {
                        builder.titre2(repartitions.get(i));
                    }
                    case 2 -> {
                        builder.titre3(repartitions.get(i));
                    }
                    case 3 -> {
                        builder.titre4(repartitions.get(i));
                    }
                    case 4 -> {
                        builder.titre5(repartitions.get(i));
                    }
                    case 5 -> {
                        builder.titre6(repartitions.get(i));
                    }
                    case 6 -> {
                        builder.titre7(repartitions.get(i));
                    }
                }
            }
        }
    }

    public static class ProgrammeTitreDecorator implements Prototype.BuilderDecorator<RepartitionTitre.BuilderBase<?, ?>> {

        @Override
        public void decorate(RepartitionTitre.BuilderBase<?, ?> target) {
            if (target.isMF()) {
                Objects.requireNonNull(target.titre5(), "titre 5 should be set explicitly for MF");
                Objects.requireNonNull(target.titre6(), "titre 6 should be set explicitly for MF");
                Objects.requireNonNull(target.titre7(), "titre 7 should be set explicitly for MF");
            }
        }

    }

}
