package ch.epfl.rigel.math;

import ch.epfl.rigel.Preconditions;

/**
 * Angle utilities
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Angle {
    static final public double TAU = 2 * Math.PI;

    //Number of ...
    static final private double SECONDS_IN_MINUTES = 60.;
    static final private double MINUTES_IN_HOURS = 60.;
    static final private double HOUR_IN_DEGREE = 1. / 15;
    static final private double DEGREE_IN_HOUR = 15.;

    static final private double RATIO_MIN_RAD = TAU / (360 * MINUTES_IN_HOURS);
    static final private double RATIO_RAD_DEG = 360. / TAU;
    static final private double RATIO_DEG_RAD = TAU / 360.;
    static final private double RATIO_SEC_RAD = TAU / (360 * MINUTES_IN_HOURS * SECONDS_IN_MINUTES);
    static final private RightOpenInterval NORMALIZING_INTERVAL = RightOpenInterval.of(0,TAU);

    /**
     * Normalize a value on the right open interval [0, TAU[
     *
     * @param rad Angle to be reduced
     * @return Reduced value of the angle
     */
    static public double normalizePositive(double rad) {
        return //Normalizing twice for bug fixing: TAU was mapped to TAU and not 0 when applied only once
                NORMALIZING_INTERVAL.reduce(NORMALIZING_INTERVAL.reduce(rad));
    }

    /**
     * @param sec an angle in arc seconds
     * @return its corresponding angle in radians
     */
    static public double ofArcsec(double sec) {
        return RATIO_SEC_RAD * sec;
    }

    /**
     * DMS --> Rad
     *
     * @param deg Degrees
     * @param min Minutes
     * @param sec Seconds
     * @return Angle in radians
     */
    static public double ofDMS(int deg, int min, double sec) {
        Preconditions.checkArgument(min >= 0 && sec >= 0 && min < 60 && sec < 60);
        return deg * RATIO_DEG_RAD + min * RATIO_MIN_RAD + sec * RATIO_SEC_RAD;
    }

    /**
     * Deg --> Rad
     *
     * @param deg angle to convert in rad
     * @return converted angle in rad
     */
    static public double ofDeg(double deg) {
        return deg * RATIO_DEG_RAD;
    }

    /**
     * Rad --> Deg
     *
     * @param rad angle to convert in degrees
     * @return converted angle in degrees
     */
    static public double toDeg(double rad) {
        return rad * RATIO_RAD_DEG;
    }

    /**
     * Hours --> Degrees --> Radians
     *
     * @param hr hours to convert
     * @return hr but in rad
     */
    static public double ofHr(double hr) {
        return hr * DEGREE_IN_HOUR * RATIO_DEG_RAD;
    }

    /**
     * Rad --> Degrees --> Hours
     *
     * @param rad angle to convert
     * @return Hours converted
     */
    static public double toHr(double rad) {
        return toDeg(rad) * HOUR_IN_DEGREE;
    }
}
