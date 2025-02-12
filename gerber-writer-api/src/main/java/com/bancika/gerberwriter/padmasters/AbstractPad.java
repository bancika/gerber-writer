package com.bancika.gerberwriter.padmasters;

/**
 * Padmaster Classes Reference
 * --------------------------
 * A padmaster has the following attributes:
 * - shape:    Geometric shape, e.g. rectangle
 * - function: A pad function, e.g. 'ViaPad'
 * - polarity: Positive or negative
 *
 * Padmasters are used to create pads by replicating them
 * - at a given point on the 2D plane
 * - under a given angle
 */
public abstract class AbstractPad {

    protected static void validateReal(double value, String fieldName) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException(fieldName + " must be a real number");
        }
    }
}