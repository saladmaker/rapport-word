package mf.dgb.rpp.model;

import java.util.List;

import io.helidon.builder.api.Prototype;
import io.helidon.builder.api.Option;

@Prototype.Blueprint
interface MissionBlueprint {

    String mission();

    @Option.Singular
    List<String> subMissions();
}
