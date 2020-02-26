package ch.epfl.rigelTest.astronomy;

import ch.epfl.rigel.astronomy.Epoch;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpochTest {

    private final static double EPSILON = 1e-2;

    @Test
    void daysUntil() {
        ZonedDateTime date = ZonedDateTime.of(1980,4,22,14,36,51,27
                , ZoneOffset.UTC);
        ZonedDateTime d = ZonedDateTime.of(
                LocalDate.of(2000, Month.JANUARY, 3),
                LocalTime.of(18, 0),
                ZoneOffset.UTC);
        assertEquals(2.25,Epoch.J2000.daysUntil(d));
        assertEquals(-7193.5, Epoch.J2000.daysUntil(date),EPSILON);
    }

    @Test
    void julianCenturiesUntil() {

        ZonedDateTime d = ZonedDateTime.of(
                LocalDate.of(2000, Month.JANUARY, 3),
                LocalTime.of(18, 0),
                ZoneOffset.UTC);

        assertEquals(2.25 /36525.0 ,Epoch.J2000.julianCenturiesUntil(d));
    }
}