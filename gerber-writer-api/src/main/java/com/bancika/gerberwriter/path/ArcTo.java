package com.bancika.gerberwriter.path;

import com.bancika.gerberwriter.Point;

public class ArcTo implements PathOperator {
    final Point to;
    final Point center;
    final String orientation;
    public ArcTo(Point to, Point center, String orientation) {
        this.to = to;
        this.center = center;
        this.orientation = orientation;
    }

    public Point getTo() {
        return to;
    }

    public Point getCenter() {
        return center;
    }

    public String getOrientation() {
        return orientation;
    }
}
