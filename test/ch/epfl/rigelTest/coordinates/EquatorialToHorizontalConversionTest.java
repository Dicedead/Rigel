package ch.epfl.rigelTest.coordinates;

import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import static ch.epfl.rigel.math.Angle.normalizePositive;
import static ch.epfl.rigel.math.Angle.ofDeg;
import static java.lang.Math.*;
import static org.junit.jupiter.api.Assertions.*;

class EquatorialToHorizontalConversionTest {

    @Test
    void apply() {
        double A    = ofDeg(283.271027);
        double h    = ofDeg(19.334345);
        double H    = ofDeg(87.933333);
        double phi  = ofDeg(52);
        double dec  = ofDeg(23.219444);

        double sinPhi       = sin(phi);
        double cosPhi       = cos(phi);
        double sinDec       = sin(dec);

        double term1        = sinDec*sinPhi + cosPhi*cos(dec)*cos(H);
        double h_           = asin(term1);
        double denomA   = sinDec - sinPhi * term1;
        double numA     = -cosPhi*cos(dec)*sin(H);
        double A_        = atan2(numA,denomA);

        A_ = normalizePositive(A_);
        /*if(denomA < 0) {
            A_ += Math.PI;
        } else if (numA < 0) {
            A_ += Angle.TAU;
        }*/
        assertEquals(A, A_, 1e-6);
        assertEquals(h, h_, 1e-6);

    }
}