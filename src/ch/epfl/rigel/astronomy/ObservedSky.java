package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.*;
import ch.epfl.rigel.logging.RigelLogger;
import com.sun.tools.javac.Main;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Pooling all the models and corresponding celestial objects, creating a representation of the Observed Sky at a
 * given place and time.
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class ObservedSky {

    private final Map<Sun, CartesianCoordinates> sunMap;
    private final Map<Moon, CartesianCoordinates> moonMap;
    private final Map<Star, CartesianCoordinates> starMap;
    private final Map<Planet, CartesianCoordinates> planetMap;

    private final Map<CelestialObject, CartesianCoordinates> celestObjToCoordsMap;

    private final StereographicProjection stereoProj;
    private final EquatorialToHorizontalConversion eqToHor;
    private final EclipticToEquatorialConversion eclToEqu;
    private final double daysUntilJ2010;
    private final StarCatalogue catalogue;

    private static final List<String> PLANET_NAMES = List.of("Mercure", "Vénus", "Mars", "Jupiter", "Saturne", "Uranus", "Neptune");
    //Sadly, PlanetModel does not offer any way to compare instances of Planet - a getter for such a list would have
    //done but pre-step 7 classes' API need not to be modified.

    private static final Function<CartesianCoordinates, BiFunction<CartesianCoordinates, CartesianCoordinates, Integer>>
            CLOSEST_TO_C = c -> (a, b) -> Double.compare(euclideanDistSquared(a, c), euclideanDistSquared(b, c));
    /*This Function assigns a target point c in Cartesian Coordinates, then returns a BiFunction of this
     * target point c and two CartesianCoordinates, comparing which one's closest to c.*/

    /**
     * Constructs an ObservedSky at a given time, place, center of projection and a set list of stars,
     * also performing prerequisite computations and collections management ensuring greater efficiency
     * for methods below.
     *
     * @param date       (ZonedDateTime) observation date and time, with timezone
     * @param geoCoords  (GeographicCoordinates) point of observation
     * @param projection (StereographicProjection) center of projection
     * @param catalogue  (StarCatalogue) stars and their asterisms
     */
    public ObservedSky(final ZonedDateTime date, final GeographicCoordinates geoCoords,
                       final StereographicProjection projection, final StarCatalogue catalogue) {
        RigelLogger.getAstronomyLogger().entering("ObservedSky", "ObservedSky");
        RigelLogger.getAstronomyLogger().info("Constructing sky at date " + date + " and position " + geoCoords.toString());
        this.stereoProj = projection;
        this.eqToHor = new EquatorialToHorizontalConversion(date, geoCoords);
        this.eclToEqu = new EclipticToEquatorialConversion(date);
        this.daysUntilJ2010 = Epoch.J2010.daysUntil(date);
        this.catalogue = catalogue;

        this.sunMap = Collections.unmodifiableMap(mapObjectToPosition(List.of(SunModel.SUN), this::applyModel));
        this.moonMap = Collections.unmodifiableMap(mapObjectToPosition(List.of(MoonModel.MOON), this::applyModel));
        this.planetMap = Collections.unmodifiableMap(transform(Arrays.stream(PlanetModel.values()).filter(i -> i.ordinal() != 2)
                .collect(Collectors.toList()), this::applyModel, i -> PLANET_NAMES.indexOf(i.name())));
        this.starMap = Collections.unmodifiableMap(transform(catalogue.stars(), Function.identity(), catalogue.starIndexMap()::get));

        this.celestObjToCoordsMap = Stream.of(starMap, planetMap, sunMap, moonMap)
                .flatMap(l -> l.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (u, v) -> v, HashMap::new));

        RigelLogger.getAstronomyLogger().info("Sky finished initialisation");
        RigelLogger.getAstronomyLogger().exiting("ObservedSky", "ObservedSky");

    }

    /**
     * Computes and returns a cell containing the celestial object that's closest to the given point if it's within a
     * distance <= to the given max distance, an empty one otherwise
     *
     * @param point       (CartesianCoordinates) point to approach
     * @param maxDistance (double) max allowed distance
     * @return (Optional < CelestialObject >) Optional.empty if no objects within maxDistance radius, otherwise, the
     * closest CelestialObject wrapped in an Optional cell.
     */
    public Optional<CelestialObject> objectClosestTo(final CartesianCoordinates point, final double maxDistance) {
        return celestObjToCoordsMap.keySet().parallelStream().min((celestObj1, celestObj2) -> CLOSEST_TO_C.apply(point)
                .apply(celestObjToCoordsMap.get(celestObj1), celestObjToCoordsMap.get(celestObj2))) //(*)
                .filter(celestObj -> Math.sqrt(euclideanDistSquared(celestObjToCoordsMap.get(celestObj), point)) <= maxDistance); //(**)
        /*
        Constructing celestObjToCoordsMap beforehand allows this method to run in linear time - all it does is search
        for the "minimum" of the CartesianCoordinates, comparing their distances to point at line (*), then check if
        its distance to point is <= maxDistance at (**). Finding the minimum of each map then comparing them all proved
        to be a lot slower (5 times). parallelStream proved to greatly shorten the execution time on testing
        (at least 33%), making the map worthwhile when compared to 2 identically ordered lists, especially after
        the initial expensive threads' initialisation.
         */
    }

    /**
     * @return (Map<Star, CartesianCoordinates>) the stars associated to their Cartesian Coordinates
     */
    public Map<Star, CartesianCoordinates> starsMap() {
        return starMap;
    }

    /**
     * @return (List <Star>) list of stars in current observed sky
     */
    public List<Star> stars() {
        return catalogue.stars();
    }

    /**
     * @return (Set<Asterism>) set of asterisms in current observed sky
     */
    public Set<Asterism> asterisms() {
        return catalogue.asterisms();
    }

    /**
     * @see StarCatalogue#asterismIndices(Asterism)
     */
    public List<Integer> asterismIndices(Asterism asterism) {
        return catalogue.asterismIndices(asterism);
    }

    /**
     * @return (Map<Planet, CartesianCoordinates>) the planets associated to their Cartesian Coordinates
     */
    public Map<Planet, CartesianCoordinates> planetsMap() {
        return planetMap;
    }

    /**
     * @return (Map<Sun, CartesianCoordinates>) the Sun associated to its Cartesian Coordinates
     */
    public Map<Sun, CartesianCoordinates> sunMap() {
        return sunMap;
    }

    /**
     * @return (Map<Moon, CartesianCoordinates>) the Moon associated to its Cartesian Coordinates
     */
    public Map<Moon, CartesianCoordinates> moonMap() {
        return moonMap;
    }

    /**
     * Applies at() method of given CelestialObjectModel and returns corresponding CelestialObject
     *
     * @param <K>                  CelestialObject's type to be computed and returned
     * @param celestialObjectModel (CelestialObjectModel<K>) corresponding Model
     * @return (K extends CelestialObject) parametrized CelestialObject
     * @see CelestialObjectModel<K>.at() documentation
     */
    private <K extends CelestialObject> K applyModel(final CelestialObjectModel<K> celestialObjectModel) {
        return celestialObjectModel.at(daysUntilJ2010, eclToEqu);
    }

    /**
     * Map creator: Keys: data's elements after applying f on them (identical keys are merged)
     * Values: data's element CartesianCoordinates
     *
     * @param <T>  data's elements' type
     * @param <S>  f's output type -> the returned Map's keys' type
     * @param data (List<T>) input List to be applied f upon then put into keys
     * @param f    (Function<T,S>) function to apply on data
     * @return (Map < S, CartesianCoordinates >) map associating CelestialObjects with their CartesianCoordinates
     */
    private <T, S extends CelestialObject> Map<S, CartesianCoordinates> mapObjectToPosition(final List<T> data, final Function<T, S> f) {
        return data.stream().map(f).collect(Collectors.toMap(Function.identity(),
                celestObj -> stereoProj.apply(eqToHor.apply(celestObj.equatorialPos())), (u, v) -> v, HashMap::new));
        //In our uses, f is either the identity -when data already contains CelestialObjects of type S-
        //or applyModel via method reference    -when data contains CelestialObjectModels<S>.
    }

    /**
     * Helper creating a TreeMap with cartesian coordinates from a list of Celestial Objects
     *
     * @param inList List to draw the celestial object from
     * @param func   Function to apply in mapObjectToPosition
     * @param comp   Comparator to keep order
     * @param <T>    Specialised Celestial object Model
     * @param <S>    Specialised Celestial object
     * @return Map containing the position of each Celestial object
     */
    private <T, S extends CelestialObject> Map<S, CartesianCoordinates>
    transform(final List<T> inList, final Function<T, S> func, final ToIntFunction<S> comp) {
        return mapObjectToPosition(inList, func).entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                Map.Entry::getValue, (o1, o2) -> o1, () -> new TreeMap<>(Comparator.comparingInt(comp))));
    }

    /**
     * Computes the square of the euclidean norm of the vector joining coord1 to coord2
     *
     * @param coord1 (CartesianCoordinates)
     * @param coord2 (CartesianCoordinates)
     * @return (double)
     */
    private static double euclideanDistSquared(CartesianCoordinates coord1, CartesianCoordinates coord2) {
        return (coord1.x() - coord2.x()) * (coord1.x() - coord2.x()) + (coord1.y() - coord2.y()) * (coord1.y() - coord2.y());
        //Math.pow(n,2) is just a tad slower than n*n for squaring, so was Math.hyp compared to this method
    }
}
