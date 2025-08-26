package rpp.poi.model;

import java.math.BigInteger;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;

public enum PageLayout {
    PORTRAIT(11906, 16838, 1134, STPageOrientation.PORTRAIT),
    LANDSCAPE(16838, 11906, 1134, STPageOrientation.LANDSCAPE);

    final BigInteger width;
    final BigInteger height;
    final BigInteger margin;
    final STPageOrientation.Enum orientation;

    PageLayout(int width, int height, int margin, STPageOrientation.Enum orientation) {
        this.width = BigInteger.valueOf(width);
        this.height = BigInteger.valueOf(height);
        this.margin = BigInteger.valueOf(margin);
        this.orientation = orientation;
    }
}