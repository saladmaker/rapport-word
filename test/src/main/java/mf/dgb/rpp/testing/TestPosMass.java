package mf.dgb.rpp.testing;

import java.util.Map;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import mf.dgb.rpp.model.CentreResponsabilite;
import mf.dgb.rpp.model.GenerationContext;
import mf.dgb.rpp.model.LanguageDirection;
import mf.dgb.rpp.model.PostOuvertsMassSalariale;
import mf.dgb.rpp.model.PostOuvertsMassSalariales;

public class TestPosMass implements DocumentGenerator {

    @Override
    public void generate(XWPFDocument document) throws Exception {
        var psm = PostOuvertsMassSalariales.builder()
                .service(PostOuvertsMassSalariale.builder()
                        .serviceType(CentreResponsabilite.SERVICES_CENTRAUX) // use your enum or const
                        .postesAnneeMoins2(100L)
                        .postesAnneeMoins1(120L)
                        .postesAnnee(140L)
                        .salairAnneeMoins2(500_000L)
                        .salairAnneeMoins1(550_000L)
                        .salairAnnee(600_000L)
                        .build())
                .service(PostOuvertsMassSalariale.builder()
                        .serviceType(CentreResponsabilite.SERVICES_DECONCENTRES)
                        .postesAnneeMoins2(50L)
                        .postesAnneeMoins1(55L)
                        .postesAnnee(65L)
                        .salairAnneeMoins2(250_000L)
                        .salairAnneeMoins1(260_000L)
                        .salairAnnee(300_000L)
                        .build())
                .service(PostOuvertsMassSalariale.builder()
                        .serviceType(CentreResponsabilite.ORGANISMES_SOUS_TUTELLE) // assuming you have this type
                        .postesAnneeMoins2(10L)
                        .postesAnneeMoins1(12L)
                        .postesAnnee(15L)
                        .salairAnneeMoins2(50_000L)
                        .salairAnneeMoins1(52_000L)
                        .salairAnnee(60_000L)
                        .build())
                .build();
         var mappers = Map.of(
                "portefeuille", "La sante",//may be we should remove it as it doesn't repeat
                "gestionnaire", "La sante",//may be we should remove it as it doesn't repeat
                "n-2", "2024",
                "n-1", "2025",
                "n", "2026",
                "n+1", "2027",
                "n+2", "2028"
        );
        psm.write(document, GenerationContext.of(document, LanguageDirection.LTR, mappers));
    }

}
