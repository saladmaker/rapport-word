package rpp.poi.model;

import java.math.BigInteger;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFNumbering;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import io.helidon.builder.api.Option;
import io.helidon.builder.api.Prototype;

@Prototype.Blueprint
interface LaMissionBlueprint extends Writable {

    String MISSION_1_TITLE_KEY = "section1.lamission.title.text";


    @Option.Required
    String intro();

    @Option.Singular
    List<Mission> missions();

    @Override
    default void write(XWPFDocument document, GenerationContext context) { 
        context.apply(PageLayout.PORTRAIT);
        //write title
        String heading2Style = context.plainContent(HEADING_2_STYLE_KEY);
        XWPFParagraph title = document.createParagraph();
        title.setStyle(heading2Style);
        title.createRun().setText(context.contextualizedContent(MISSION_1_TITLE_KEY));

        //write intro
        XWPFParagraph intro = document.createParagraph();
        String introStyle = context.plainContent(LONG_PARAGRAPH_STYLE_KEY);
        intro.setStyle(introStyle);
        addParagraphWithManualBreaks(intro, context.contextualize(intro()));
        writeMissions(document, context);

    }
    default void writeMissions(XWPFDocument document, GenerationContext context) {
        
        //create numbering and associate it with the predefined simple multi level list
        String numberingId = context.plainContent(MULTI_LEVEL_LIST_KEY);
        BigInteger abstractNumId = new BigInteger(numberingId);//predefined numbering id
        XWPFNumbering numbering = document.createNumbering();
        BigInteger numId = numbering.addNum(abstractNumId);
        for (var m : missions()) {
            XWPFParagraph mParagraph = document.createParagraph();
            //set numbering
            mParagraph.setNumID(numId);
            mParagraph.setNumILvl(BigInteger.ZERO); //mission point level
            mParagraph.createRun().setText(context.contextualize(m.mission()));

            for (var sm : m.sub()) {
                XWPFParagraph smParagraph = document.createParagraph();
                smParagraph.setNumID(numId);
                smParagraph.setNumILvl(BigInteger.ONE); //mission details -points level
                smParagraph.createRun().setText(context.contextualize(sm));
            }
        }


    }
}
