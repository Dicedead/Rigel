package ch.epfl.rigelTest.gui;

import ch.epfl.rigel.gui.TimeAccelerator;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public class TimeSimulationTest {

    private static ZonedDateTime zdtApril20 = ZonedDateTime.of(LocalDate.of(2020, Month.APRIL,20),
            LocalTime.of(21,0),
            ZoneOffset.UTC);

    @Test
    void continuousAccelerators() {
        assertEquals(ZonedDateTime.of(
                LocalDate.of(2020, Month.APRIL, 20),
                LocalTime.of(21,11,42),
                ZoneOffset.UTC
        ), TimeAccelerator.continuous(300).adjust(zdtApril20, Duration.ofMillis(2340).toNanos()));
    }

    @Test
    void discreteAccelerators() {
        assertEquals(ZonedDateTime.of(
                LocalDate.of(2020, Month.MAY, 13),
                LocalTime.of(19,29,32),
                ZoneOffset.UTC
                ),
                TimeAccelerator.discrete(10, Duration.parse("PT23H56M04S")).adjust(
                        zdtApril20, Duration.ofMillis(2340).toNanos()
                ));
    }
}
