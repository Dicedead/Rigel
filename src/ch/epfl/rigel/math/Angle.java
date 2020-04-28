package ch.epfl.rigel.math;

import ch.epfl.rigel.Preconditions;

/**
 * Angle utilities
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Angle {

    private Angle() {
        throw new UnsupportedOperationException("Tried to instantiate instantiable class Angle.");
    }

    public final static double TAU = 2 * Math.PI;

    //Number of ...
    private final static double SECONDS_IN_MINUTES = 60d;
    private final static double MINUTES_IN_HOURS = 60d;
    private final static double HOUR_IN_DEGREE = 1d / 15;
    private final static double DEGREE_IN_HOUR = 15d;

    private final static double RATIO_MIN_RAD = TAU / (360 * MINUTES_IN_HOURS);
    private final static double RATIO_RAD_DEG = 360 / TAU;
    private final static double RATIO_DEG_RAD = TAU / 360;
    private final static double RATIO_SEC_RAD = TAU / (360d * MINUTES_IN_HOURS * SECONDS_IN_MINUTES);
    private final static RightOpenInterval NORMALIZING_INTERVAL = RightOpenInterval.of(0, TAU);

    /**
     * Normalize a value on the right open interval [0, TAU[
     *
     * @param rad (double) Angle to be reduced
     * @return (double) Reduced value of the angle
     */
    static public double normalizePositive(double rad) {
        return NORMALIZING_INTERVAL.reduce(rad);
    }

    /**
     * @param sec (double) an angle in arc seconds
     * @return (double) its corresponding angle in radians
     */
    static public double ofArcsec(double sec) {
        return RATIO_SEC_RAD * sec;
    }

    /**
     * DMS --> Rad
     *
     * @param deg (double) Degrees
     * @param min (double) Minutes
     * @param sec (double) Seconds
     * @return (double) Angle in radians
     */
    static public double ofDMS(int deg, int min, double sec) {
        Preconditions.checkArgument(deg >= 0 && min >= 0 && sec >= 0 && min < 60 && sec < 60);
        return deg * RATIO_DEG_RAD + min * RATIO_MIN_RAD + sec * RATIO_SEC_RAD;
    }

    /**
     * Deg --> Rad
     *
     * @param deg (double) angle to convert in rad
     * @return (double) converted angle in rad
     */
    static public double ofDeg(double deg) {
        return deg * RATIO_DEG_RAD;
    }

    /**
     * Rad --> Deg
     *
     * @param rad (double) angle to convert in degrees
     * @return (double) converted angle in degrees
     */
    static public double toDeg(double rad) {
        return rad * RATIO_RAD_DEG;
    }

    /**
     * Hours --> Degrees --> Radians
     *
     * @param hr (double) hours to convert
     * @return (double) hr but in rad
     */
    static public double ofHr(double hr) {
        return hr * DEGREE_IN_HOUR * RATIO_DEG_RAD;
    }

    /**
     * Rad --> Degrees --> Hours
     *
     * @param rad (double) angle to convert
     * @return (double) Hours converted
     */
    static public double toHr(double rad) {
        return toDeg(rad) * HOUR_IN_DEGREE;
    }
}
