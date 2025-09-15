package mf.dgb.rpp.model;

import io.helidon.builder.api.Prototype;

import java.util.List;
import java.util.Objects;

import io.helidon.builder.api.Option;

@Prototype.Blueprint(decorator = CentreResponsabiliteTitreBlueprint.CentreResponsabiliteTitreDecorator.class)
@Prototype.CustomMethods(CentreResponsabiliteTitreBlueprint.CustomMethods.class)
interface CentreResponsabiliteTitreBlueprint {

    @Option.Required
    CentreResponsabilite serviceType();

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

    default boolean hasSpecialTitres() {
        return titre5() != 0
                || titre6() != 0
                || titre7() != 0;
    }

    public static class CentreResponsabiliteTitreDecorator
            implements Prototype.BuilderDecorator<CentreResponsabiliteTitre.BuilderBase<?, ?>> {

        @Override
        public void decorate(CentreResponsabiliteTitre.BuilderBase<?, ?> builderBase) {
            if (builderBase.serviceType().isPresent()) {
                var serviceType = builderBase.serviceType().get();
                boolean hasSpecialTitre = (builderBase.titre5() != 0) ||
                        (builderBase.titre6() != 0) ||
                        (builderBase.titre7() != 0);
                if (!(CentreResponsabilite.SERVICE_CENTRAUX == serviceType) && hasSpecialTitre) {
                    throw new IllegalArgumentException(
                            "repartition size for non service centraux should be 4, found: service type "
                                    + serviceType);

                }
            }

        }
    }

    final class CustomMethods {

        @Prototype.BuilderMethod
        static void repartition(CentreResponsabiliteTitre.BuilderBase<?, ?> builderBase, List<Long> repartition) {

            Objects.requireNonNull(builderBase);
            Objects.requireNonNull(repartition);

            var repartitionSize = repartition.size();

            // repartition should have t1, t2, t3, t4, [t5, t6, t7]
            if (repartitionSize < 4 || repartitionSize > 7) {
                throw new IllegalArgumentException("repartition size should have a size of 4-7, found: "
                        + repartition.size() + " repartition:" + repartition.toString());
            }

            // if service type was set and it is indeed of services centraux it should have
            // only t1, t2, t3, t4
            if (builderBase.serviceType().isPresent()) {
                var serviceType = builderBase.serviceType().get();
                if (!(CentreResponsabilite.SERVICE_CENTRAUX == serviceType) && repartitionSize > 4) {
                    throw new IllegalArgumentException(
                            "repartition size for non service centraux should be 4, found: service type " + serviceType
                                    + " size:"
                                    + repartition + " repartition:" + repartition.toString());

                }
            }

            for (int i = 0; i < repartition.size(); i++) {

                switch (i) {

                    case 0 -> {
                        builderBase.titre1(repartition.get(i));
                    }
                    case 1 -> {
                        builderBase.titre2(repartition.get(i));
                    }
                    case 2 -> {
                        builderBase.titre3(repartition.get(i));
                    }
                    case 3 -> {
                        builderBase.titre4(repartition.get(i));
                    }
                    case 4 -> {
                        builderBase.titre5(repartition.get(i));
                    }
                    case 5 -> {
                        builderBase.titre6(repartition.get(i));
                    }
                    case 6 -> {
                        builderBase.titre7(repartition.get(i));
                    }

                }
            }
        }

        @Prototype.FactoryMethod
        static CentreResponsabiliteTitre create(CentreResponsabilite serviceType, List<Long> repartition) {
            return CentreResponsabiliteTitre.builder()
                    .serviceType(serviceType)
                    .repartition(repartition)
                    .build();
        }

        @Prototype.FactoryMethod
        static CentreResponsabiliteTitre servicesCentraux(List<Long> repartition) {
            return CentreResponsabiliteTitre.builder()
                    .serviceType(CentreResponsabilite.SERVICE_CENTRAUX)
                    .repartition(repartition).build();
        }

        @Prototype.FactoryMethod
        static CentreResponsabiliteTitre serviceDeconcentrees(List<Long> repartition) {
            return CentreResponsabiliteTitre.builder()
                    .serviceType(CentreResponsabilite.SERVICE_DECONCENTREE)
                    .repartition(repartition).build();
        }

        @Prototype.FactoryMethod
        static CentreResponsabiliteTitre organismesSousTutelle(List<Long> repartition) {
            return CentreResponsabiliteTitre.builder()
                    .serviceType(CentreResponsabilite.ORGANISMES_SOUS_TUTELLE)
                    .repartition(repartition).build();
        }

        @Prototype.FactoryMethod
        static CentreResponsabiliteTitre organismesTerritotiaux(List<Long> repartition) {
            return CentreResponsabiliteTitre.builder()
                    .serviceType(CentreResponsabilite.ORGANISMES_TERRITORIAUX)
                    .repartition(repartition).build();
        }

        @Prototype.FactoryMethod
        static CentreResponsabiliteTitre autreOrganismesSousTutelle(List<Long> repartition) {
            return CentreResponsabiliteTitre.builder()
                    .serviceType(CentreResponsabilite.AUTRE_ORGANISMES_SOUS_TUTELLE)
                    .repartition(repartition).build();
        }

        @Prototype.FactoryMethod
        static CentreResponsabiliteTitre emptyServicesCentraux() {
            return CentreResponsabiliteTitre.builder()
                    .serviceType(CentreResponsabilite.SERVICE_CENTRAUX)
                    .build();
        }

        @Prototype.FactoryMethod
        static CentreResponsabiliteTitre emptyServiceDeconcentrees() {
            return CentreResponsabiliteTitre.builder()
                    .serviceType(CentreResponsabilite.SERVICE_DECONCENTREE)
                    .build();
        }

        @Prototype.FactoryMethod
        static CentreResponsabiliteTitre emptyOrganismesSousTutelle() {
            return CentreResponsabiliteTitre.builder()
                    .serviceType(CentreResponsabilite.ORGANISMES_SOUS_TUTELLE)
                    .build();
        }

        @Prototype.FactoryMethod
        static CentreResponsabiliteTitre emptyOrganismesTerritotiaux() {
            return CentreResponsabiliteTitre.builder()
                    .serviceType(CentreResponsabilite.ORGANISMES_TERRITORIAUX)
                    .build();
        }

        @Prototype.FactoryMethod
        static CentreResponsabiliteTitre emptyAutreOrganismesSousTutelle() {
            return CentreResponsabiliteTitre.builder()
                    .serviceType(CentreResponsabilite.AUTRE_ORGANISMES_SOUS_TUTELLE)
                    .build();
        }
    }

}
