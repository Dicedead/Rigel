package ch.epfl.rigel.gui;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.function.BiFunction;

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
    ZonedDateTime adjust(final ZonedDateTime initialTime, final long nanosElapsed);

    /**
     * Creates a continuous time accelerator:
     * (T0, n) -> T0 + factor * n
     *
     * taking a ZonedDateTime T0 and a long n as inputs with n corresponding to the real time elapsed between the
     * returned time and T0 in nanoseconds
     *
     * @param factor (int) acceleration factor
     * @return TimeAccelerator
     */
    static TimeAccelerator continuous(final int factor) {
        return (time, nanos) -> time.plusNanos(factor * nanos);
    }

    /**
     * Creates a discrete time accelerator:
     * (T0, n) -> T0 + step * floor(freq*nanos)
     *
     * taking a ZonedDateTime T0 and a long n as inputs with n corresponding to the real time elapsed between the
     * returned time and T0 in nanoseconds
     *
     * @param freq (int) rescales how many discrete steps are taken per real time unit
     * @param step (Duration) advancement step the simulation
     * @return TimeAccelerator
     */
    static TimeAccelerator discrete(final int freq, final Duration step) {
        return (time, nanos) -> time.plusNanos(step.toNanos() * (long) Math.floor(freq * nanos));
    }

}
