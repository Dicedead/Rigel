package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class HorizontalCoordinates extends SphericalCoordinates {

    /**
     * Constructor for HorizontalCoordinates
     *
     * @param az  input in radians for azimuth
     * @param alt input in radians for altitude
     */
    private HorizontalCoordinates(double az, double alt) {
        super(az, alt);
    }

    /**
     * Constructs a HorizontalCoordinates
     *
     * @param az  input in radians for azimuth
     * @param alt input in radians for altitude
     */
    public static HorizontalCoordinates of(double az, double alt) {

        return new HorizontalCoordinates(

                Preconditions.checkInInterval(RightOpenInterval.of(0, Angle.TAU), az),
                Preconditions.checkInInterval(ClosedInterval.symmetric(Math.PI), alt));
    }

    /**
     * Constructs a HorizontalCoordinates
     *
     * @param azDeg  input in degrees for azimuth
     * @param altDeg input in degrees for altitude
     */
    public static HorizontalCoordinates ofDeg(double azDeg, double altDeg) {
        Preconditions.checkInInterval(RightOpenInterval.symmetric(360), azDeg);
        Preconditions.checkInInterval(ClosedInterval.symmetric(180), altDeg);

        return new HorizontalCoordinates(Angle.ofDeg(azDeg), Angle.ofDeg(altDeg));
    }

    /**
     * Getter for azimuth in radians
     *
     * @return azimuth in radians
     */
    public double az() {
        return lon();
    }

    /**
     * Getter for azimuth in degrees
     *
     * @return azimuth in degrees
     */
    public double azDeg() {
        return lonDeg();
    }

    /**
     * Calculates and outputs azimuth's octant in desired string format
     *
     * @param n (desired) placeholder for North
     * @param e placeholder for East
     * @param s placeholder for South
     * @param w placeholder for West
     * @return String with concatenated octant representation
     */
    public String azOctantName(String n, String e, String s, String w) {

        int current = 0;
        String[] tab = {n, n + e, e, s + e, s, s + w, w, n + w};

        while (!RightOpenInterval.of(current * Math.PI / 4 - Math.PI / 8,
                (current + 1) * Math.PI / 4 - Math.PI / 8).contains(az())) {
            ++current;
        }

        return tab[current];
    }

    /**
     * Getter for altitude in radians
     * @return altitude in radians
     */
    public double alt() {
        return lat();
    }

    /**
     * Getter for altitude in degrees
     * @return altitude in degrees
     */
    public double altDeg() {
        return latDeg();
    }

    /**
     * Compute angular distance between two points
     *
     * @param that the HorizontalCoordinates which's distance to this will be computed
     * @return (double) angular distance between this and (HorizCoords) that
     */
    public double angularDistanceTo(HorizontalCoordinates that) {
        return Math.acos(
               Math.sin(this.alt())
             * Math.sin(that.alt())
             +
               Math.cos(this.alt())
             * Math.cos(that.alt())
             * Math.cos(this.az() - that.az()));
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
}
