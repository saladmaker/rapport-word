package mf.dgb.rpp.model;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;

import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import io.helidon.builder.api.Prototype;

@Prototype.Blueprint
interface LeMinistereBlueprint extends Writable {

    String MNSTR_1_TITLE_KEY = "section1.leministere.title.text";
    String MNSTR_2_ORG_TITLE_KEY = "section1.leministere.organigramme.title.text";

    byte[] image();

    @Override
    default void write(XWPFDocument document, GenerationContext context) {
        //enforce landscape mode
        context.apply(PageLayout.LANDSCAPE);

        // le ministere title
        String heading2Style = context.plainContent(HEADING_2_STYLE_KEY);
        XWPFParagraph heading = document.createParagraph();
        heading.setStyle(heading2Style);
        heading.createRun().setText(context.contextualizedContent(MNSTR_1_TITLE_KEY));

        // orgranigramme title
        String orgaTitleStyle = context.plainContent(PARAGRAPH_DEFAULT_STYLE_KEY);
        XWPFParagraph organigrammeTitle = document.createParagraph();
        organigrammeTitle.setStyle(orgaTitleStyle);
        organigrammeTitle.createRun().setText(context.contextualizedContent(MNSTR_2_ORG_TITLE_KEY));

        //insert image
        String imageStyle = context.plainContent(IMG_STYLE_KEY);
        byte[] img = image();
        if (img != null && img.length > 0) {
            insertScaledImage(document, img, imageStyle, 0.80d, 0.2);
        }

    }

    private void insertScaledImage(
            XWPFDocument document,
            byte[] imgBytes,
            String styleId,
            double scaleFactor,
            double distortionTolerance) { // e.g. 0.2 = 20%

        try (ByteArrayInputStream bis = new ByteArrayInputStream(imgBytes)) {
            // ---- 1. Read image ----
            BufferedImage bufferedImage = ImageIO.read(bis);
            if (bufferedImage == null) {
                throw new RuntimeException("Invalid image format");
            }
            int imgWidthPx = bufferedImage.getWidth();
            int imgHeightPx = bufferedImage.getHeight();

            double pageWidthCm = PageLayout.LANDSCAPE.width.doubleValue() / 576;
            double pageHeightCm = PageLayout.LANDSCAPE.height.doubleValue() / 576;
            double marginCm = PageLayout.LANDSCAPE.margin.doubleValue() / 567.0;
            double availableWidthCm = pageWidthCm - (marginCm * 2);
            double availableHeightCm = pageHeightCm - (marginCm * 2);
            double dpi = 96.0;
            double imgWidthCm = (imgWidthPx / dpi) * 2.54;
            double imgHeightCm = (imgHeightPx / dpi) * 2.54;

            // Independent ratios
            double widthRatio = availableWidthCm / imgWidthCm;
            double heightRatio = availableHeightCm / imgHeightCm;

            // Default scales (no distortion)
            double scale = Math.min(widthRatio, heightRatio) * scaleFactor;
            double widthScale = scale;
            double heightScale = scale;

            // Allow distortion if within tolerance
            double maxRatio = Math.max(widthRatio, heightRatio) * scaleFactor;
            if (maxRatio / scale <= 1.0 + distortionTolerance) {
                // Use independent scaling
                widthScale = widthRatio * scaleFactor;
                heightScale = heightRatio * scaleFactor;
            }

            double finalWidthCm = imgWidthCm * widthScale;
            double finalHeightCm = imgHeightCm * heightScale;

            // ---- 5. Convert to EMUs (1 cm = 360,000 EMUs) ----
            int finalWidthEmu = (int) (finalWidthCm * 360000);
            int finalHeightEmu = (int) (finalHeightCm * 360000);

            XWPFParagraph imgPara = document.createParagraph();
            imgPara.setStyle(styleId);
            XWPFRun imgRun = imgPara.createRun();

            try (ByteArrayInputStream imgStream = new ByteArrayInputStream(imgBytes)) {
                imgRun.addPicture(imgStream, Document.PICTURE_TYPE_JPEG,
                        "organigramme.jpg", finalWidthEmu, finalHeightEmu);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
