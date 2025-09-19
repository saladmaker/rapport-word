package mf.dgb.rpp.model;

import io.helidon.builder.api.Prototype;

import java.util.List;
import java.util.Objects;

import io.helidon.builder.api.Option;
import java.util.ArrayList;

@Prototype.Blueprint(decorator = ProgrammeTitreBlueprint.ProgrammeTitreDecorator.class)
@Prototype.CustomMethods(ProgrammeTitreBlueprint.CustomMethods.class)
interface ProgrammeTitreBlueprint {

    List<ColumnExtractor<ProgrammeTitre, ?>> FIXED_EXTRACTORS = List.of(
            ColumnExtractor.ofUnsummable(ProgrammeTitreBlueprint::name),
            ColumnExtractor.ofSummable(ProgrammeTitreBlueprint::titre1),
            ColumnExtractor.ofSummable(ProgrammeTitreBlueprint::titre2),
            ColumnExtractor.ofSummable(ProgrammeTitreBlueprint::titre3),
            ColumnExtractor.ofSummable(ProgrammeTitreBlueprint::titre4)
    );
    
    List<ColumnExtractor<ProgrammeTitre, ?>> MF_EXTRACTOR = List.of(
            ColumnExtractor.ofSummable(ProgrammeTitreBlueprint::titre5),
            ColumnExtractor.ofSummable(ProgrammeTitreBlueprint::titre6),
            ColumnExtractor.ofSummable(ProgrammeTitreBlueprint::titre7)
    );
    
    static List<ColumnExtractor<ProgrammeTitre, ?>> extractors(List<ProgrammeTitre> data){
        List<ColumnExtractor<ProgrammeTitre, ?>> extractors = new ArrayList<>();
        extractors.addAll(FIXED_EXTRACTORS);
        boolean hasMF = data.stream().anyMatch(ProgrammeTitre::isMF);
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
        static void repartition(ProgrammeTitre.BuilderBase<?, ?> builder, List<Long> repartitions) {
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

    public static class ProgrammeTitreDecorator implements Prototype.BuilderDecorator<ProgrammeTitre.BuilderBase<?, ?>> {

        @Override
        public void decorate(ProgrammeTitre.BuilderBase<?, ?> target) {
            if (target.isMF()) {
                Objects.requireNonNull(target.titre5(), "titre 5 should be set explicitly for MF");
                Objects.requireNonNull(target.titre6(), "titre 6 should be set explicitly for MF");
                Objects.requireNonNull(target.titre7(), "titre 7 should be set explicitly for MF");
            }
        }

    }

}
