package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;

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
    SUN(ofDeg(279.557208), ofDeg(283.112438), 0.016705);
    static private final double Ratio = TAU / 365.242191;
    private final double lon2010, lonPer, excent;

    SunModel(double ofDeg, double ofDeg1, double excentricity) {
        lon2010 = ofDeg;
        lonPer = ofDeg1;
        this.excent = excentricity;
    }

    /**
     * Builds the Sun at a given point in time.
     *
     * @param daysSinceJ2010                 (double)
     * @param eclipticToEquatorialConversion Celestial object's coordinates
     * @return (Sun) Fully created Sun with appropriate time, geographic and physical parameters.
     */
    @Override
    public Sun at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {

        double meanAnomaly = Ratio * daysSinceJ2010 + lon2010 - lonPer;

        double trueAnomaly = meanAnomaly + 2 * excent * sin(meanAnomaly);

        EclipticCoordinates eclipticCoordinates = EclipticCoordinates.of(normalizePositive(trueAnomaly + lonPer), 0);

        return new Sun(eclipticCoordinates, eclipticToEquatorialConversion.apply(eclipticCoordinates),
                (float) (ofDeg(0.533128) * (1 + excent * cos(trueAnomaly)) / (1 - Math.pow(excent, 2))), (float) (meanAnomaly));
    }
}
