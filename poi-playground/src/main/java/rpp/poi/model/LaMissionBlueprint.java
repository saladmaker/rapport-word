package rpp.poi.model;

import java.math.BigInteger;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFNumbering;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import io.helidon.builder.api.Prototype;
import io.helidon.builder.api.Option;

@Prototype.Blueprint
interface LaMissionBlueprint extends Writable {

    String TITLE = "La mission";

    @Option.Required
    String intro();

    @Option.Singular
    List<Mission> mission();

    @Override
    default void write(XWPFDocument document) {
        XWPFParagraph title = document.createParagraph();
        title.setStyle("Heading2");
        title.createRun().setText(TITLE);

        XWPFParagraph intro = document.createParagraph();
        intro.setStyle("Normal");
        addParagraphWithManualBreaks(intro, intro());

        XWPFNumbering numbering = document.createNumbering();

        //retrieve numbering id
        BigInteger abstractNumId = BigInteger.valueOf(2);
        BigInteger numId = numbering.addNum(abstractNumId);
        for (var m : mission()) {
            XWPFParagraph mParagraph = document.createParagraph();
            //set numbering
            mParagraph.setNumID(numId); 
            mParagraph.setNumILvl(BigInteger.ZERO); //mission point 
            mParagraph.createRun().setText(m.mission());

            for (var subm : m.sub()) {
                XWPFParagraph smParagraph = document.createParagraph();
                smParagraph.setNumID(numId);
                smParagraph.setNumILvl(BigInteger.ONE); //submission point
                smParagraph.createRun().setText(subm);
            }
        }

    }
}
