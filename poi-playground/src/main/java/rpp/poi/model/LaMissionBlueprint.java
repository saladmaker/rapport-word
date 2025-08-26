package rpp.poi.model;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFNumbering;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import io.helidon.builder.api.Prototype;
import io.helidon.builder.api.Option;

@Prototype.Blueprint
interface LaMissionBlueprint extends Writable {

    String MISSION_1_TITLE_KEY = "section1.lamission.title.text";
    String MISSION_1_TITLE_STYLE_KEY = "section1.lamission.title.style";
    String MISSION_2_INTRO_STYLE_KEY = "section1.lamission.intro.style";
    String MISSION_3_NUMBERING_ID = "section1.lamission.numbering.id";


    @Option.Required
    String intro();

    @Option.Singular
    List<Mission> missions();

    @Override
    default void write(XWPFDocument document, Map<String, String> config) {
        //write title
        XWPFParagraph title = document.createParagraph();
        title.setStyle(config.get(MISSION_1_TITLE_STYLE_KEY));
        title.createRun().setText(config.get(MISSION_1_TITLE_KEY));

        //write intro
        XWPFParagraph intro = document.createParagraph();
        intro.setStyle(config.get(MISSION_2_INTRO_STYLE_KEY));
        addParagraphWithManualBreaks(intro, intro());
        writeMissions(document, config);


    }
    default void writeMissions(XWPFDocument document, Map<String, String> config) {
        //create numbering and associate it with the predefined simple multi level list
        XWPFNumbering numbering = document.createNumbering();
        BigInteger abstractNumId = new BigInteger(config.get(MISSION_3_NUMBERING_ID));//predefined numbering id
        BigInteger numId = numbering.addNum(abstractNumId);
        for (var m : missions()) {
            XWPFParagraph mParagraph = document.createParagraph();
            //set numbering
            mParagraph.setNumID(numId);
            mParagraph.setNumILvl(BigInteger.ZERO); //mission point level
            mParagraph.createRun().setText(m.mission());

            for (var sm : m.sub()) {
                XWPFParagraph smParagraph = document.createParagraph();
                smParagraph.setNumID(numId);
                smParagraph.setNumILvl(BigInteger.ONE); //submission point level
                smParagraph.createRun().setText(sm);
            }
        }


    }
}
