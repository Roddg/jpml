package org.kl.jpml.test.shape;

import org.kl.jpml.meta.Extract;
import org.kl.jpml.type.IntRef;

public class Quadrate extends Figure {
    private int width;

    private static int staticWidth = 5;

    public Quadrate() {
        this.width = 10;
    }

    public Quadrate(int width) {
        this.width = width;
    }

    @Override
    public int square() {
        return width * width;
    }

    @Extract
    public void deconstruct(IntRef width) {
        width.set(this.width);
    }

    @Extract
    public static void unapply(IntRef width) {
        width.set(staticWidth);
    }

    public int width() {
        return width;
    }
}