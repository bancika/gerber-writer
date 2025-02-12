package com.bancika.gerberwriter.padmasters;

import com.bancika.gerberwriter.Point;

import java.util.Arrays;

public final class UserPolygon extends AbstractPad {
    private final Point[] polygon;
    private final String function;
    private final boolean negative;
    
    public UserPolygon(Point[] polygon, String function, boolean negative) {
        if (polygon == null || polygon.length < 3) {
            throw new IllegalArgumentException("polygon must have at least three vertices");
        }
        if (!polygon[0].equals(polygon[polygon.length - 1])) {
            throw new IllegalArgumentException("polygon must be closed");
        }
        if (function == null) {
            throw new IllegalArgumentException("function must not be null");
        }

        this.polygon = Arrays.copyOf(polygon, polygon.length);
        this.function = function;
        this.negative = negative;
    }

    // Getters
    public Point[] getPolygon() { return Arrays.copyOf(polygon, polygon.length); }
    public String getFunction() { return function; }
    public boolean isNegative() { return negative; }
}
