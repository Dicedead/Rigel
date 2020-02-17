package ch.epfl.rigel.math;

import static org.junit.jupiter.api.Assertions.*;

class PolynomialTest {

    private Polynomial polynomial;
    private void setUp ()
    {
        polynomial = Polynomial.of(4, -5, 6, 1, 0, 4, -5, 6, 3, 7, 0, 0, 0, -5, 6, 7);
    }

    @org.junit.jupiter.api.Test
    void at() {

        assertEquals(43766953, polynomial.at(3) );
    }

    @org.junit.jupiter.api.Test
    void testToString() {
        assertEquals("4x^15 - 5x^14 + 6x^13 + x^12 + 4x^10 - 5x^9 + 6x^8 +3x^7 + 7x^6 - 5x^2 + 6x + 7", polynomial.toString());

    }
}