package ch.epfl.rigel.math;

import ch.epfl.rigel.Preconditions;

public class Angle
{
    static final public double TAU = 2 * Math.PI;

    //Number of ...
    static final public double SECONDS_IN_MINUTES   = 60.;
    static final public double MINUTES_IN_HOURS     = 60.;
    static final public double HOUR_IN_DEGREE       = 1./15;
    static final public double DEGREE_IN_HOUR       = 15.;

    static final public double RATIO_MIN_RAD        = TAU / (360 * MINUTES_IN_HOURS);
    static final public double RATIO_RAD_DEG        = 360./ TAU;
    static final public double RATIO_DEG_RAD        = TAU /360.;
    static final public double RATIO_SEC_RAD        = TAU / (360 * MINUTES_IN_HOURS * SECONDS_IN_MINUTES);



    /**
     * Normalise a value on the right open interval [0, TAU[
     * @param rad Angle to be reduced
     * @return Reduced value of the angle
     */
    static public double normalizePositive(double rad)
    {
       return RightOpenInterval.symmetric(TAU).reduce(rad);
    }

    /**
     * @param sec an angle in arc seconds
     * @return its corresponding angle in radians
     */
    static public double ofArcsec(double sec)
    {
        return RATIO_SEC_RAD * sec;
    }

    /**
     * DMS --> Rad
     * @param deg Degrees
     * @param min Minutes
     * @param sec Seconds
     * @return Angle in radians
     */
    static public double ofDMS(int deg, int min, double sec)
    {
        Preconditions.checkArgument(min >= 0 && sec >= 0);
        return deg*RATIO_DEG_RAD + min * RATIO_MIN_RAD + sec * RATIO_SEC_RAD;
    }

    /**
     * Deg --> Rad
     * @param deg angle to convert in rad
     * @return converted angle in rad
     */
    static public double ofDeg(double deg)
    {
        return deg*RATIO_DEG_RAD;
    }

    /**
     * Rad --> Deg
     * @param rad angle to convert in degrees
     * @return converted angle in degrees
     */
    static public double toDeg(double rad)
    {
        return rad*RATIO_RAD_DEG;
    }

    /**
     * Hours --> Degrees --> Radians
     * @param hr hours to convert
     * @return hr but in rad
     */
    static public double ofHr(double hr)
    {
        return hr*DEGREE_IN_HOUR*RATIO_DEG_RAD;
    }

    /**
     * Rad --> Degrees --> Hours
     * @param rad angle to convert
     * @return Hours converted
     */
    static public double toHr(double rad)
    {
        return toDeg(rad) *HOUR_IN_DEGREE;
    }
}
