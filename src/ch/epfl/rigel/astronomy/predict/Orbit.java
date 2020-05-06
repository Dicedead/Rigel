package ch.epfl.rigel.astronomy.predict;

import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.CelestialObjectModel;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.graphs.Cycle;
import ch.epfl.rigel.math.graphs.Path;

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

    public Orbit(int time, int resolution, CelestialObjectModel<T> function, EclipticToEquatorialConversion conversion)
    {
        super(construct(time, resolution, function, conversion));
    }

    private static<T> List<Supplier<T>> construct (int time, int resolution, CelestialObjectModel<T> function,
                                                   EclipticToEquatorialConversion conversion)
    {
        DoubleFunction<Supplier<T>> f = i -> (Supplier<T>)(() -> function.at(i, conversion));
        return DoubleStream.iterate(time, t -> f.apply(t).equals(f.apply(time)), n -> n + resolution)
                .mapToObj(f).collect(Collectors.toList());
    }

    public Path<T> representatives(int step)
    {
        return new Path<>(flow(p -> at(p.cardinality() + step), at(0)).image(Supplier::get));
    }
}
