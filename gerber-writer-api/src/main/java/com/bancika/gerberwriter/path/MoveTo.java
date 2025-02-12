package com.bancika.gerberwriter.path;

import com.bancika.gerberwriter.Point;

public class MoveTo implements PathOperator {
    final Point to;
    public MoveTo(Point to) { this.to = to; }

    public Point getTo() {
        return to;
    }
}