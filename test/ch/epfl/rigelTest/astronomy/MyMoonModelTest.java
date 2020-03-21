package ch.epfl.rigelTest.astronomy;

import ch.epfl.rigel.astronomy.MoonModel;
import ch.epfl.rigel.astronomy.SunModel;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigelTest.math.UsefulMathTestingMethods;
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
    void atRaHr() {

        assertEquals(UsefulMathTestingMethods.hoursFromHMS(14, 12, 42),
                MoonModel.MOON.at(J2010.daysUntil(ZonedDateTime.of(
                        LocalDate.of(2003, 9, 1),
                        LocalTime.of(0, 0),
                        ZoneOffset.UTC
                )), new EclipticToEquatorialConversion(ZonedDateTime.of(
                        LocalDate.of(2003, 9, 1),
                        LocalTime.of(0, 0),
                        ZoneOffset.UTC
                ))).equatorialPos().raHr());

    }

    @Test
    void atDec() {

        assertEquals(Angle.ofDMS(-11, 31, 38),
                MoonModel.MOON.at(J2010.daysUntil(ZonedDateTime.of(
                        LocalDate.of(2003, 9, 1),
                        LocalTime.of(0, 0),
                        ZoneOffset.UTC
                )), new EclipticToEquatorialConversion(ZonedDateTime.of(
                        LocalDate.of(2003, 9, 1),
                        LocalTime.of(0, 0),
                        ZoneOffset.UTC
                ))).equatorialPos().dec());

    }

    @Test
    void atAngSize() {

        assertEquals(Angle.ofDMS(0, 32, 49),
                MoonModel.MOON.at(J2010.daysUntil(ZonedDateTime.of(
                        LocalDate.of(1979, 9, 1),
                        LocalTime.of(0, 0),
                        ZoneOffset.UTC
                )), new EclipticToEquatorialConversion(ZonedDateTime.of(
                        LocalDate.of(1979, 9, 1),
                        LocalTime.of(0, 0),
                        ZoneOffset.UTC
                ))).angularSize());

    }

    @Test
    void atPhase() {

        assertEquals("Lune (22.5%)",
                MoonModel.MOON.at(J2010.daysUntil(ZonedDateTime.of(
                        LocalDate.of(2003, 9, 1),
                        LocalTime.of(0, 0),
                        ZoneOffset.UTC
                )), new EclipticToEquatorialConversion(ZonedDateTime.of(
                        LocalDate.of(2003, 9, 1),
                        LocalTime.of(0, 0),
                        ZoneOffset.UTC
                ))).info());

    }


}
