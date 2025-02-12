package com.bancika.gerberwriter.padmasters;

public final class RoundedThermal extends AbstractPad {
    private final double outerDiameter;
    private final double innerDiameter;
    private final double gap;
    private final String function;
    private final boolean negative;

    public RoundedThermal(double outerDiameter, double innerDiameter, double gap, String function, boolean negative) {
        validateReal(outerDiameter, "outerDiameter");
        validateReal(innerDiameter, "innerDiameter");
        validateReal(gap, "gap");

        if (!(outerDiameter > 0 && innerDiameter > 0 && gap > 0)) {
            throw new IllegalArgumentException("parameters must be strictly positive");
        }
        if ((gap + outerDiameter - innerDiameter) * 2 * Math.sqrt(2) >= (outerDiameter + innerDiameter)) {
            throw new IllegalArgumentException("gap too big in relation to diameters");
        }
        if (function == null) {
            throw new IllegalArgumentException("function must not be null");
        }

        this.outerDiameter = outerDiameter;
        this.innerDiameter = innerDiameter;
        this.gap = gap;
        this.function = function;
        this.negative = negative;
    }

    // Getters
    public double getOuterDiameter() { return outerDiameter; }
    public double getInnerDiameter() { return innerDiameter; }
    public double getGap() { return gap; }
    public String getFunction() { return function; }
    public boolean isNegative() { return negative; }
}
