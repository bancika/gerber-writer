package com.bancika.gerberwriter.padmasters;

public final class RoundedRectangle extends AbstractPad {
    private final double xSize;
    private final double ySize;
    private final double radius;
    private final String function;
    private final boolean negative;

    public RoundedRectangle(double xSize, double ySize, double radius, String function, boolean negative) {
        validateReal(xSize, "xSize");
        validateReal(ySize, "ySize");
        validateReal(radius, "radius");

        if (xSize <= 0) {
            throw new IllegalArgumentException("xSize must be > 0");
        }
        if (ySize <= 0) {
            throw new IllegalArgumentException("ySize must be > 0");
        }
        if (radius < 0 || radius > 0.5 * Math.min(xSize, ySize)) {
            throw new IllegalArgumentException("radius must be: 0 <= radius <= half the smallest side");
        }
        if (function == null) {
            throw new IllegalArgumentException("function must not be null");
        }

        this.xSize = xSize;
        this.ySize = ySize;
        this.radius = radius;
        this.function = function;
        this.negative = negative;
    }

    // Getters
    public double getXSize() { return xSize; }
    public double getYSize() { return ySize; }
    public double getRadius() { return radius; }
    public String getFunction() { return function; }
    public boolean isNegative() { return negative; }
}