package rpp.poi.model;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;

import io.helidon.builder.api.Prototype;

@Prototype.Blueprint
interface LeMinistereBlueprint extends Writable {

    String MNSTR_1_TITLE_KEY = "section1.leministere.title.text";
    String MNSTR_1_TITLE_STYLE_KEY = "section1.leministere.title.style";
    String MNSTR_2_ORG_TITLE_KEY ="section1.leministere.organigramme.title.text";
    String MNSTR_2_ORG_TITLE_STYLE_KEY ="section1.leministere.organigramme.title.style";
    String MNSTR_3_ORG_IMG_STYLE_KEY = "section1.leministere.organigramme.image.style";

    byte[] image();

    @Override
    default void write(XWPFDocument document, Map<String, String> config, PageLayoutManager plm) {
        plm.apply(PageLayout.LANDSCAPE);
        // le ministere title
        XWPFParagraph heading = document.createParagraph();
        heading.setStyle(config.get(MNSTR_1_TITLE_STYLE_KEY));
        heading.createRun().setText(config.get(MNSTR_1_TITLE_KEY));

        //orgranigramme title
        XWPFParagraph organigrammeTitle = document.createParagraph();
        organigrammeTitle.setStyle(config.get(MNSTR_2_ORG_TITLE_STYLE_KEY));
        organigrammeTitle.createRun().setText(config.get(MNSTR_2_ORG_TITLE_KEY));


        byte[] img = image();
        if (img != null && img.length > 0) {
            try (ByteArrayInputStream bis = new ByteArrayInputStream(img)) {
                // ---- 1. Read image to get original dimensions ----
                BufferedImage bufferedImage = ImageIO.read(bis);
                if (bufferedImage == null) {
                    throw new RuntimeException("Invalid image format");
                }
                int imgWidthPx = bufferedImage.getWidth();
                int imgHeightPx = bufferedImage.getHeight();

                // ---- 2. Page setup ----
                double pageWidthCm = 29.7; // A4 landscape width
                double pageHeightCm = 21.0; // A4 landscape height
                double marginCm = 2.0;

                double availableWidthCm = pageWidthCm - (marginCm * 2);
                double availableHeightCm = pageHeightCm - (marginCm * 2);

                // ---- 3. Subtract paragraphs height ----
                double p1HeightPt = 14 + 12.4; // font size 14 + spacing 12.4
                double p2HeightPt = 12 + 8; // font size 12 + spacing 8
                double totalParagraphHeightPt = p1HeightPt + p2HeightPt;
                double totalParagraphHeightCm = totalParagraphHeightPt * 0.0352778;

                double imageAreaHeightCm = availableHeightCm - totalParagraphHeightCm;

                // ---- 4. Apply extra 0.5 cm padding ----
                availableWidthCm -= 0.5;
                imageAreaHeightCm -= 0.5;

                if (availableWidthCm <= 0 || imageAreaHeightCm <= 0) {
                    throw new RuntimeException("Not enough space for image after applying padding.");
                }

                // ---- 5. Convert image size to cm using DPI (assume 96 DPI) ----
                double dpi = 96.0;
                double imgWidthCm = (imgWidthPx / dpi) * 2.54;
                double imgHeightCm = (imgHeightPx / dpi) * 2.54;

                // ---- 6. Compute scaling factor ----
                double scale = Math.min(availableWidthCm / imgWidthCm, imageAreaHeightCm / imgHeightCm);

                double finalWidthCm = imgWidthCm * scale;
                double finalHeightCm = imgHeightCm * scale;

                // ---- 7. Convert to EMUs (1 cm = 360,000 EMUs) ----
                int finalWidthEmu = (int) (finalWidthCm * 360000);
                int finalHeightEmu = (int) (finalHeightCm * 360000);

                // ---- 8. Insert image ----
                XWPFParagraph imgPara = document.createParagraph();
                imgPara.setStyle(config.get(MNSTR_3_ORG_IMG_STYLE_KEY));
                XWPFRun imgRun = imgPara.createRun();
                try (ByteArrayInputStream imgStream = new ByteArrayInputStream(img)) {
                    imgRun.addPicture(imgStream, Document.PICTURE_TYPE_PNG, "image.png", finalWidthEmu, finalHeightEmu);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
