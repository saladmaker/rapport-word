package rpp.poi.model;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import io.helidon.builder.api.Prototype;


@Prototype.Blueprint
interface AuSujetDuPortefeuilleBlueprint extends Writable {

    String $_1_AU_SUJECT = "Au sujet du portefeuille";

    LaMission laMission();

    LeMinistere leMinistere();

    CartographieProgrammesPortefeuille cartographie();

    FichePortefeuille fichePortefeuille();

    @Override
    default void write(XWPFDocument document) {
        XWPFParagraph auSujectTitle = document.createParagraph();
        auSujectTitle.setStyle("Heading1");
        auSujectTitle.createRun().setText($_1_AU_SUJECT);

        laMission().write(document);
        leMinistere().write(document);
        cartographie().write(document);
        fichePortefeuille().write(document);
    }
}
