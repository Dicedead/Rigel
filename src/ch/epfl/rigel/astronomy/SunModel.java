package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;

import static ch.epfl.rigel.math.Angle.ofArcsec;
import static ch.epfl.rigel.math.Angle.ofDeg;

/**
 * Mathematical model of the Sun
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public enum SunModel implements CelestialObjectModel<Sun>
{
    SUN(ofDeg(279.557208), ofDeg(283.112438), ofArcsec(0.016705));

    private final double Lon2010, LonPer, Ex;
    SunModel(double ofDeg, double ofDeg1, double ofArcsec) {
        Lon2010 = ofDeg;
        LonPer = ofDeg1;
        Ex = ofArcsec;
    }

    /**
     * Builds the Sun at a given point and time.
     *
     * @param daysSinceJ2010 (double)
     * @param eclipticToEquatorialConversion Celestial object's coordinates
     * @return (Sun) Fully created Sun with appropriate time, geographic and physical parameters.
     */
    @Override
    public Sun at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {
        return null;
    }
}
