package ch.epfl.rigelTest.astronomy;

import ch.epfl.rigel.astronomy.Star;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import org.junit.jupiter.api.Test;

import static ch.epfl.rigel.math.Angle.*;
import static org.junit.jupiter.api.Assertions.*;

class MyStarTest {

    static public final Star Rigel = new Star(1,
            "Rigel",
            EquatorialCoordinates.of(ofDMS(75, 14, 32.3), ofDMS( -8, 12, 6)),
            (float)(-6.69),
            (float)(-0.03));
    @Test
    void hipparcosId() {
        assertEquals(1, Rigel.hipparcosId());
    }

    @Test
    void colorTemperature() {
        assertEquals(10515,new Star(24436, "Rigel", EquatorialCoordinates.of(0, 0), 0, -0.03f)
                .colorTemperature());
        assertEquals( 3793,new Star(27989, "Betelgeuse", EquatorialCoordinates.of(0, 0), 0, 1.50f)
                .colorTemperature());
    }
}