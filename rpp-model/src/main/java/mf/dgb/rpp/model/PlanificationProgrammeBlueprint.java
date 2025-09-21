package mf.dgb.rpp.model;

import io.helidon.builder.api.Prototype;
import io.helidon.builder.api.Option;

@Prototype.Blueprint
interface PlanificationProgrammeBlueprint {

    @Option.DefaultInt(0)
    int counter();

    @Option.Required
    String name();

}
