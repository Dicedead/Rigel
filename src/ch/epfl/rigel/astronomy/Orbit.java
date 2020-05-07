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
 * Orbit prediction class
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Orbit<T extends CelestialObject> extends Cycle<Supplier<T>> {

    private final static double HOURS_IN_DAY = 24d;
    private int currentModulo;
    private List<T> currentRepresentativeList;

    public Orbit(ZonedDateTime initialTime, int resolutionInHours, int maxDays, CelestialObjectModel<T> function,
                 EclipticToEquatorialConversion conversion) {
        super(construct(Epoch.J2010.daysUntil(initialTime), resolutionInHours, maxDays, function, conversion));
    }

    private static <T extends CelestialObject> List<Supplier<T>> construct(double initialDaysSince2010, int resolutionInHours,
            int maxDays, CelestialObjectModel<T> model, EclipticToEquatorialConversion eclToEqu) {
        DoubleFunction<Supplier<T>> f = daysSinceJ2010 -> (Supplier<T>) (() -> model.at(daysSinceJ2010, eclToEqu));

        return DoubleStream.iterate(initialDaysSince2010, daysSince2010 -> daysSince2010 < initialDaysSince2010 + maxDays,
                daysSince2010 -> daysSince2010 + resolutionInHours/HOURS_IN_DAY)
                .mapToObj(f)
                .collect(Collectors.toList());
    }

    public List<T> representatives(int indexUntil, int stepModulo) {
        if (currentRepresentativeList == null || stepModulo != currentModulo) {
            currentRepresentativeList = List.copyOf(flow(indexUntil)
                    .suchThat(supplier -> indexOf(supplier) % stepModulo == 0)
                    .image(Supplier::get).getRawData());
            currentModulo = stepModulo;
        }
        return currentRepresentativeList;
    }
}
