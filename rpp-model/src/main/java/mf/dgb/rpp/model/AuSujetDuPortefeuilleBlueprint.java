package mf.dgb.rpp.model;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import io.helidon.builder.api.Prototype;


@Prototype.Blueprint
interface AuSujetDuPortefeuilleBlueprint extends Writable {

    
    String AUSJT_1_TITLE_KEY = "section1.ausujetprotefeuille.title.text";

    LaMission laMission();

    LeMinistere leMinistere();

    CartographieProgrammesPortefeuille cartographie();

    FichePortefeuille fichePortefeuille();

    @Override
    default void write(XWPFDocument document, GenerationContext context) {
        //set portraint mode even if it's the default
        context.apply(PageLayout.PORTRAIT);
        
        
        String heading1Style = context.plainContent(HEADING_1_STYLE_KEY);
        XWPFParagraph auSujectTitle = document.createParagraph();
        auSujectTitle.setStyle(heading1Style);
        String auSujetText = context.contextualizedContent(AUSJT_1_TITLE_KEY);
        auSujectTitle.createRun().setText(auSujetText);


        laMission().write(document,context);
        leMinistere().write(document,context);
        cartographie().write(document,context);
        fichePortefeuille().write(document,context);
    }
}
