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

    MOON;

    static final private double LON_M = ofDeg(91.929336);
    static final private double LON_PER = ofDeg(130.143076);
    static final private double LON_ASC = ofDeg(291.682547);
    static final private double INC = ofDeg(5.145396);
    static final private double EXC = 0.0549;

    //Converting many nameless constants to rad
    static final private Double[] c =
            Stream.of(13.1763966, 0.1114041, 1.2739,
                    0.1858 + 0.37, 6.2886, 0.214,
                    0.6583, 0.0529539, 0.16, 0.5181, 0.37)
                    .map(Angle::ofDeg)
                    .collect(Collectors.toList()).toArray(Double[]::new);

    /**
     * Computes the Moon's position at a given time
     *
     * @param daysSinceJ2010        (double) Calculated through Epoch or by other means: days between time t at J2010
     * @param eclipToEquaConversion (EclipticToEquatorialConversion) at time t
     * @return (Moon) fully parametrized Moon
     */
    @Override
    public Moon at(double daysSinceJ2010, EclipticToEquatorialConversion eclipToEquaConversion) {

        //Mean anomaly
        final double lonOrbM = c[0] * daysSinceJ2010 + LON_M;
        final double AnMoy = lonOrbM - c[1] * daysSinceJ2010 - LON_PER;

        //Computation of Sun's values
        final Sun sun = SunModel.SUN.at(daysSinceJ2010, eclipToEquaConversion);

        final double sunLon = sun.eclipticPos().lon();
        final double sin_sunMeanAnomaly = sin(sun.meanAnomaly());

        //Computing ingredients for Moon's position, phase and angular size
        final double evection = c[2] * sin(2 * (lonOrbM - sunLon) - AnMoy);

        final double anomaly = AnMoy + evection - c[3] * sin_sunMeanAnomaly;
        final double CorrC = c[4] * sin(anomaly);

        final double lonOrbCorr = lonOrbM + evection + CorrC - (c[3] - c[10]) * sin_sunMeanAnomaly + c[5] * sin(2 * anomaly);
        final double lonOrb = lonOrbCorr + c[6] * sin(2 * (lonOrbCorr - sunLon));

        final double lonCorrAsc = LON_ASC - c[7] * daysSinceJ2010 - c[8] * sin_sunMeanAnomaly;
        final double lonOrb_lonCorrAsc = lonOrb - lonCorrAsc;

        return new Moon(eclipToEquaConversion.apply(
                EclipticCoordinates.of(
                        Angle.normalizePositive(atan2(sin(lonOrb_lonCorrAsc) * cos(INC), cos(lonOrb_lonCorrAsc)) + lonCorrAsc),
                        asin(sin(lonOrb_lonCorrAsc) * sin(INC)))),
                (float) (((1 + EXC * cos(anomaly + CorrC)) / (1 - EXC * EXC)) * c[9]),
                0, (float) ((1 - cos(lonOrb - sunLon)) / 2));
    }
}
