package rpp.poi.model;

import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
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

        for (var m : mission()) {
            XWPFParagraph mParagraph = document.createParagraph();
            intro.setStyle("Normal");
            addParagraphWithManualBreaks(mParagraph, m.mission());
        }

    }
}
