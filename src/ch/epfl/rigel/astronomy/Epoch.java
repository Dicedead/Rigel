package ch.epfl.rigel.astronomy;

import java.time.*;
import java.time.temporal.ChronoUnit;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 *
 */

public enum Epoch {

    J2000(LocalDate.of(2000, Month.JANUARY, 1)),
    J2010(LocalDate.of(2010, Month.JANUARY, 1).minusDays(1));


    private ZonedDateTime epoch;

    private Epoch(LocalDate date) {
        epoch = date.atStartOfDay(ZoneOffset.UTC);
    }

    /**
     *
     * @param when the time we want to know the distance of
     * @return the distance in days from our epch to when
     */
    public double daysUntil(ZonedDateTime when)
    {
        return (double)(epoch.until(when, ChronoUnit.MILLIS))/ (24 * 3600 * 1000);
    }

    /**
     *
     * @param when the time we want to know the distance of
     * @return the distance in julian years from our epch to when
     */
    public double julianCenturiesUntil(ZonedDateTime when)
    {
        return daysUntil(when)/36525;
    }
}
