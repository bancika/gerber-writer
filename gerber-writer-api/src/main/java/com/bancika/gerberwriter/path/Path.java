package com.bancika.gerberwriter.path;

import com.bancika.gerberwriter.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Path {
    private final List<PathOperator> operators = new ArrayList<>();
    private Point currentPoint = null;
    private Point startPoint = null;
    private boolean contour = false;
    private Point pointMax = new Point(1, 1);

    public void moveTo(Point to) {
        operators.add(new MoveTo(to));
        currentPoint = to;
        startPoint = to;
        contour = false;
        updatePointMax(to);
    }

    public void lineTo(Point end) {
        if (currentPoint == null) {
            throw new IllegalStateException("No current point");
        }
        operators.add(new LineTo(end));
        currentPoint = end;
        contour = end.equals(startPoint);
        updatePointMax(end);
    }

    public void arcTo(Point end, Point center, String orientation) {
        if (currentPoint == null) {
            throw new IllegalStateException("No current point");
        }
        if (!orientation.equals("+") && !orientation.equals("-")) {
            throw new IllegalArgumentException("Orientation must be '+' or '-'");
        }

        double maxDeviation = 2.0e-5;
        if (Math.abs(Point.l2(center, currentPoint) - Point.l2(center, end)) > maxDeviation) {
            throw new IllegalArgumentException(
                    String.format("Radii to begin and end points differ by more than %f", maxDeviation));
        }

        operators.add(new ArcTo(end, center, orientation));
        currentPoint = end;
        contour = end.equals(startPoint);
        updatePointMax(end);
    }

    private void updatePointMax(Point p) {
        pointMax = new Point(
                Math.max(Math.abs(p.x), pointMax.x),
                Math.max(Math.abs(p.y), pointMax.y)
        );
    }

    public int size() {
        return operators.size();
    }

    public List<PathOperator> getOperators() {
        return Collections.unmodifiableList(operators);
    }

    public boolean isContour() {
        return contour;
    }

    public Point getPointMax() {
        return pointMax;
    }
}
