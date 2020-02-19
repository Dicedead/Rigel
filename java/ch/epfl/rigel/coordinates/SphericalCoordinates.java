package ch.epfl.rigel.coordinates;

import static ch.epfl.rigel.math.Angle.toDeg;

class SphericalCoordinates
{

    final private double longitude;
    final private double latitude;


    SphericalCoordinates(double longitude, double latitude)
    {
        this.latitude   = latitude;
        this.longitude  = longitude;
    }

    double lon() { return longitude; }
    double lonDeg() {return toDeg(longitude);}
    double lat(){ return latitude; }
    double latDeg(){return toDeg(latitude);}

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public final boolean equals(Object o) {
        System.err.println("Fatal error : tried to test equality between intervals but double precision does not \n" +
                "allows it.");
        throw new UnsupportedOperationException();
    }

    @Override
    public final int hashCode() {
        System.err.println("Fatal error : tried to test equality between intervals but double precision does not \n" +
                "allows it.");
        throw new UnsupportedOperationException();
    }
}
