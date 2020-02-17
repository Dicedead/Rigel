package ch.epfl.rigel.math;

import static org.junit.jupiter.api.Assertions.*;

class PolynomialTest {

    private static Polynomial polynomial;

    @org.junit.jupiter.api.BeforeAll
    private static void setUp ()
    {
        polynomial = Polynomial.of(4, -5, 6, 1, 0, 4, -5, 6, 3, 7, 0, 0, 0, -5, 6, 7);
    }

    @org.junit.jupiter.api.Test
    void at() {

        assertEquals(43766953, polynomial.at(3) );
    }

    @org.junit.jupiter.api.Test
    void testToString() {
        assertEquals("4.0x^15-5.0x^14+6.0x^13+x^12+4.0x^10-5.0x^9+6.0x^8+3.0x^7+7.0x^6-5.0x^2+6.0x+7.0", polynomial.toString());

    }
}