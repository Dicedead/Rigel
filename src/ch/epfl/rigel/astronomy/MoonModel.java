package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ch.epfl.rigel.math.Angle.ofDeg;
import static java.lang.Math.*;

/**
 * Mathematical model of the Moon
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public enum MoonModel implements CelestialObjectModel<Moon> {

    MOON(ofDeg(91.929336), ofDeg(130.143076), ofDeg(291.682547), ofDeg(5.145396), 0.0549);

    final private double lonM, lonPer, lonAsc, inc, exc;

    //Converting many constants to rad
    static final private Double[] c =
            Stream.of(13.1763966, 0.1114041, 1.2739,
            0.1858 + 0.37, 6.2886, 0.214,
            0.6583, 0.0529539, 0.16, 0.5181)
            .map(Angle::ofDeg)
            .collect(Collectors.toList()).toArray(Double[]::new);

    private MoonModel(double lonM, double lonPer, double lonAsc, double inc, double exc) {
        this.lonM = lonM;
        this.lonPer = lonPer;
        this.lonAsc = lonAsc;
        this.inc = inc;
        this.exc = exc;
    }

    /**
     * Computes the Moon's position at a given time
     *
     * @param daysSinceJ2010                 (double) Calculated through Epoch or by other means: days between time t at J2010
     * @param eclipticToEquatorialConversion at time t
     * @return (Moon) fully parametrized Moon
     */
    @Override
    public Moon at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {

        double lonOrbM = c[0] * daysSinceJ2010 + lonM;
        double AnMoy = lonOrbM - c[1] * daysSinceJ2010 - lonPer;

        Sun sun = SunModel.SUN.at(daysSinceJ2010, eclipticToEquatorialConversion);

        double sunLon = sun.eclipticPos().lon();
        double sin_sunMeanAnomaly = sin(sun.meanAnomaly());

        double evection = c[2] * sin(2 * (lonOrbM - sunLon) - AnMoy);

        double anomaly = AnMoy + evection - c[3] * sin_sunMeanAnomaly;
        double CorrC = c[4] * sin(anomaly);

        double lonOrbCorr = lonOrbM + evection + CorrC - (c[3] - Angle.ofDeg(0.37)) * sin_sunMeanAnomaly + c[5] * sin(2 * anomaly);
        double lonOrb = lonOrbCorr + c[6] * sin(2 * (lonOrbCorr - sunLon));

        double lonCorrAsc = lonAsc - c[7] * daysSinceJ2010 - c[8] * sin_sunMeanAnomaly;
        double lonOrb_lonCorrAsc = lonOrb - lonCorrAsc;

        return new Moon(eclipticToEquatorialConversion.apply(
                EclipticCoordinates.of(
                        Angle.normalizePositive(atan2(sin(lonOrb_lonCorrAsc) * cos(inc), cos(lonOrb_lonCorrAsc)) + lonCorrAsc),
                        asin(sin(lonOrb_lonCorrAsc) * sin(inc)))),
                (float) (((1 + exc * cos(anomaly + CorrC)) / (1 - pow(exc, 2))) * c[9]),
                0, (float) ((1 - cos(lonOrb - lonOrbM - sunLon)) / 2));
    }
}
