package mf.dgb.rpp.model;

import io.helidon.builder.api.Option;
import io.helidon.builder.api.Prototype;

@Prototype.Blueprint
interface FicheProgrammeGenerationBlueprint extends FicheProgrammeBlueprint{

    @Option.Required
    int counter();

}
