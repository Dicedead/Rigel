package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;

import static ch.epfl.rigel.math.Angle.*;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Mathematical model of the Sun
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public enum SunModel implements CelestialObjectModel<Sun> {

    SUN;

    static private final double RATIO    = TAU / 365.242191;
    static private final double THETA_0  = ofDeg(0.533128);
    static private final double LON_2010 = ofDeg(279.557208);
    static private final double LON_PER = ofDeg(283.112438);
    static private final double EXCENT  = 0.016705;

    /**
     * Builds the Sun at a given point in time.
     *
     * @param daysSinceJ2010                 (double)
     * @param eclipticToEquatorialConversion Celestial object's coordinates
     * @return (Sun) Fully created Sun with appropriate time, geographic and physical parameters.
     */
    @Override
    public Sun at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {

        final double meanAnomaly = RATIO * daysSinceJ2010 + LON_2010 - LON_PER;
        final double trueAnomaly = meanAnomaly + 2 * EXCENT * sin(meanAnomaly);

        final EclipticCoordinates eclipticCoordinates = EclipticCoordinates.of(normalizePositive(trueAnomaly + LON_PER), 0);

        return new Sun(eclipticCoordinates, eclipticToEquatorialConversion.apply(eclipticCoordinates),
                (float) (THETA_0 * (1 + EXCENT * cos(trueAnomaly)) / (1 - Math.pow(EXCENT, 2))), //Angular size
                (float) (meanAnomaly));
    }
}
