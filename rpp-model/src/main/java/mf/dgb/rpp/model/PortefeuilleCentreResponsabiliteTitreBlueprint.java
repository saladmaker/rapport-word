package mf.dgb.rpp.model;

import io.helidon.builder.api.Prototype;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.helidon.builder.api.Option;

@Prototype.Blueprint
@Prototype.CustomMethods(PortefeuilleCentreResponsabiliteTitreBlueprint.CustomMethods.class)
interface PortefeuilleCentreResponsabiliteTitreBlueprint {

    List<ColumnExtractor<CentreResponsabiliteTitre, ?>> FIXED_EXTRACTORS = List.of(
            ColumnExtractor.ofConstable(CentreResponsabiliteTitre::serviceType),
            ColumnExtractor.ofSummable(CentreResponsabiliteTitre::titre1),
            ColumnExtractor.ofSummable(CentreResponsabiliteTitre::titre2),
            ColumnExtractor.ofSummable(CentreResponsabiliteTitre::titre3),
            ColumnExtractor.ofSummable(CentreResponsabiliteTitre::titre4)
    );

    List<ColumnExtractor<CentreResponsabiliteTitre, ?>> MF_EXTRACTORS = List.of(
            ColumnExtractor.ofSummable(CentreResponsabiliteTitre::titre5),
            ColumnExtractor.ofSummable(CentreResponsabiliteTitre::titre6),
            ColumnExtractor.ofSummable(CentreResponsabiliteTitre::titre7)
    );


    @Option.DefaultBoolean(false)
    boolean isMF();

    @Option.DefaultBoolean(false)
    boolean specialCentreDeResponsabilite();

    @Option.Access("")
    List<CentreResponsabiliteTitre> services();

    default List<ColumnExtractor<CentreResponsabiliteTitre, ?>> extractors(){
        List<ColumnExtractor<CentreResponsabiliteTitre, ?>> extractors = new ArrayList<>();
        extractors.addAll(FIXED_EXTRACTORS);
        if(isMF()){
            extractors.addAll(MF_EXTRACTORS);
        }
        return extractors;
    }

    final class CustomMethods {

        @Prototype.BuilderMethod
        static void service(PortefeuilleCentreResponsabiliteTitre.BuilderBase<?, ?> builderBase,
                CentreResponsabiliteTitre service) {
            Objects.requireNonNull(builderBase);
            Objects.requireNonNull(service);
            var existingTypes = builderBase.services();
            var type = service.serviceType();

            //only mf should have t4, t5, t7 for services centraux
            if (service.hasSpecialTitres()) {
                if (!(builderBase.isMF()) || (service.serviceType() != CentreResponsabilite.SERVICES_CENTRAUX)) {
                    throw new IllegalArgumentException("titre 5, titre 6, titre 7 should only be set for service centraux of mf, found: " + service);
                }
            }
            //only portefeuille with 
            if(CentreResponsabilite.AUTRE_ORGANISMES_SOUS_TUTELLE == service.serviceType()){
                if(!builderBase.specialCentreDeResponsabilite()){
                    throw new IllegalArgumentException("only portefeuille with special centre responsabilite could such a service type, found:" + service);
                }
            }

            //check that we don't have two instance of the same service type
            if (exists(existingTypes, type)) {
                throw new IllegalArgumentException("service type already exist, exist:" + existingTypes + ", type:" + service);
            }

            //overwrite previous
            var all = new ArrayList<CentreResponsabiliteTitre>();
            all.addAll(existingTypes);
            all.add(service);
            builderBase.services(all);

        }

        private static boolean exists(List<CentreResponsabiliteTitre> types, CentreResponsabilite type) {
            return types.stream()
                    .map(CentreResponsabiliteTitre::serviceType)
                    .anyMatch(r -> r.equals(type));
        }
    }
}
