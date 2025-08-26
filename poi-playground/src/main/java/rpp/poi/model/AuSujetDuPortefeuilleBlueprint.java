package rpp.poi.model;

import java.util.Map;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import io.helidon.builder.api.Prototype;


@Prototype.Blueprint
interface AuSujetDuPortefeuilleBlueprint extends Writable {

    String AUSJT_1_TITLE_KEY = "section1.ausujetprotefeuille.title.text";
    String AUSJT_1_TITLE_STYLE_KEY = "section1.ausujetprotefeuille.title.style";

    LaMission laMission();

    LeMinistere leMinistere();

    CartographieProgrammesPortefeuille cartographie();

    FichePortefeuille fichePortefeuille();

    @Override
    default void write(GenerationContext context) {
        var document = context.document;
        //title style and text
        XWPFParagraph auSujectTitle = document.createParagraph();
        auSujectTitle.setStyle(config.get(AUSJT_1_TITLE_STYLE_KEY));
        auSujectTitle.createRun().setText(config.get(AUSJT_1_TITLE_KEY));

        laMission().write(document,config);
        leMinistere().write(document,config);
        cartographie().write(document,config);
        fichePortefeuille().write(document,config);
    }
}
