package com.bancika.gerberwriter.padmasters;

public final class RegularPolygon extends AbstractPad{
    private final double outerDiameter;
    private final int vertices;
    private final String function;
    private final boolean negative;

    public RegularPolygon(double outerDiameter, int vertices, String function, boolean negative) {
        validateReal(outerDiameter, "outerDiameter");

        if (outerDiameter <= 0) {
            throw new IllegalArgumentException("outer_diameter must be > 0");
        }
        if (vertices < 3 || vertices > 12) {
            throw new IllegalArgumentException("vertices must be from 3 up to 12");
        }
        if (function == null) {
            throw new IllegalArgumentException("function must not be null");
        }

        this.outerDiameter = outerDiameter;
        this.vertices = vertices;
        this.function = function;
        this.negative = negative;
    }

    // Getters
    public double getOuterDiameter() { return outerDiameter; }
    public int getVertices() { return vertices; }
    public String getFunction() { return function; }
    public boolean isNegative() { return negative; }
}
