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

        // Define or retrieve abstract numbering (from template or programmatically)
        // Here assume abstractNumId = 2 (your "Mission" style)
        BigInteger abstractNumId = BigInteger.valueOf(2);

        // Create a new concrete num instance bound to that abstract
        BigInteger numId = numbering.addNum(abstractNumId);

        // Now when writing paragraphs:
        for (var m : mission()) {
            XWPFParagraph mParagraph = document.createParagraph();
            mParagraph.setNumID(numId); // bind to numbering
            mParagraph.setNumILvl(BigInteger.ZERO); // level 0
            mParagraph.createRun().setText(m.mission());

            for (var subm : m.sub()) {
                XWPFParagraph smParagraph = document.createParagraph();
                smParagraph.setNumID(numId);
                smParagraph.setNumILvl(BigInteger.ONE); // level 1
                smParagraph.createRun().setText(subm);
            }
        }

    }
}
