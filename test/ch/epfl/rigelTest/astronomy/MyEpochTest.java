package ch.epfl.rigelTest.astronomy;

import ch.epfl.rigel.astronomy.Epoch;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MyEpochTest {

    private final static double EPSILON   = 0;


    @Test
    void daysUntil() {
        ZonedDateTime date = ZonedDateTime.of(1980,4,22,14,36,51,27
                , ZoneOffset.UTC);
        ZonedDateTime d = ZonedDateTime.of(
                LocalDate.of(2000, Month.JANUARY, 3),
                LocalTime.of(18, 0),
                ZoneOffset.UTC);
        assertEquals(2.25,Epoch.J2000.daysUntil(d));
        assertEquals(-7192.891076377315, Epoch.J2000.daysUntil(date),EPSILON);
    }

    @Test
    void julianCenturiesUntil() {

        ZonedDateTime d = ZonedDateTime.of(
                LocalDate.of(2000, Month.JANUARY, 3),
                LocalTime.of(18, 0),
                ZoneOffset.UTC);

        ZonedDateTime d2 = ZonedDateTime.of(LocalDate.of(1980,Month.APRIL,22),
                LocalTime.of(14,36,51,(int)67e7),ZoneOffset.UTC);

        assertEquals(2.25 /36525.0 ,Epoch.J2000.julianCenturiesUntil(d));
        //assertEquals(-0.196947296,Epoch.J2000.julianCenturiesUntil(d2));
    }
}