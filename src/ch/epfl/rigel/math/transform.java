package ch.epfl.rigel.math;

import ch.epfl.rigel.coordinates.CartesianCoordinates;

import java.util.function.BiFunction;

public class transform {

    final double Mxx, Mxy, Myy, Myx, Tx, Ty;
    public transform(double mxx, double mxy, double myy, double myx, double tx, double ty) {
        Mxx = mxx;
        Mxy = mxy;
        Myy = myy;
        Myx = myx;
        Tx = tx;
        Ty = ty;
    }

    public CartesianCoordinates apply(final Double x, final Double y) {
        return CartesianCoordinates.of(Mxx * x + Mxy * y + Tx,
                Myx * x + Myy * y + Ty);
    }

    public CartesianCoordinates applyDelta(final Double x, final Double y) {
        return CartesianCoordinates.of(Mxx * x + Mxy * y,
                Myx * x + Myy * y);
    }
}
