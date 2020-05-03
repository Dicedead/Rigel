package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Horizontal coordinates system
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class HorizontalCoordinates extends SphericalCoordinates {

    private final static RightOpenInterval LON_INTERVAL_RAD_0toTAU = RightOpenInterval.of(0, Angle.TAU);
    private final static ClosedInterval LAT_INTERVAL_RAD_SYM_PI = ClosedInterval.symmetric(Math.PI);

    private final static RightOpenInterval LON_INTERVAL_DEG_0to360 = RightOpenInterval.of(0, 360);
    private final static ClosedInterval LAT_INTERVAL_DEG_SYM_180 = ClosedInterval.symmetric(180);

    private final static Function<Integer, Double> DEFINE_OCTANT_EDGE = A -> A * Math.PI / 4 - Math.PI / 8;

    /**
     * Constructor for HorizontalCoordinates
     *
     * @param az  (double) input in radians for azimuth
     * @param alt (double) input in radians for altitude
     */
    private HorizontalCoordinates(double az, double alt) {
        super(az, alt);
    }

    /**
     * Constructs a HorizontalCoordinates (factory constructor)
     *
     * @param az  (double) input in radians for azimuth
     * @param alt (double) input in radians for altitude
     */
    public static HorizontalCoordinates of(double az, double alt) {

        return new HorizontalCoordinates(

                Preconditions.checkInInterval(LON_INTERVAL_RAD_0toTAU, az),
                Preconditions.checkInInterval(LAT_INTERVAL_RAD_SYM_PI, alt));
    }

    /**
     * Constructs a HorizontalCoordinates
     *
     * @param azDeg  (double) input in degrees for azimuth
     * @param altDeg (double) input in degrees for altitude
     */
    public static HorizontalCoordinates ofDeg(double azDeg, double altDeg) {
        Preconditions.checkInInterval(LON_INTERVAL_DEG_0to360, azDeg);
        Preconditions.checkInInterval(LAT_INTERVAL_DEG_SYM_180, altDeg);

        return new HorizontalCoordinates(Angle.ofDeg(azDeg), Angle.ofDeg(altDeg));
    }

    /**
     * @return (double) azimuth in radians
     */
    public double az() {
        return super.lon();
    }

    /**
     * @return (double) azimuth in degrees
     */
    public double azDeg() {
        return super.lonDeg();
    }

    /**
     * @return (double) altitude in radians
     */
    public double alt() {
        return super.lat();
    }

    /**
     * @return (double) altitude in degrees
     */
    public double altDeg() {
        return super.latDeg();
    }

    /**
     * Computes and outputs azimuth's octant in desired string format
     * <p>
     * n,e,s & w are Strings:
     *
     * @param n placeholder for North
     * @param e placeholder for East
     * @param s placeholder for South
     * @param w placeholder for West
     * @return (String) String with concatenated octant representation
     */
    public String azOctantName(String n, String e, String s, String w) {
        return List.of(n, n + e, e, s + e, s, s + w, w, n + w)
                .get(azOctantRecur(0) % 8);
    }

    /**
     * Auxiliary method for finding azimuth's octant
     *
     * @param current (int) incrementing int
     * @return (int) az in octant: current, if not: recursive call with current <- current + 1
     */
    private int azOctantRecur(int current) {
        return RightOpenInterval.of(
                DEFINE_OCTANT_EDGE.apply(current),
                DEFINE_OCTANT_EDGE.apply(current + 1))
                .contains(az()) ?
                current : azOctantRecur(current + 1);
    }

    /**
     * Compute angular distance between two points
     *
     * @param that (HorizontalCoordinates) the HorizontalCoordinates which's distance to this will be computed
     * @return (double) angular distance between this and (HorizCoords) that
     */
    public double angularDistanceTo(HorizontalCoordinates that) {

        return
                Math.acos(Math.sin(this.alt()) * Math.sin(that.alt())
                        + Math.cos(this.alt()) * Math.cos(that.alt()) * Math.cos(this.az() - that.az()));
    }

    /**
     * toString override for HorizontalCoordinates
     *
     * @return (String) 4 decimal precision of azimuth and altitude in degrees
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(az=%.4f°, alt=%.4f°)", azDeg(), altDeg());
    }

    /**
     * Had not to throw UOE for performance
     */
    public boolean equals(Object o) {
        return o == this;
    }
}
