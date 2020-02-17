package ch.epfl.rigel.math;

import static org.junit.jupiter.api.Assertions.*;

class PolynomialTest {

    private Polynomial polynomial;
    private void setUp ()
    {
        polynomial = Polynomial.of(4, 5, 6, 1, 0, 4, 5, 6, 3, 7, 0, 0, 0, 5, 6, 7);
    }

    @org.junit.jupiter.api.Test
    void at() {

        assertEquals(0,polynomial.at(5678) );
    }

    @org.junit.jupiter.api.Test
    void testToString() {
        assertEquals("xc", polynomial.toString());

    }
}