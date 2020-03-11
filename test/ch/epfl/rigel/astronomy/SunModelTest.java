package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import org.junit.jupiter.api.Test;

import java.time.*;

import static ch.epfl.rigel.math.Angle.normalizePositive;
import static ch.epfl.rigel.math.Angle.ofDMS;
import static org.junit.jupiter.api.Assertions.*;

class SunModelTest {

    @Test
    void at() {
        assertEquals(EquatorialCoordinates.of(normalizePositive(ofDMS(-30, 39, 38.44)), ofDMS(-8, 28, 12)).ra(),
                SunModel.SUN.at(27 + 31, new EclipticToEquatorialConversion(ZonedDateTime.of(LocalDate.of(2010, Month.FEBRUARY, 27),
                        LocalTime.of(0,0), ZoneOffset.UTC))).equatorialPos().ra());
    }
}