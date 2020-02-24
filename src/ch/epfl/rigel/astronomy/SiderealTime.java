package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.GeographicCoordinates;

import java.time.ZonedDateTime;

import static ch.epfl.rigel.astronomy.Epoch.J2000;

public final class SiderealTime {

    private SiderealTime () {}

    public static double greenwich(ZonedDateTime when)
    {
        double T = J2000.julianCenturiesUntil(when);
        double t = 
        return 0;
    }

    public static double local (ZonedDateTime when, GeographicCoordinates where)
    {
        return 0;
    }
}
