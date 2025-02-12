package com.bancika.gerberwriter.padmasters;

public final class Rectangle extends AbstractPad {
    private final double xSize;
    private final double ySize;
    private final String function;
    private final boolean negative;

    public Rectangle(double xSize, double ySize, String function, boolean negative) {
        validateReal(xSize, "xSize");
        validateReal(ySize, "ySize");
        if (xSize <= 0) {
            throw new IllegalArgumentException("X size must be > 0");
        }
        if (ySize <= 0) {
            throw new IllegalArgumentException("Y size must be > 0");
        }
        if (function == null) {
            throw new IllegalArgumentException("function must not be null");
        }

        this.xSize = xSize;
        this.ySize = ySize;
        this.function = function;
        this.negative = negative;
    }

    // Getters
    public double getXSize() { return xSize; }
    public double getYSize() { return ySize; }
    public String getFunction() { return function; }
    public boolean isNegative() { return negative; }
}