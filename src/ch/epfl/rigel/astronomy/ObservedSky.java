package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.*;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.function.BiFunction;
import java.util.function.Function;
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

    private Map<Sun, CartesianCoordinates> sunMap;
    private Map<Moon, CartesianCoordinates> moonMap;
    private Map<Star, CartesianCoordinates> starMap;
    private Map<Planet, CartesianCoordinates> planetMap;

    private Map<CelestialObject, CartesianCoordinates> celestObjToCoordsMap;

    private final StereographicProjection stereoProj;
    private final EquatorialToHorizontalConversion eqToHor;
    private final EclipticToEquatorialConversion eclToEqu;
    private final double daysUntilJ2010;
    private final StarCatalogue catalogue;

    private final CartesianCoordinates sunPosition;
    private final CartesianCoordinates moonPosition;
    private final Moon moon;
    private final Sun sun;

    private static final Function<CartesianCoordinates, BiFunction<CartesianCoordinates, CartesianCoordinates, Integer>>
            CLOSEST_TO_C = c -> (a, b) -> Double.compare(euclideanDistSquared(a, c), euclideanDistSquared(b, c));

    private static final Function<CartesianCoordinates, BiFunction<CartesianCoordinates, Double, Boolean>>
            IS_IN_SQUARE_AROUND = point -> (cartes, distance) -> cartes.x() - point.x() <= distance &&
                                                                 cartes.y() - point.y() <= distance;

    /**
     * Constructs an ObservedSky at a given time, place, center of projection and a set list of stars,
     * also performing prerequisite computations and collections management ensuring greater efficiency
     * for methods below.
     *
     * @param date       (ZonedDateTime) observation date and time, with timezone
     * @param geoCoords  (GeographicCoordinates) point of observation
     * @param projection (StereographicProjection) center of projection
     * @param catalogue  (StarCatalogue) stars and their asterisms
     * @param execServ   (ExecutorService) executor service for mapObjectToPosition
     */
    public ObservedSky(ZonedDateTime date, GeographicCoordinates geoCoords,
                       StereographicProjection projection, StarCatalogue catalogue, ExecutorService execServ) {
        this.stereoProj = projection;
        this.eqToHor = new EquatorialToHorizontalConversion(date, geoCoords);
        this.eclToEqu = new EclipticToEquatorialConversion(date);
        this.daysUntilJ2010 = Epoch.J2010.daysUntil(date);
        this.catalogue = catalogue;

        try {
            execServ.submit (() -> {

                this.sunMap = mapSingleObjectToPosition(SunModel.SUN, this::applyModel);
                this.moonMap = mapSingleObjectToPosition(MoonModel.MOON, this::applyModel);
                this.planetMap = mapObjectsToPosition(PlanetModel.EXTRATERRESTRIAL, this::applyModel);
                this.starMap = mapObjectsToPosition(catalogue.stars());

                this.celestObjToCoordsMap = Collections.unmodifiableMap(Stream.of(starMap, planetMap, sunMap, moonMap)
                        .flatMap(l -> l.entrySet().stream())
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (u, v) -> v)));

            }).get();

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }



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
    public Optional<CelestialObject> objectClosestTo(CartesianCoordinates point, double maxDistance) {
        return celestObjToCoordsMap.entrySet().parallelStream()
                .filter(celest -> IS_IN_SQUARE_AROUND.apply(point).apply(celest.getValue(), maxDistance))
                .min((celestObj1, celestObj2) -> CLOSEST_TO_C.apply(point).apply(celestObj1.getValue(), celestObj2.getValue()))
                .filter(celestObj -> euclideanDistance(celestObj.getValue(), point) <= maxDistance)
                .map(Map.Entry::getKey);

        /* parallelStream proved to shorten the execution time upon testing (0.55 ms to 0.40 ms at step 11, may be
           reduced at step 12 with improved thread management) */
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
     * Map creator: Keys: data's elements after applying f on them (identical keys are merged)
     * Values: data's elements' CartesianCoordinates
     *
     * @param <T>  data's elements' type
     * @param <S>  f's output type -> the returned Map's keys' type
     * @param data (List<T>) input List to be applied f upon then put into keys
     * @param f    (Function<T,S>) function to apply on data
     * @return (Map <S, CartesianCoordinates>) map associating CelestialObjects with their CartesianCoordinates
     */
    public <T, S extends CelestialObject> Map<S, CartesianCoordinates> mapObjectsToPosition(List<T> data, Function<T, S> f) {
        return (data.parallelStream()
                .map(f)
                .collect(Collectors.toConcurrentMap(
                        Function.identity(),
                        celestObj -> eqToHor.andThen(stereoProj).apply(celestObj.equatorialPos()),
                        (u, v) -> v)));
    }

    /**
     * Map creator: Keys: data's elements after applying f on them (identical keys are merged)
     * Values: data's elements' CartesianCoordinates
     * A call to this method is a tad faster than {@code mapObjectsToPosition(List<S>, Function.identity())} as it has
     * one less intermediate stream instruction
     *
     * @param <S>  f's output type -> the returned Map's keys' type
     * @param data (List<T>) input List to be applied f upon then put into keys
     * @return (Map <S, CartesianCoordinates>) map associating CelestialObjects with their CartesianCoordinates
     */
    public <S extends CelestialObject> Map<S, CartesianCoordinates> mapObjectsToPosition(List<S> data) {
        return (data.parallelStream()
                .collect(Collectors.toConcurrentMap(
                        Function.identity(),
                        celestObj -> eqToHor.andThen(stereoProj).apply(celestObj.equatorialPos()),
                        (u, v) -> v)));
    }

    /**
     * Map creator: Key: item after applying f on them (identical keys are merged)
     * Value: item's CartesianCoordinates
     *
     * @param <T>  item's type
     * @param <S>  f's output type -> the returned Map's key type
     * @param item (T) input List to be applied f upon then put into keys
     * @param f    (Function<T,S>) function to apply on data
     * @return (Map <S, CartesianCoordinates>) map associating CelestialObject item with its CartesianCoordinates
     */
    public <T, S extends CelestialObject> Map<S, CartesianCoordinates> mapSingleObjectToPosition(T item, Function<T, S> f) {
        var temp = f.apply(item);
        return Map.of(temp, eqToHor.andThen(stereoProj).apply(temp.equatorialPos()));
    }

    /**
     * Applies at() method of given CelestialObjectModel and returns corresponding CelestialObject
     *
     * @param <K>                  CelestialObject's type to be computed and returned
     * @param celestialObjectModel (CelestialObjectModel<K>) corresponding Model
     * @return (K extends CelestialObject) parametrized CelestialObject
     * @see CelestialObjectModel#at(double, EclipticToEquatorialConversion)
     */
    private <K extends CelestialObject> K applyModel(CelestialObjectModel<K> celestialObjectModel) {
        return celestialObjectModel.at(daysUntilJ2010, eclToEqu);
    }
}
