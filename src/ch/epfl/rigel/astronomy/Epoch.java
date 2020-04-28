package ch.epfl.rigel.astronomy;

import java.time.*;

/**
 * Enum providing two standard time references
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public enum Epoch {
    J2000(ZonedDateTime.of(LocalDate.of(2000, Month.JANUARY, 1), LocalTime.NOON, ZoneOffset.UTC)),
    J2010(ZonedDateTime.of(LocalDate.of(2010, Month.JANUARY, 1).minusDays(1), LocalTime.MIDNIGHT,
            ZoneOffset.UTC));

    private static final double COEFF_TO_DAYS = 1d / (24 * 3600 * 1000);
    private static final double COEFF_JULIAN = 1d / 36525;
    private static final long MILLIS_IN_DAY = 86_400_000L;
    private static final long NANOS_IN_DAY = 86_400_000_000_000L;

    private final ZonedDateTime epoch;

    Epoch(ZonedDateTime date) {
        this.epoch = date;
    }

    /**
     * @param when (ZonedDateTime) the time we want to know the distance of
     * @return (double) the distance in days from our epoch to when
     */
    public double daysUntil(ZonedDateTime when) {
        return until(epoch, when) * COEFF_TO_DAYS;
    }

    /**
     * @param when (ZonedDateTime) the time we want to know the distance of
     * @return (double) the distance in julian years from our epoch to when
     */
    public double julianCenturiesUntil(final ZonedDateTime when)
    {
        return daysUntil(when) * COEFF_JULIAN;
    }

    /**
     * Custom made until function from Java sources, this implementation is ~3x faster than the classic Java function
     * This function will be public in part 7 as the public interface may be changed from now on
     *
     * @param now  (ZonedDateTime) the time we want to know the distance from
     * @param when (ZonedDateTime) the time we want to know the distance of
     * @return (double) the distance in milliseconds from now to when
     */
    static public double until (ZonedDateTime now, ZonedDateTime when)
    {
        OffsetDateTime you = now.withZoneSameInstant(when.getZone()).toOffsetDateTime();
        LocalDateTime end = LocalDateTime.from(when);
        long amount = end.toLocalDate().toEpochDay() - you.toLocalDate().toEpochDay();

        return (amount-Long.signum(amount))*MILLIS_IN_DAY+(end.toLocalTime().toNanoOfDay() - you.toLocalTime().toNanoOfDay()
                + Long.signum(amount)*NANOS_IN_DAY)/1e6;
    }
}
