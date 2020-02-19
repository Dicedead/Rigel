package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.Locale;

public final class HorizontalCoordinates extends SphericalCoordinates {

    /**
     * Constructor for HorizontalCoordinates
     *
     * @param az input in radians for longitude
     * @param alt  input in radians for latitude
     */
    private HorizontalCoordinates(double az, double alt) {
        super(az, alt);
    }

    public static HorizontalCoordinates of(double az, double alt)
    {
        Preconditions.checkInInterval(RightOpenInterval.of(0,360),az);
        Preconditions.checkInInterval(ClosedInterval.of(-90,90),alt);

        return new HorizontalCoordinates(az,alt);
    }

    public static HorizontalCoordinates ofDeg(double azDeg, double altDeg)
    {
        Preconditions.checkInInterval(RightOpenInterval.symmetric(360), azDeg);
        Preconditions.checkInInterval(RightOpenInterval.symmetric(360), altDeg);

        return new HorizontalCoordinates(Angle.ofDeg(azDeg), Angle.ofDeg(altDeg));
    }

    public double az() {
        return lon();
    }

    public double azDef() {
        return lonDeg();
    }

    public String azOctantName(String n, String e, String s, String w)
    {

        int current = 0;
        String[] tab = {n, n+e, e, s+e, s, s+w, w, n+w};

        while(!RightOpenInterval.of(current, current + Math.PI/4).contains(az()))
            ++current;

        return tab[current];
    }

    public double alt() {
        return lat();
    }

    public double altDeg() {
        return latDeg();
    }

    public double angularDistanceTo(HorizontalCoordinates that)
    {
        return Math.acos(Math.sin(this.alt())
                *Math.sin(that.alt())
                + Math.cos(this.alt())
                *Math.cos(that.alt())
                *Math.cos(this.az() - that.az()));
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT,"(az=%.4f°, alt=%.4f°)",azDef(),altDeg());
    }
}
