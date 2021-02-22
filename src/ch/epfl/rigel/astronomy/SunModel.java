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

    private final static double RATIO = TAU / 365.242191;
    private final static double THETA_0 = ofDeg(0.533128);
    private final static double LON_2010 = ofDeg(279.557208);
    private final static double LON_PER = ofDeg(283.112438);
    private final static double EXCENT = 0.016705;
    private final static double ONE_MIN_EXC2 = 1 - EXCENT * EXCENT;

    /**
     * Builds the Sun at a given point in time.
     *
     * @param daysSinceJ2010        (double)
     * @param eclipToEquaConversion Celestial object's coordinates
     * @return (Sun) Fully created Sun with appropriate time, geographic and physical parameters.
     */
    @Override
    public Sun at(double daysSinceJ2010, EclipticToEquatorialConversion eclipToEquaConversion) {

        double meanAnomaly = RATIO * daysSinceJ2010 + LON_2010 - LON_PER;
        double trueAnomaly = meanAnomaly + 2 * EXCENT * sin(meanAnomaly);

        EclipticCoordinates eclipCoords = EclipticCoordinates.of(normalizePositive(trueAnomaly + LON_PER), 0);

        return new Sun(eclipCoords, eclipToEquaConversion.apply(eclipCoords),
                (float) (THETA_0 * (1 + EXCENT * cos(trueAnomaly)) / (ONE_MIN_EXC2)), //Angular size
                (float) (meanAnomaly));
    }
}
