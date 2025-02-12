package com.bancika.gerberwriter.padmasters;

public final class Circle extends AbstractPad {
    
    private final double diameter;
    private final String function;
    private final boolean negative;

    public Circle(double diameter, String function, boolean negative) {
        validateReal(diameter, "diameter");
        if (diameter < 0) {
            throw new IllegalArgumentException("diameter must be >= 0");
        }
        if (function == null) {
            throw new IllegalArgumentException("function must not be null");
        }

        this.diameter = diameter;
        this.function = function;
        this.negative = negative;
    }

    // Getters
    public double getDiameter() { return diameter; }
    public String getFunction() { return function; }
    public boolean isNegative() { return negative; }
}
