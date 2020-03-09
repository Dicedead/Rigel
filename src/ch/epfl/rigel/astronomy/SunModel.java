package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;

import static ch.epfl.rigel.math.Angle.ofArcsec;
import static ch.epfl.rigel.math.Angle.ofDeg;

public enum SunModel implements CelestialObjectModel<Sun>
{
    SUN(ofDeg(279.557208), ofDeg(283.112438), ofArcsec(0.016705));

    private final double Lon2010, LonPer, Ex;
    SunModel(double ofDeg, double ofDeg1, double ofArcsec) {
        Lon2010 = ofDeg;
        LonPer = ofDeg1;
        Ex = ofArcsec;
    }

    @Override
    public Sun at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {
        return null;
    }
}
