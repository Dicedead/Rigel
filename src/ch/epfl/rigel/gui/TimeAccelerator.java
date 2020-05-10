package ch.epfl.rigel.gui;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Functional interface for time variation simulating
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
@FunctionalInterface
public interface TimeAccelerator {

    /**
     * Computes a new simulated time after transformation of an initial simulated time and
     * of an elapsed duration in nanoseconds between the real times separating the initial and returned
     * separated times
     *
     * @param initialTime (ZonedDateTime) initial time with timezone information
     * @param nanosElapsed (long) real time elapsed between initialTime and returned time
     * @return (ZonedDateTime) adjusted simulated time
     */
    ZonedDateTime adjust(ZonedDateTime initialTime, long nanosElapsed);

    /**
     * Creates a continuous time accelerator:
     * (T0, n) -> T0 + factor * n
     *
     * taking a ZonedDateTime T0 and a long n as inputs with n corresponding to the real time elapsed between the
     * returned time and T0 in nanoseconds
     * side-effect: zone information is lost because of daylight saving time changes
     *
     * @param factor (int) acceleration factor
     * @return TimeAccelerator
     */
    static TimeAccelerator continuous(int factor) {
        return (time, nanos) -> time.withFixedOffsetZone().plusNanos(factor * nanos);
        /* withFixedOffsetZone() will simply return time itself (ie not creating a new object) if current time operation
         * did not involve daylight saving, unlike its documentation suggests */
    }

    /**
     * Creates a discrete time accelerator:
     * (T0, n) -> T0 + step * floor(freq*nanos)
     *
     * taking a ZonedDateTime T0 and a long n as inputs with n corresponding to the real time elapsed between the
     * returned time and T0 in nanoseconds
     * side-effect: zone information is lost because of daylight saving time changes
     *
     * @param freq (int) rescales how many discrete steps are taken per real time in Hertz
     * @param step (Duration) advancement step the simulation
     * @return TimeAccelerator
     */
    static TimeAccelerator discrete(int freq, Duration step) {
        return (time, nanos) -> time.withFixedOffsetZone().plus(step.multipliedBy((long) Math.floor(nanos * freq * 1e-9)));
    }
}
