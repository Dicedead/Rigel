package ch.epfl.rigelTest.astronomy;

import ch.epfl.rigel.astronomy.MoonModel;
import ch.epfl.rigel.astronomy.SunModel;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static ch.epfl.rigel.astronomy.Epoch.J2010;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyMoonModelTest {

    @Test
    void atWorksOnKnownValues() {
        assertEquals(14 + 12/60.0 + 42/3600d,
                MoonModel.MOON.at(J2010.daysUntil(ZonedDateTime.of(
                        LocalDate.of(2003,9,1),
                        LocalTime.of(0,0),
                        ZoneOffset.UTC
                )),new EclipticToEquatorialConversion(ZonedDateTime.of(
                        LocalDate.of(2003,9,1),
                        LocalTime.of(0,0),
                        ZoneOffset.UTC
                ))).equatorialPos().raHr());
    }
}
