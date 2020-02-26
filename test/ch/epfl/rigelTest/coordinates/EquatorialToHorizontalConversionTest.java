package ch.epfl.rigelTest.coordinates;

import ch.epfl.rigel.astronomy.SiderealTime;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import org.junit.jupiter.api.Test;

import static ch.epfl.rigel.math.Angle.ofDeg;
import static java.lang.Math.*;
import static org.junit.jupiter.api.Assertions.*;

class EquatorialToHorizontalConversionTest {

    @Test
    void apply() {
        double A    = ofDeg(76.728973);
        double h    = ofDeg(19.334345);
        double H    = ofDeg(87.933333);
        double phi  = ofDeg(52);
        double dec  = ofDeg(23.219444);

        double sinPhi       = sin(phi);
        double cosPhi       = cos(phi);
        double sinDec       = sin(dec);

        double term1        = sinDec*sinPhi + cosPhi*cos(dec)*cos((H));
        double h_           = asin(term1);
        double A_           = acos((sinDec - sinPhi*term1)/(cos(h_)*cosPhi));

        assertEquals(A, A_, 0.0000001);
        assertEquals(h, h_, 0.0000001);

    }
}