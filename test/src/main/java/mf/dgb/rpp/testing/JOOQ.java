package mf.dgb.rpp.testing;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import mf.dgb.rpp.aggregator.Portefeuilles;

public class JOOQ implements DocumentGenerator{

    @Override
    public void generate(XWPFDocument document) throws Exception {
        Portefeuilles pf = new Portefeuilles();
        var execice = pf.findMostRecentExercice();
        System.out.println("execice " + execice);
        var portefeuilles = pf.portefeuilleByExecrcice(execice);
        System.out.println("portefeuilles" + portefeuilles);
        System.out.println("portefeuilles" + portefeuilles.size());
        var p1 = portefeuilles.get(0);
        var programmes = pf.programmeByProtefeuille(p1);
        System.out.println("programmes " + programmes);
    }

}