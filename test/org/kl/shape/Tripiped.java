package org.kl.shape;

import org.kl.meta.Exclude;
import org.kl.meta.Extract;
import org.kl.type.FloatRef;

public class Tripiped extends Figure {
    private float width;
    private float longitude;
    private float height;

    @Exclude private static short staticWidth = 5;
    @Exclude private static short staticLongitude = 5;
    @Exclude private static short staticHeight = 10;

    public Tripiped() {
        this.width = 5;
        this.longitude = 10;
        this.height = 15;
    }

    public Tripiped(float width, float longitude, float height) {
        this.width = width;
        this.longitude = longitude;
        this.height = height;
    }

    @Override
    public int square() {
        return (int) (width * height * longitude);
    }

    @Extract
    public void deconstruct(FloatRef width, FloatRef longitude) {
        width.set(this.width);
        longitude.set(this.longitude);
    }

    @Extract
    public void deconstruct(FloatRef width, FloatRef longitude, FloatRef height) {
        width.set(this.width);
        longitude.set(this.longitude);
        height.set(this.height);
    }

    @Extract
    public static void unapply(FloatRef width, FloatRef longitude, FloatRef height) {
        width.set(staticWidth);
        longitude.set(staticLongitude);
        height.set(staticHeight);
    }

    public float width() {
        return width;
    }

    public float longitude() {
        return longitude;
    }

    public float height() {
        return height;
    }
}
