package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.*;
import com.sun.source.tree.Tree;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import static java.lang.Math.pow;

public final class ObservedSky {

    private final Map<Sun, CartesianCoordinates> sunMap;
    private final Map<Moon, CartesianCoordinates> moonMap;

    private final Map<CelestialObject, CartesianCoordinates> coordsToCelObjectsMap = new HashMap<>();

    private final StarCatalogue catalogue;
    private final double[] starsPositions;
    private final List<Planet> planetList;
    private final double[] planetPositions;

    private final StereographicProjection positionToObserve;
    private final EquatorialToHorizontalConversion eqToHor;
    private final EclipticToEquatorialConversion eclToEqu;
    private final double daysUntilJ2010;

    private static final List<String> PLANET_NAMES = List.of("Mercure","Vénus","Mars","Jupiter","Saturne","Uranus","Neptune");
    //Sadly, PlanetModel does not offer any way to compare instances of Planet - a getter for such a list would have
    //done but pre-step 7 classes need not to be modified.

    static private final Function<CartesianCoordinates, BiFunction<CartesianCoordinates, CartesianCoordinates, Integer>>
            CLOSEST_TO_C = c -> (a, b) -> Double.compare((pow(a.x() - c.x(), 2) + pow(a.y() - c.y(), 2)), (pow(b.x() - c.x(), 2)
            + pow(b.y() - c.y(), 2)));


    public ObservedSky(final ZonedDateTime date, final GeographicCoordinates geoCoords,
                       final StereographicProjection projection, final StarCatalogue catalogue) {

        this.positionToObserve = projection;
        this.eqToHor = new EquatorialToHorizontalConversion(date, geoCoords);
        this.eclToEqu = new EclipticToEquatorialConversion(date);
        this.daysUntilJ2010 = Epoch.J2010.daysUntil(date);
        this.catalogue = catalogue; //Kept for its immutable list of stars

        final List<PlanetModel> extraTerrPlanets = new ArrayList<>(Arrays.asList(PlanetModel.values()));
        extraTerrPlanets.remove(2); //removing the Earth

        final Map<Star, CartesianCoordinates> starMap = new TreeMap<>(Comparator.comparingInt((Star i) ->
                catalogue.stars().indexOf(i)));
        starMap.putAll(Map.copyOf(calculatePosition(catalogue.stars(), Function.identity())));

        final Map<Planet, CartesianCoordinates> planetMap = new TreeMap<>(Comparator.comparingInt((Planet i) ->
                PLANET_NAMES.indexOf(i.name())));
        planetMap.putAll(Map.copyOf(calculatePosition(List.copyOf(extraTerrPlanets), this::getAt)));

        sunMap = Map.copyOf(calculatePosition(List.of(SunModel.SUN), this::getAt));
        moonMap = Map.copyOf(calculatePosition(List.of(MoonModel.MOON), this::getAt));

        planetList = List.copyOf(planetMap.keySet());
        planetPositions = positionsToArray(planetMap);
        starsPositions = positionsToArray(starMap);

        List.of(starMap, planetMap, sunMap, moonMap).forEach(coordsToCelObjectsMap::putAll);
    }

    public Optional<CelestialObject> objectClosestTo(final CartesianCoordinates point, final double maxDistance) {
        return coordsToCelObjectsMap.keySet().stream().min((l, j) -> CLOSEST_TO_C.apply(point)
                .apply(coordsToCelObjectsMap.get(l), coordsToCelObjectsMap.get(j)))
                .filter(i -> CLOSEST_TO_C.apply(point).apply(coordsToCelObjectsMap.get(i), point) <= maxDistance);
    }

    public List<Star> stars() {
        return catalogue.stars();
    }

    public double[] starsPosition() {
        return starsPositions.clone();
    }

    public List<Planet> planets() {
        return planetList;
    }

    public double[] planetPosition() {
        return planetPositions.clone();
    }

    public Sun sun() {
        return (Sun) sunMap.keySet().toArray()[0]; //note that the keySet's size is only 1, hence toArray costs 1 flop,
    }                                              //and one more to get

    public CartesianCoordinates sunPosition() {
        return (CartesianCoordinates) sunMap.values().toArray()[0];
    }

    public Moon moon() {
        return (Moon) moonMap.keySet().toArray()[0];
    }

    public CartesianCoordinates moonPosition() {
        return (CartesianCoordinates) moonMap.values().toArray()[0];
    }

    private <K extends CelestialObject> K getAt(final CelestialObjectModel<K> k) {
        return k.at(daysUntilJ2010, eclToEqu);
    }

    private <T, K extends CelestialObject> Map<K, CartesianCoordinates> calculatePosition(final List<T> data, final Function<T, K> f) {
        return data.stream().map(f).collect(Collectors.toUnmodifiableMap(Function.identity(),
                l -> positionToObserve.apply(eqToHor.apply(l.equatorialPos())), (u, v) -> v));
    }

    private <K extends CelestialObject> double[] positionsToArray(Map<K, CartesianCoordinates> objectsMap) {
        return objectsMap.values().stream().flatMap(l -> List.of(l.x(), l.y()).stream()).mapToDouble(
                Double::doubleValue).toArray();
    }

}
