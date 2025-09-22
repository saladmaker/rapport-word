package mf.dgb.rpp.model;

import io.helidon.builder.api.Option;
import io.helidon.builder.api.Prototype;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Prototype.Blueprint
interface PostOuvertsMassSalarialesBlueprint {

    List<PostOuvertsMassSalariale> services();

    final class CustomMethods{
        @Prototype.BuilderMethod
        static void service(PostOuvertsMassSalariales.BuilderBase<?,?> builderBase, PostOuvertsMassSalariale poms){
            Objects.requireNonNull(poms, "poms can't be null!");

            List<PostOuvertsMassSalariale> services = new ArrayList<>(builderBase.services());

            for(var p: services){
                if(p.serviceType() == poms.serviceType()){
                    throw  new IllegalArgumentException("service type " + p.serviceType() + " already exist!");
                }
            }

            services.add(poms);

            //replaces old collection
            builderBase.services(services);
        }
    }

}
