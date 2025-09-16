package mf.dgb.rpp.model;

import io.helidon.builder.api.Prototype;

import java.util.List;
import java.util.Objects;

import io.helidon.builder.api.Option;

@Prototype.Blueprint
@Prototype.CustomMethods(CentreRespEvoluPostesBlueprint.CustomMethods.class)
interface CentreRespEvoluPostesBlueprint {

    List<ColumnExtractor<CentreRespEvoluPostes, ?>> EXTRACTORS = List.of(
            ColumnExtractor.ofConstable(CentreRespEvoluPostes::serviceType),
            ColumnExtractor.ofSummable(CentreRespEvoluPostes::AnneeMoins2),
            ColumnExtractor.ofSummable(CentreRespEvoluPostes::AnneeMoins1),
            ColumnExtractor.ofSummable(CentreRespEvoluPostes::Annee),
            ColumnExtractor.ofSummable(CentreRespEvoluPostes::AnneePlus1),
            ColumnExtractor.ofSummable(CentreRespEvoluPostes::AnneePlus2));

    @Option.Required
    CentreResponsabilite serviceType();

    @Option.DefaultInt(0)
    long AnneeMoins2();

    @Option.DefaultInt(0)
    long AnneeMoins1();

    @Option.DefaultInt(0)
    long Annee();

    @Option.DefaultInt(0)
    long AnneePlus1();

    @Option.DefaultInt(0)
    long AnneePlus2();

    final class CustomMethods {
        @Prototype.FactoryMethod
        static CentreRespEvoluPostes servicesCentraux(List<Long> evolutions) {
            return CentreRespEvoluPostes.builder()
                    .serviceType(CentreResponsabilite.SERVICES_CENTRAUX)
                    .evolution(evolutions)
                    .build();
        }

        @Prototype.FactoryMethod
        static CentreRespEvoluPostes servicesDeconcentres(List<Long> evolutions) {
            return CentreRespEvoluPostes.builder()
                    .serviceType(CentreResponsabilite.SERVICES_DECONCENTRES)
                    .evolution(evolutions)
                    .build();
        }

        @Prototype.FactoryMethod
        static CentreRespEvoluPostes organesTerritoriaux(List<Long> evolutions) {
            return CentreRespEvoluPostes.builder()
                    .serviceType(CentreResponsabilite.ORGANES_TERRITORIAUX)
                    .evolution(evolutions)
                    .build();
        }

        @Prototype.FactoryMethod
        static CentreRespEvoluPostes organismesSousTutelle(List<Long> evolutions) {
            return CentreRespEvoluPostes.builder()
                    .serviceType(CentreResponsabilite.ORGANISMES_SOUS_TUTELLE)
                    .evolution(evolutions)
                    .build();
        }

        @Prototype.FactoryMethod
        static CentreRespEvoluPostes autreOrganismesSousTutelle(List<Long> evolutions) {
            return CentreRespEvoluPostes.builder()
                    .serviceType(CentreResponsabilite.AUTRE_ORGANISMES_SOUS_TUTELLE)
                    .evolution(evolutions)
                    .build();
        }

        @Prototype.BuilderMethod
        static void evolution(CentreRespEvoluPostes.BuilderBase<?, ?> builderBase, List<Long> evolutions) {
            Objects.requireNonNull(evolutions);

            int evolutionsSize = evolutions.size();
            if (evolutionsSize < 3 || evolutionsSize > 5) {
                throw new IllegalArgumentException("evolution de postes should a size of 3-5, found:" + evolutions);
            }

            switch (evolutionsSize) {
                case 3 -> {
                    builderBase.Annee(evolutions.get(0));
                    builderBase.AnneePlus1(evolutions.get(1));
                    builderBase.AnneePlus2(evolutions.get(2));
                }

                case 4 -> {
                    builderBase.AnneeMoins1(evolutions.get(0));
                    builderBase.Annee(evolutions.get(1));
                    builderBase.AnneePlus1(evolutions.get(2));
                    builderBase.AnneePlus2(evolutions.get(3));
                }
                case 5 -> {
                    builderBase.AnneeMoins2(evolutions.get(0));
                    builderBase.AnneeMoins1(evolutions.get(1));
                    builderBase.Annee(evolutions.get(2));
                    builderBase.AnneePlus1(evolutions.get(3));
                    builderBase.AnneePlus2(evolutions.get(4));
                }
            }
        }

    }
}
