package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static ch.epfl.rigel.math.Angle.ofDeg;
import static java.lang.Math.*;
import static java.util.Arrays.asList;

public enum MoonModel implements CelestialObjectModel<Moon>{

    MOON(ofDeg(91.929336), ofDeg(130.143076), ofDeg(291.682547), ofDeg(5.145396), 0.0549);

    final private double LonM,  LonPer,  LonAsc,  inc,  exc;
    final private Double[] c = Stream.of(13.1763966, 0.1114041, 1.2739,
                                        0.1858 - 0.37, 6.2886, 0.214,
                                        0.6583, 0.0529539, 0.16, 0.5181)
                                        .map(Angle::ofDeg)
                                        .collect(Collectors.toList()).toArray(Double[]::new);
    private MoonModel(double lonM, double lonPer, double lonAsc, double inc, double exc) {
        LonM = lonM;
        LonPer = lonPer;
        LonAsc = lonAsc;
        this.inc = inc;
        this.exc = exc;
    }

    @Override
    public Moon at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {

        double lonOrbM = c[0]*daysSinceJ2010 + LonM;
        double AnMoy = lonOrbM - c[1]*daysSinceJ2010-LonPer;

        Sun sun = SunModel.SUN.at(daysSinceJ2010, eclipticToEquatorialConversion);

        double sunLon = sun.eclipticPos().lon();
        double sinSun = sin(sun.meanAnomaly());

        double Evection = c[2]* sin(2*(lonOrbM - sunLon)-AnMoy);

        double anomaly = AnMoy + Evection - (c[3])*sinSun;
        double CorrC = c[4] * sin(anomaly);

        double lonOrbCorr = lonOrbM + Evection + CorrC - Evection +  c[5] * sin(2*anomaly);
        double lonOrb = lonOrbCorr + c[6]* sin(2*lonOrbCorr - sunLon);

        double lonCorrAsc = lonOrb - LonAsc - c[7]*daysSinceJ2010 - c[8]*sinSun;

        return new Moon(eclipticToEquatorialConversion.apply(
                EclipticCoordinates.of(
                        atan2(sin(lonCorrAsc)*cos(inc), cos(lonCorrAsc)) - lonCorrAsc + lonOrb,
                        asin(sin(lonCorrAsc)*sin(inc)))),
                (float)((1+exc*cos(anomaly+CorrC))/(1-pow(exc, 2)) * c[9]),
                0, (float)(1- cos(lonOrb - lonOrbM - sunLon)/2) );
    }
}
