package mf.dgb.rpp.testing.fusion;

import java.math.BigInteger;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSpacing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLineSpacingRule;

import mf.dgb.rpp.model.GenerationContext;
import mf.dgb.rpp.model.PageLayout;

public class Fusion {
    public static void write(XWPFDocument document, GenerationContext context) {
        // Apply page layout
        context.apply(PageLayout.LANDSCAPE);

String CARTO_2_TABLE_STYLE_KEY = "section1.cartographie.table.style";

// === First Table (1x3) ===
XWPFTable progStrtable = document.createTable(1, 3);
context.applyTableStyle(progStrtable, CARTO_2_TABLE_STYLE_KEY);

// Get usable page width from PageLayout
BigInteger usableWidth = PageLayout.LANDSCAPE.usableWidth();

// Column widths (1st table)
BigInteger col1Width = PageLayout.LANDSCAPE.scaledWidth(0.15);
BigInteger col2Width = PageLayout.LANDSCAPE.scaledWidth(0.45);
BigInteger col3Width = PageLayout.LANDSCAPE.scaledWidth(0.40);

// Set table width explicitly
progStrtable.getCTTbl().getTblPr().addNewTblW().setW(usableWidth);

// Apply column widths
progStrtable.getRow(0).getCell(0).getCTTc().addNewTcPr().addNewTcW().setW(col1Width);
progStrtable.getRow(0).getCell(1).getCTTc().addNewTcPr().addNewTcW().setW(col2Width);
progStrtable.getRow(0).getCell(2).getCTTc().addNewTcPr().addNewTcW().setW(col3Width);

// Fill cells
progStrtable.getRow(0).getCell(0).setText("\u00A0");
progStrtable.getRow(0).getCell(1).setText("Postes ouverts");
progStrtable.getRow(0).getCell(2).setText("Masse salariale");

XWPFParagraph spacer = document.createParagraph();

// Set minimal line spacing (exactly 1 point)
CTPPr ppr = spacer.getCTP().isSetPPr() ? spacer.getCTP().getPPr() : spacer.getCTP().addNewPPr();
CTSpacing spacing = ppr.isSetSpacing() ? ppr.getSpacing() : ppr.addNewSpacing();
spacing.setLine(BigInteger.valueOf(100)); // 100 = 1 point (minimum)
spacing.setLineRule(STLineSpacingRule.EXACT);

// Add an empty run (no visible text)
spacer.createRun();

// Remove any default spacing
spacer.setSpacingBefore(0);
spacer.setSpacingAfter(0);

// === Second Table (1x5) ===
XWPFTable secondRow = document.createTable(1, 5);
context.applyTableStyle(secondRow, CARTO_2_TABLE_STYLE_KEY);

// Column widths (2nd table)
BigInteger Seccol1Width = PageLayout.LANDSCAPE.scaledWidth(0.15);
BigInteger Seccol2Width = PageLayout.LANDSCAPE.scaledWidth(0.29);
BigInteger Seccol3Width = PageLayout.LANDSCAPE.scaledWidth(0.16);
BigInteger Seccol4Width = PageLayout.LANDSCAPE.scaledWidth(0.33);
BigInteger Seccol5Width = PageLayout.LANDSCAPE.scaledWidth(0.07);

// Set table width explicitly
secondRow.getCTTbl().getTblPr().addNewTblW().setW(usableWidth);

// Apply column widths
secondRow.getRow(0).getCell(0).getCTTc().addNewTcPr().addNewTcW().setW(Seccol1Width);
secondRow.getRow(0).getCell(1).getCTTc().addNewTcPr().addNewTcW().setW(Seccol2Width);
secondRow.getRow(0).getCell(2).getCTTc().addNewTcPr().addNewTcW().setW(Seccol3Width);
secondRow.getRow(0).getCell(3).getCTTc().addNewTcPr().addNewTcW().setW(Seccol4Width);
secondRow.getRow(0).getCell(4).getCTTc().addNewTcPr().addNewTcW().setW(Seccol5Width);

// Fill cells (2nd–5th)
secondRow.getRow(0).getCell(0).setText("\u00A0"); // visually empty
secondRow.getRow(0).getCell(1).setText("Nombre");
secondRow.getRow(0).getCell(2).setText("Variation (2024-2026)");
secondRow.getRow(0).getCell(3).setText("Montant");
secondRow.getRow(0).getCell(4).setText("Variation (2025-2026)");

    }
}
