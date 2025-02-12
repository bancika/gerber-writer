package com.bancika.gerberwriter.padmasters;

public  final class ChamferedRectangle extends AbstractPad {
    private final double xSize;
    private final double ySize;
    private final double cutoff;
    private final String function;
    private final boolean negative;

    public ChamferedRectangle(double xSize, double ySize, double cutoff, String function, boolean negative) {
        validateReal(xSize, "xSize");
        validateReal(ySize, "ySize");
        validateReal(cutoff, "cutoff");

        if (xSize <= 0) {
            throw new IllegalArgumentException("X size must be > 0");
        }
        if (ySize <= 0) {
            throw new IllegalArgumentException("Y size must be > 0");
        }
        if (cutoff < 0 || cutoff > 0.5 * Math.min(xSize, ySize)) {
            throw new IllegalArgumentException("Cutoff must be: 0 <= cutoff <= half the smallest side");
        }
        if (function == null) {
            throw new IllegalArgumentException("function must not be null");
        }

        this.xSize = xSize;
        this.ySize = ySize;
        this.cutoff = cutoff;
        this.function = function;
        this.negative = negative;
    }

    // Getters
    public double getXSize() { return xSize; }
    public double getYSize() { return ySize; }
    public double getCutoff() { return cutoff; }
    public String getFunction() { return function; }
    public boolean isNegative() { return negative; }
}
