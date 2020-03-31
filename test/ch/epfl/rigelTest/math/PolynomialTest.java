package ch.epfl.rigelTest.math;

import ch.epfl.rigel.math.Polynomial;
import ch.epfl.test.Chronometer;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static ch.epfl.test.TestRandomizer.newRandom;
import static java.lang.System.nanoTime;
import static org.junit.jupiter.api.Assertions.*;

class PolynomialTest {

    private static Polynomial polynomial;
    private static Polynomial polynomial2;

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
    void atNonInstanciatedPolynomial() {
        assertThrows(NullPointerException.class, () -> {polynomial2.at(1);});
    }

    @org.junit.jupiter.api.Test
    void testToString() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assertEquals("4x^15-5x^14+6x^13+x^12+4x^10-5x^9+6x^8+3x^7+7x^6-5x^2+6x+7", polynomial.toString());
        var a = Chronometer.battle(List.of(Polynomial.class.getMethod("at", double.class)), List.of(3.), new Polynomial[]{polynomial}, 1000);
        a.keySet().stream().forEach(k -> System.out.println(a.get(k)));
    }

}