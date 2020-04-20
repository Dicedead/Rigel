package ch.epfl.rigel.gui;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.function.BiFunction;

/**
 * Enum predefining time simulators (named: timed accelerators) and giving them names
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public enum NamedTimeAccelerator {

    TIMES_1("1x", TimeAccelerator.continuous(1)),
    TIMES_30("30x", TimeAccelerator.continuous(30)),
    TIMES_300("300x", TimeAccelerator.continuous(300)),
    TIMES_3000("3000x", TimeAccelerator.continuous(3000)),
    DAY("jour", TimeAccelerator.discrete(60, Duration.ofHours(24))),
    SIDEREAL_DAY("jour sidéral", TimeAccelerator.discrete(60, Duration.parse("23:56:04")));

    private final String name;
    private final BiFunction<ZonedDateTime, Long, ZonedDateTime> accelerator;

    NamedTimeAccelerator(final String name, final BiFunction<ZonedDateTime, Long, ZonedDateTime> accelerator) {
        this.name = name;
        this.accelerator = accelerator;
    }

    /**
     * @return (String) this accelerator's identifying name
     */
    public String getName() {
        return name;
    }

    /**
     * @return (BiFunction<ZonedDateTime, Long, ZonedDateTime>) this accelerator's simulation function
     */
    public BiFunction<ZonedDateTime, Long, ZonedDateTime> getAccelerator() {
        return accelerator;
    }

    /**
     * @see Object#toString()
     *
     * @return (String) this accelerator's name
     */
    @Override
    public String toString() {
        return getName();
    }
}
