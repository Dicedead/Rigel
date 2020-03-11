package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import java.awt.*;

import static ch.epfl.rigel.math.Angle.*;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Mathematical model of the Sun
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public enum SunModel implements CelestialObjectModel<Sun>
{
    SUN(ofDeg(279.557208), ofDeg(283.112438), ofArcsec(0.016705));
    static private final double Ratio = TAU/365.242191;
    static private final double doubleE = 2*Math.exp(1);
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

        double meanAnomaly = Ratio * daysSinceJ2010 + Lon2010 - LonPer;

        double trueAnomaly = meanAnomaly + doubleE*sin(meanAnomaly);

        EclipticCoordinates eq = EclipticCoordinates.of(normalizePositive(trueAnomaly + LonPer), 0);

        float angularSize = (float)(ofDeg(0.533128)*(1+Math.exp(1)*cos(trueAnomaly))/(1-Math.exp(2)));

        return new Sun(eq ,eclipticToEquatorialConversion.apply(eq), angularSize, (float)(meanAnomaly));
    }
}
