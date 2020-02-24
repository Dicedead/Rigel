package ch.epfl.rigel.astronomy;

import java.time.*;
import java.time.temporal.ChronoUnit;

public enum Epoch {

    J2000(LocalDate.of(2000, Month.JANUARY, 1)),
    J2010(LocalDate.of(2010, Month.JANUARY, 1).minusDays(1));

    private ZonedDateTime epoch;
    private Epoch(LocalDate date) {
        epoch = date.atStartOfDay(ZoneOffset.UTC);
    }

    public double daysUntil(ZonedDateTime when)
    {
        return (double)(epoch.until(when, ChronoUnit.MILLIS))/ (24 * 3600 * 1000);
    }

    public double julianCenturiesUntil(ZonedDateTime when)
    {
        return daysUntil(when)/36525;
    }
}
