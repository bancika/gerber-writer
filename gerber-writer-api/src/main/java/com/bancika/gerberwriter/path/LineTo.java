package com.bancika.gerberwriter.path;

import com.bancika.gerberwriter.Point;

public class LineTo implements PathOperator {
    final Point to;
    public LineTo(Point to) { this.to = to; }

    public Point getTo() {
        return to;
    }
}
