package com.bancika.gerberwriter.padmasters;

public final class Thermal extends AbstractPad {
    private final double outerDiameter;
    private final double innerDiameter;
    private final double gap;
    private final String function;
    private final boolean negative;

    public Thermal(double outerDiameter, double innerDiameter, double gap, String function, boolean negative) {
        validateReal(outerDiameter, "outerDiameter");
        validateReal(innerDiameter, "innerDiameter");
        validateReal(gap, "gap");

        if (!(0 < innerDiameter && innerDiameter < outerDiameter)) {
            throw new IllegalArgumentException("diameter values invalid");
        }
        if (!(0 < gap && gap < outerDiameter/Math.sqrt(2))) {
            throw new IllegalArgumentException("gap only valid if 0 < gap <= outer_diameter/sqrt(2)");
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
