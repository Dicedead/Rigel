package ch.epfl.rigel.gui.bonus;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.math.sets.abtract.AbstractMathSet;
import ch.epfl.rigel.math.sets.concrete.IndexedSet;
import ch.epfl.rigel.math.sets.concrete.MathSet;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ch.epfl.rigel.math.sets.concrete.MathSet.of;

public class StarSearcher extends Searcher<CelestialObject> {

    static private final int DEFAULT_CACHE_SIZE = 10;

    StarSearcher(StarCatalogue s, Filters f)
    {
        super(new IndexedSet<>(s.stars().stream()
                .collect(Collectors.toMap(CelestialObject::name, Function.identity()))),
                DEFAULT_CACHE_SIZE,
                t -> f.classList().contains(t.getClass()));
    }




    AbstractMathSet<CartesianCoordinates> obtainPositions(ObservedSky sky)
    {
        return getResults().getValue().image(sky.celestialObjMap()::get);
    }

    public enum Filters {

        SOLAR_SYSTEM(Moon.class, Sun.class, Planet.class),
        STARS(Star.class),
        ALL(Moon.class, Sun.class, Planet.class, Star.class);

        private final MathSet<Class<? extends CelestialObject>> classList;

        @SafeVarargs
        Filters(Class<? extends CelestialObject>... classList) {
            this.classList = of(classList);
        }
        public MathSet<Class<? extends CelestialObject>> classList() {
            return classList;
        }
    }


}