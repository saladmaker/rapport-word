package mf.dgb.rpp.model;

import java.util.Set;

import io.helidon.builder.api.Prototype;
import io.helidon.builder.api.Option;

@Prototype.Blueprint
interface ProgrammeStructureBlueprint {

    @Option.Required
    String name();

    @Option.Singular
    Set<String> serviceCentres();

    @Option.Singular
    Set<String> serviceDeconcentrees();

    @Option.Singular
    Set<String> organismeSousTutelles();

    @Option.Singular
    Set<String> orgamismeTerris();

}