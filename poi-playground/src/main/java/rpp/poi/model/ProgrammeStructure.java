package rpp.poi.model;

import java.util.Set;

public record ProgrammeStructure(
    String name,
    Set<String> sc,
    Set<String> sdc,
    Set<String> ost,
    Set<String> ot
    ) {
}
