package ch.epfl.rigel.math.graphs;

import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.CelestialObjectModel;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;

import java.util.List;
import java.util.function.DoubleFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class StarPath<T extends CelestialObject> extends Cycle<Supplier<T>> {

    StarPath(final int time, final int resolution, final CelestialObjectModel<T> function, final EclipticToEquatorialConversion conversion)
    {
        super(construct(time, resolution, function, conversion));
    }

    private static<T> List<Supplier<T>> construct (final int time, final int resolution, final CelestialObjectModel<T> function, final EclipticToEquatorialConversion conversion)
    {
        DoubleFunction<Supplier<T>> f = i -> (Supplier<T>)(() -> function.at(i, conversion));
        return DoubleStream.iterate(time, t -> f.apply(t).equals(f.apply(time)), n -> n + resolution).mapToObj(f).collect(Collectors.toList());
    }

    public Path<T> representatives(final int step)
    {
        return new Path<>(flow(p -> at(p.cardinality() + step) , at(0)).image(Supplier::get));
    }
}
