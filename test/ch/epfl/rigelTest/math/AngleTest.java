package ch.epfl.rigelTest.math;

import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AngleTest {

    @Test
    void normalizePositive() {
        assertEquals(0, Angle.normalizePositive(6.283185307179586));
    }

    @Test
    void ofArcsec() {
        assertEquals(16.758985999089518, Angle.ofArcsec(3456789));
    }

    @Test
    void ofDMS() {
        assertThrows(IllegalArgumentException.class, () -> { Angle.ofDMS(1, 60, 0);});
    }

    @Test
    void ofDeg() {
        assertEquals(Math.PI, Angle.ofDeg(180));
    }

    @Test
    void toDeg() {
        assertEquals(180, Angle.toDeg(Math.PI));
    }

    @Test
    void ofHr() {
        assertEquals(Math.PI/12, Angle.ofHr(1));
    }

    @Test
    void toHr() {
        assertEquals(12, Angle.toHr(Math.PI));
    }
}