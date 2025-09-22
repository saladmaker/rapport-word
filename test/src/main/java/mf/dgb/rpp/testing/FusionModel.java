package mf.dgb.rpp.testing;

import java.util.Map;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import mf.dgb.rpp.model.GenerationContext;
import mf.dgb.rpp.model.LanguageDirection;
import mf.dgb.rpp.testing.fusion.Fusion;

public class FusionModel implements DocumentGenerator{

    @Override
    public void generate(XWPFDocument document) throws Exception {
        var context = GenerationContext.of(document, LanguageDirection.LTR, Map.of());
        Fusion.write(document, context);
    }

}
