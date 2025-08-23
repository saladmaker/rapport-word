package rpp.poi.model;

import java.util.Optional;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import io.helidon.builder.api.Prototype;
import rpp.poi.model.LaMission;
import rpp.poi.model.LeMinistere;
import rpp.poi.model.CartographieProgrammesPortefeuille;


@Prototype.Blueprint
interface AuSujetDuPortefeuilleBlueprint extends Writable {

    String $_1_AU_SUJECT = "Au sujet du portefeuille";

    LaMission laMission();

    LeMinistere leMinistere();

    Optional<CartographieProgrammesPortefeuille> cartographie();

    @Override
    default void write(XWPFDocument document) {
        XWPFParagraph auSujectTitle = document.createParagraph();
        auSujectTitle.setStyle("Heading1");
        auSujectTitle.createRun().setText($_1_AU_SUJECT);

        laMission().write(document);
        leMinistere().write(document);
    }
}
