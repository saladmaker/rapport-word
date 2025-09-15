package mf.dgb.rpp.model;

import io.helidon.builder.api.Prototype;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.helidon.builder.api.Option;

@Prototype.Blueprint

interface PortefeuilleCentreResponsibiliteTitreBlueprint {

    @Option.DefaultBoolean(false)
    boolean isMF();

    @Option.DefaultBoolean(false)
    boolean specialCentreDeResponsabilite();

    @Option.Access("")
    List<CentreResponsabiliteTitre> services();

    final class CustomMethods {
        @Prototype.BuilderMethod
        static void service(PortefeuilleCentreResponsibiliteTitre.BuilderBase<?, ?> builderBase,
                CentreResponsabiliteTitre service) {
            Objects.requireNonNull(builderBase);
            Objects.requireNonNull(service);
            var existingTypes = builderBase.services();
            var type = service.serviceType();

            //only mf should have t4, t5, t7 for services centraux
            if(service.hasSpecialTitres()){
                if(!(builderBase.isMF()) || (service.serviceType() != CentreResponsabilite.SERVICE_CENTRAUX)){
                    throw new IllegalStateException("titre 5, titre 6, titre 7 should only be set for service centraux of mf, found: "+ service);
                }
            }

            //check that we don't have two instance of the same service type
            if(exists(existingTypes, type)){
                throw new IllegalArgumentException("service type already exist, found:" + existingTypes + " type:" + service) ;
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
