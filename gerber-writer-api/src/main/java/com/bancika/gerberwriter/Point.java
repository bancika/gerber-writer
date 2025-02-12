package com.bancika.gerberwriter;

public class Point {
    public final double x;
    public final double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public static Point offset(Point a, Point b) {
        return new Point(a.x - b.x, a.y - b.y);
    }

    public static Point rotate(Point point, double angleDegrees) {
        double angleRad = Math.toRadians(angleDegrees);
        return new Point(
                point.x * Math.cos(angleRad) - point.y * Math.sin(angleRad),
                point.x * Math.sin(angleRad) + point.y * Math.cos(angleRad)
        );
    }

    public static double lInf(Point a, Point b) {
        return Math.max(Math.abs(a.x - b.x), Math.abs(a.y - b.y));
    }

    public static double l2(Point a, Point b) {
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }

    public static String orientation(Point center, Point p0, Point p1) {
        double value = -(p0.y - center.y) * (p1.x - center.x) +
                (p0.x - center.x) * (p1.y - center.y);
        if (value > 0) return "+";
        if (value == 0) return "0";
        return "-";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        // Using Double.compare to handle NaN and -0.0 cases properly
        return Double.compare(point.x, x) == 0 &&
                Double.compare(point.y, y) == 0;
    }

    @Override
    public int hashCode() {
        // Using Double.hashCode to be consistent with equals
        int result = Double.hashCode(x);
        result = 31 * result + Double.hashCode(y);
        return result;
    }

    @Override
    public String toString() {
        return String.format("Point(%.6f, %.6f)", x, y);
    }
}
