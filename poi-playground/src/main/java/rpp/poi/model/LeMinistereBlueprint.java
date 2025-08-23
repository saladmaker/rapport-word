package rpp.poi.model;

import java.io.ByteArrayInputStream;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;

import io.helidon.builder.api.Prototype;

@Prototype.Blueprint
interface LeMinistereBlueprint extends Writable {

    String TITLE = "Le ministere";

    byte[] image();

    @Override
    default void write(XWPFDocument document) {
        ensureOrientation(document, STPageOrientation.LANDSCAPE);

        // Heading 2
        XWPFParagraph heading = document.createParagraph();
        heading.setStyle("Heading2");
        XWPFRun headingRun = heading.createRun();
        headingRun.setText(TITLE);

        // Normal text
        XWPFParagraph textPara = document.createParagraph();
        textPara.createRun().setText("Organigramme du ministère");

        // Image
        byte[] img = image();
        if (img != null && img.length > 0) {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(img)) {
                XWPFParagraph imgPara = document.createParagraph();
                XWPFRun imgRun = imgPara.createRun();
                imgRun.addPicture(bis, Document.PICTURE_TYPE_PNG, "image.png",
                        Units.toEMU(500), Units.toEMU(300));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
