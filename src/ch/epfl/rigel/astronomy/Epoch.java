package ch.epfl.rigel.astronomy;

import java.time.*;
import java.time.temporal.ChronoUnit;

/**
 * Enum providing two standard time references
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public enum Epoch {

    J2000(ZonedDateTime.of(LocalDateTime.of(2000, Month.JANUARY,1,12,0),ZoneOffset.UTC)),
    J2010(ZonedDateTime.of(LocalDate.of(2010, Month.JANUARY, 1).minusDays(1),LocalTime.of(0,0),
            ZoneOffset.UTC));


    private final ZonedDateTime epoch;

    private Epoch(ZonedDateTime date) {
        this.epoch = date;
    }

    /**
     * @param when the time we want to know the distance of
     * @return the distance in days from our epoch to when
     */
    public double daysUntil(ZonedDateTime when)
    {
        return ((double)epoch.until(when, ChronoUnit.MILLIS))/ (24 * 3600 * 1000);
    }

    /**
     * @param when the time we want to know the distance of
     * @return the distance in julian years from our epoch to when
     */
    public double julianCenturiesUntil(ZonedDateTime when)
    {
        return daysUntil(when)/36525.0;
    }
}
