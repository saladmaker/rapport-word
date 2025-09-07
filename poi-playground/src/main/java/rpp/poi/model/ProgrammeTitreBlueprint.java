package rpp.poi.model;

import io.helidon.builder.api.Prototype;
import rpp.poi.model.ProgrammeTitre.BuilderBase;

import java.util.List;
import java.util.Objects;

import io.helidon.builder.api.Option;
import java.util.ArrayList;

@Prototype.Blueprint(decorator = ProgrammeTitreBlueprint.ProgrammeTitreDecorator.class)
@Prototype.CustomMethods(ProgrammeTitreBlueprint.CustomMethods.class)
interface ProgrammeTitreBlueprint extends Extractable<ProgrammeTitre>{

    @Option.Required
    String name();

    @Option.DefaultBoolean(false)
    boolean isMF();

    @Option.DefaultLong(0L)
    Long t1();

    @Option.DefaultLong(0L)
    Long t2();

    @Option.DefaultLong(0L)
    Long t3();

    @Option.DefaultLong(0L)
    Long t4();

    @Option.DefaultLong(0L)
    Long t5();

    @Option.DefaultLong(0L)
    Long t6();

    @Option.DefaultLong(0L)
    Long t7();

    @Override
    default List<ColumnExtractor<ProgrammeTitre, ?>> extractors() {
        
        List<ColumnExtractor<ProgrammeTitre, ?>> fixed = List.of(
                ColumnExtractor.ofUnsummable(ProgrammeTitreBlueprint::name),
                ColumnExtractor.ofSummable(ProgrammeTitreBlueprint::t1),
                ColumnExtractor.ofSummable(ProgrammeTitreBlueprint::t2),
                ColumnExtractor.ofSummable(ProgrammeTitreBlueprint::t3),
                ColumnExtractor.ofSummable(ProgrammeTitreBlueprint::t4)
        );
        if (isMF()) {
            List<ColumnExtractor<ProgrammeTitre, ?>> availableExtractors = new ArrayList<>();
            availableExtractors.addAll(fixed);
            availableExtractors.add(ColumnExtractor.ofSummable(ProgrammeTitreBlueprint::t5));
            availableExtractors.add(ColumnExtractor.ofSummable(ProgrammeTitreBlueprint::t6));
            availableExtractors.add(ColumnExtractor.ofSummable(ProgrammeTitreBlueprint::t7));
            return availableExtractors;
        }
        return fixed;

    }

    final class CustomMethods {

        @Prototype.BuilderMethod
        static void repartition(ProgrammeTitre.BuilderBase<?, ?> builder, List<Long> repartitions) {
            Objects.requireNonNull(repartitions, "repartitions can not be null!");
            if (repartitions.size() > 7) {
                throw new IllegalArgumentException(
                        "repartition shoul be of size 7 repartition size : " + repartitions.size());
            }
            for (int i = 0; i < repartitions.size(); i++) {
                switch (i) {
                    case 0 -> {
                        builder.t1(repartitions.get(i));
                    }
                    case 1 -> {
                        builder.t2(repartitions.get(i));
                    }
                    case 2 -> {
                        builder.t3(repartitions.get(i));
                    }
                    case 3 -> {
                        builder.t4(repartitions.get(i));
                    }
                    case 4 -> {
                        builder.t5(repartitions.get(i));
                    }
                    case 5 -> {
                        builder.t6(repartitions.get(i));
                    }
                    case 6 -> {
                        builder.t7(repartitions.get(i));
                    }
                }
            }
        }
    }

    public static class ProgrammeTitreDecorator implements Prototype.BuilderDecorator<ProgrammeTitre.BuilderBase<?, ?>> {

        @Override
        public void decorate(BuilderBase<?, ?> target) {
            if (target.isMF()) {
                Objects.requireNonNull(target.t5(), "titre 5 should be set explicitly for MF");
                Objects.requireNonNull(target.t6(), "titre 6 should be set explicitly for MF");
                Objects.requireNonNull(target.t7(), "titre 7 should be set explicitly for MF");
            }
        }

    }

}
