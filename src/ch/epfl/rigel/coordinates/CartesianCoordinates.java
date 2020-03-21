package ch.epfl.rigel.coordinates;


/**
 * Cartesian coordinates representaation
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
     * Constructs a cartesian system
     *
     * @param x coordinate
     * @param y coordinate
     * @return a cartesian coordinates
     */
    public static CartesianCoordinates of(double x, double y)
    {
        return new CartesianCoordinates(x, y);
    }

    /**
     * @return x coordinate
     */
    public double x() {
        return x;
    }

    /**
     * @return y coordinate
     */
    public double y() {
        return y;
    }

    @Override
    public String toString() {
        return "CartesianCoordinates : ("+x+" ; "+y+")";
    }

    @Override
    public final boolean equals(Object o) {
        //System.err.println("Fatal error : tried to test equality but double precision does not \n" +
        //        "allow it.");
        throw new UnsupportedOperationException();
    }

    @Override
    public final int hashCode() {
        //System.err.println("Fatal error : tried to test equality but double precision does not \n" +
        //        "allow it.");
        throw new UnsupportedOperationException();
    }
}
