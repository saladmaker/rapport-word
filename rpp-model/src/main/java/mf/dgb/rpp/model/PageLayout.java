package mf.dgb.rpp.model;

import java.math.BigInteger;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;

public enum PageLayout {
    PORTRAIT(BigInteger.valueOf(11906), BigInteger.valueOf(16838), STPageOrientation.PORTRAIT,
            BigInteger.valueOf(1440)), // A4 portrait, 1" margin
    LANDSCAPE(BigInteger.valueOf(16838), BigInteger.valueOf(11906), STPageOrientation.LANDSCAPE,
            BigInteger.valueOf(1440));

    public final BigInteger width;
    public final BigInteger height;
    public final STPageOrientation.Enum orientation;
    public final BigInteger margin;

    PageLayout(BigInteger width, BigInteger height, STPageOrientation.Enum orientation, BigInteger margin) {
        this.width = width;
        this.height = height;
        this.orientation = orientation;
        this.margin = margin;
    }

    public BigInteger usableWidth() {
        return width.subtract(margin.multiply(BigInteger.valueOf(2)));
    }

    public BigInteger scaledWidth(double factor) {
        return BigInteger.valueOf(
                (long) (usableWidth().doubleValue() * factor));
    }

}
