package rpp.poi.model;

import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import io.helidon.builder.api.Prototype;
import io.helidon.builder.api.Option;

@Prototype.Blueprint
interface CartographieProgrammesPortefeuilleBlueprint extends Writable{

    @Option.Singular
    List<ProgrammeStructure> programmeStructures();

    @Override
    default void write(XWPFDocument document) {
    }

    
}
