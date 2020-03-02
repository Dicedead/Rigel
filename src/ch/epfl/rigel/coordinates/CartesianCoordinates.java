package ch.epfl.rigel.coordinates;
/**
 * Cartesian coordinates representaation
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class CartesianCoordinates {

    private CartesianCoordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Allows to construct a cartesian system
     * @param x coordinate
     * @param y coordinate
     * @return a cartesian coordinates
     */
    static CartesianCoordinates of(double x, double y)
    {
        return new CartesianCoordinates(x, y);
    }

    /**
     *
     * @return x coordinate
     */
    public double x() {
        return x;
    }

    /**
     *
     * @return y coordinates
     */
    public double y() {
        return y;
    }

    private final double x, y;

}
