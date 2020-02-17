package ch.epfl.rigelTest.math;

import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AngleTest {

    @Test
    void normalizePositive() {
        assertEquals(0, Angle.normalizePositive(12*Math.PI));
    }

    @Test
    void ofArcsec() {
        assertEquals(Math.PI, Angle.ofArcsec(3600*180));
    }

    @Test
    void ofDMS() {
        assertEquals(2 * Angle.RATIO_DEG_RAD, Angle.ofDMS(1, 60, 0));

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
        assertEquals(6, Angle.ofHr(Math.PI));
    }
}