package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.*;

import java.time.ZonedDateTime;
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
import java.util.stream.Stream;

import static ch.epfl.rigel.coordinates.PlanarTransformation.euclideanDistSquared;
import static ch.epfl.rigel.coordinates.PlanarTransformation.euclideanDistance;

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

    private final CartesianCoordinates sunPosition;
    private final CartesianCoordinates moonPosition;
    private final Moon moon;
    private final Sun sun;

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
        this.stereoProj = projection;
        this.eqToHor = new EquatorialToHorizontalConversion(date, geoCoords);
        this.eclToEqu = new EclipticToEquatorialConversion(date);
        this.daysUntilJ2010 = Epoch.J2010.daysUntil(date);
        this.catalogue = catalogue;

        this.sunMap = mapObjectToPosition(List.of(SunModel.SUN), this::applyModel);
        this.moonMap = mapObjectToPosition(List.of(MoonModel.MOON), this::applyModel);
        this.planetMap = mapObjectToPosition(Arrays.stream(PlanetModel.values())
                         .filter(i -> i.ordinal() != 2)
                         .collect(Collectors.toList()), this::applyModel);
        this.starMap = mapObjectToPosition(catalogue.stars(), Function.identity());

        this.celestObjToCoordsMap = Collections.unmodifiableMap(Stream.of(starMap, planetMap, sunMap, moonMap)
                .flatMap(l -> l.entrySet().stream())
                .parallel()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (u, v) -> v, HashMap::new)));

        this.sunPosition = (CartesianCoordinates) sunMap.values().toArray()[0];
        this.sun = (Sun) sunMap.keySet().toArray()[0];
        this.moonPosition = (CartesianCoordinates) moonMap.values().toArray()[0];
        this.moon = (Moon) moonMap.keySet().toArray()[0];

    }

    /**
     * Computes and returns a cell containing the celestial object that's closest to the given point if it's within a
     * distance <= to the given max distance, an empty one otherwise
     *
     * @param point       (CartesianCoordinates) point to approach
     * @param maxDistance (double) max allowed distance
     * @return (Optional <CelestialObject>) Optional.empty if no object within maxDistance radius, otherwise, the
     * closest CelestialObject wrapped in an Optional cell.
     */
    public Optional<CelestialObject> objectClosestTo(final CartesianCoordinates point, final double maxDistance) {
        return celestObjToCoordsMap.keySet().parallelStream().min((celestObj1, celestObj2) -> CLOSEST_TO_C.apply(point)
                .apply(celestObjToCoordsMap.get(celestObj1), celestObjToCoordsMap.get(celestObj2))) //(*)
                .filter(celestObj -> euclideanDistance(celestObjToCoordsMap.get(celestObj), point) <= maxDistance); //(**)
        /*
        Constructing celestObjToCoordsMap beforehand allows this method to run in linear time, and although it causes
        spatial complexity, finding the minimum of each map then comparing them all proved to be a lot slower (5 times).
        (*) is a linear scan, comparing keySet's elements by their distance (squared) to the target, and (**) checks
        whether the closest object found (ie the 'minimum') is within a maxDistance radius of the target.
        Applying a first approximate filter to the celestial objects before running the linear scan has also proven to
        be slower, for a Stream.

        parallelStream proved to greatly shorten the execution time on testing (at least 33%),
        making the map worthwhile when compared to 2 identically ordered lists, especially after
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
     * @return (Sun) Sun object in current observed sky
     */
    public Sun sun() {
        return sun;
    }

    /**
     * @return (CartesianCoordinates) Sun's CartesianCoordinates
     */
    public CartesianCoordinates sunPosition() {
        return sunPosition;
    }

    /**
     * @return (Map<Moon, CartesianCoordinates>) the Moon associated to its Cartesian Coordinates
     */
    public Map<Moon, CartesianCoordinates> moonMap() {
        return moonMap;
    }

    /**
     * @return (Moon) Moon object in current observed sky
     */
    public Moon moon() {
        return moon;
    }

    /**
     * @return (CartesianCoordinates) Moon's CartesianCoordinates
     */
    public CartesianCoordinates moonPosition() {
        return moonPosition;
    }

    /**
     * @return (Map<CelestialObject, CartesianCoordinates>) immutable map of all celestial objects along with
     *          their cartesian position
     */
    public Map<CelestialObject, CartesianCoordinates> celestialObjMap() {
        return celestObjToCoordsMap;
    }

    /**
     * Applies at() method of given CelestialObjectModel and returns corresponding CelestialObject
     *
     * @param <K>                  CelestialObject's type to be computed and returned
     * @param celestialObjectModel (CelestialObjectModel<K>) corresponding Model
     * @return (K extends CelestialObject) parametrized CelestialObject
     * @see CelestialObjectModel#at(double, EclipticToEquatorialConversion)
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
     * @return (Map <S, CartesianCoordinates>) map associating CelestialObjects with their CartesianCoordinates
     */
    private <T, S extends CelestialObject> Map<S, CartesianCoordinates> mapObjectToPosition(final List<T> data, final Function<T, S> f) {
        return Collections.unmodifiableMap(data.stream().map(f).collect(Collectors.toMap(Function.identity(),
                celestObj -> stereoProj.apply(eqToHor.apply(celestObj.equatorialPos())), (u, v) -> v, HashMap::new)));
        //In our uses, f is either the identity -when data already contains CelestialObjects of type S-
        //or applyModel via method reference    -when data contains CelestialObjectModels<S>.
    }
}
