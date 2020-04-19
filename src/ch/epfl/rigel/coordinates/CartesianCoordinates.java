package ch.epfl.rigel.coordinates;


/**
 * Cartesian coordinates representation
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class CartesianCoordinates {

    private final double x, y;

    private CartesianCoordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructs a cartesian system (factory constructor)
     *
     * @param x (double) x coordinate
     * @param y (double) y coordinate
     * @return (CartesianCoordinates)
     */
    public static CartesianCoordinates of(double x, double y) {
        return new CartesianCoordinates(x, y);
    }

    /**
     * @return (double) x coordinate
     */
    public double x() {
        return x;
    }

    /**
     * @return (double) y coordinate
     */
    public double y() {
        return y;
    }

    /**
     * @return (String) "CartesianCoordinates : (x,y)"
     */
    @Override
    public String toString() {
        return "CartesianCoordinates : (" + x + " ; " + y + ")";
    }

    /**
     * @throws UnsupportedOperationException (double precision does not allow for equals)
     * @see Object#equals(Object)
     */
    @Override
    public final boolean equals(Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException (double precision does not allow for hashcode)
     * @see Object#hashCode()
     */
    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }
}
