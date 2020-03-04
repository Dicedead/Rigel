package ch.epfl.rigelTest.astronomy;

import ch.epfl.rigel.astronomy.Moon;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoonTest {

    private final static Moon testMoon = new Moon(EquatorialCoordinates.of(0,0),3,3,0.2347f);

    @Test
    void info() {
        assertEquals("Lune (23.5%)",testMoon.info());
    }

    @Test
    void name() {
        assertEquals("Lune",testMoon.name());
    }

    @Test
    void angularSize() {
        assertEquals(3,testMoon.angularSize());
    }
}