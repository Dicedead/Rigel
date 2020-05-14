package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.graphs.Cycle;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.DoubleFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

/**
 * Orbit prediction class for solar system's components.
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Orbit<T extends CelestialObject> extends Cycle<Supplier<T>> {

    private final static double HOURS_IN_DAY = 24d;
    private int currentModulo;
    private List<T> currentRepresentativeList;
    //Even though these 2 attributes are not final, to the outside world, Orbit is indeed immutable. This simply avoids
    //unnecessary re-computation.

    /**
     * Orbit constructor with calculation parameters
     *
     * @param initialTime       (ZonedDateTime) start time of orbit prediction
     * @param resolutionInHours (int) discrete simulation step in hours
     * @param maxDays           (long) orbit will be computed from initialTime to roughly initialTime + maxDays
     * @param model             (CelestialObjectModel<T>) model used to compute the orbit
     * @param conversion        (EclipticToEquatorialConversion)
     */
    public Orbit(ZonedDateTime initialTime, int resolutionInHours, long maxDays, CelestialObjectModel<T> model,
                 EclipticToEquatorialConversion conversion) {
        super(construct(Epoch.J2010.daysUntil(initialTime), resolutionInHours, maxDays, model, conversion));
    }

    /**
     * Get a list of representatives of the celestial object's orbit
     *
     * @param indexUntil (int) maximum number of representatives
     * @param stepModulo (int) get every 'stepModulo' representative
     * @return (List<T>) said list
     */
    public List<T> representatives(int indexUntil, int stepModulo) {
        if (currentRepresentativeList == null || stepModulo != currentModulo) {
            currentRepresentativeList = List.copyOf(flow(indexUntil)
                    .suchThat(supplier -> indexOf(supplier) % stepModulo == 0)
                    .image(Supplier::get).getRawData());
            currentModulo = stepModulo;
        }
        return currentRepresentativeList;
    }

    private static <T extends CelestialObject> List<Supplier<T>> construct(double initialDaysSince2010, int resolutionInHours,
            long maxDays, CelestialObjectModel<T> model, EclipticToEquatorialConversion eclToEqu) {
        DoubleFunction<Supplier<T>> f = daysSinceJ2010 -> (Supplier<T>) (() -> model.at(daysSinceJ2010, eclToEqu));

        return DoubleStream.iterate(initialDaysSince2010, daysSince2010 -> daysSince2010 > initialDaysSince2010 - maxDays,
                daysSince2010 -> daysSince2010 - resolutionInHours / HOURS_IN_DAY)
                .mapToObj(f)
                .collect(Collectors.toList());
    }
}
