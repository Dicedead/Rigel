package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import org.junit.jupiter.api.Test;

import static ch.epfl.rigel.math.Angle.*;
import static org.junit.jupiter.api.Assertions.*;

class StarTest {

    static public final Star Rigel = new Star(1,
            "Rigel",
            EquatorialCoordinates.of(ofDMS(75, 14, 32.3), ofDMS( -8, 12, 6)),
            (float)(-6.69),
            (float)(-0.03));
    @Test
    void hipparcosId() {
        assertEquals(0, Rigel.hipparcosId());
    }

    @Test
    void colorTemperature() {
        assertEquals(12000, Rigel.colorTemperature(), 1000);
    }
}