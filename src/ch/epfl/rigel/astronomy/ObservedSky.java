package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.*;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Math.pow;

import static java.lang.Math.pow;

public final class ObservedSky {

    private final Map<Star, CartesianCoordinates> starMap;
    private final Map<Planet, CartesianCoordinates> planetMap;
    private final Map<Sun, CartesianCoordinates> sunMap;
    private final Map<Moon, CartesianCoordinates> moonMap;

    private final Map<CartesianCoordinates, ? extends CelestialObject> coordsToCelObjectsMap;

    private final StarCatalogue catalogue;
    private final List<Double> starsPositions;
    private final List<Planet> planetList;
    private final List<Double> planetPositions;

    private final StereographicProjection positionToObserve;
    private final EquatorialToHorizontalConversion eqToHor;
    private final ZonedDateTime dateT;

    public ObservedSky(final ZonedDateTime date, final GeographicCoordinates geoCoords,
                       final StereographicProjection projection, final StarCatalogue catalogue) {
        this.positionToObserve = projection;
        this.eqToHor = new EquatorialToHorizontalConversion(date, geoCoords);
        this.dateT = date;
        this.catalogue = catalogue; //Kept for its immutable list of stars

        final List<PlanetModel> extraTerrPlanets = Arrays.asList(PlanetModel.values());
        extraTerrPlanets.remove(2); //removing the earth

        starMap = Map.copyOf(calculatePosition(catalogue.stars(), Function.identity()));
        planetMap = Map.copyOf(calculatePosition(List.copyOf(extraTerrPlanets), this::getAt));
        sunMap = Map.copyOf(calculatePosition(List.of(SunModel.SUN), this::getAt));
        moonMap = Map.copyOf(calculatePosition(List.of(MoonModel.MOON), this::getAt));

        planetList = List.copyOf(planetMap.keySet());
        planetPositions = List.copyOf(positionsToList(planetMap));
        starsPositions = List.copyOf(positionsToList(starMap));

        coordsToCelObjectsMap = mergeMaps(List.of(invertKeys(starMap), invertKeys(planetMap), invertKeys(sunMap), invertKeys(moonMap)));
    }
    static private final Function<CartesianCoordinates, BiFunction<CartesianCoordinates, CartesianCoordinates, Integer>> f =
            c -> (a, b) -> Double.compare((pow(a.x() - c.x(), 2) + pow(a.y() - c.y(), 2)) , (pow(b.x() - c.x(), 2) + pow(b.y()- c.y(), 2)));

    public Optional<CelestialObject> objectClosestTo(final CartesianCoordinates point, final double maxDistance) {
        final CartesianCoordinates closestCoord = coordsToCelObjectsMap.keySet().stream()
                .min(Comparator.comparingDouble(coord -> euclidianDistance(coord, point))).get();
        return (euclidianDistance(closestCoord,point) <= maxDistance) ? Optional.of(coordsToCelObjectsMap.get(closestCoord)) : Optional.empty();
    }
    Optional<? extends CelestialObject> objectClosestTo (CelestialObject c, double distMax, CartesianCoordinates me) {

        BiFunction<CartesianCoordinates, CartesianCoordinates, Integer> d = f.apply(me);
        var a = Stream.concat(Stream.concat(starMap.entrySet().stream(), planetMap.entrySet().stream()),
                Stream.concat(sunMap.entrySet().stream(), moonMap.entrySet().stream()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (value1, value2) -> value2));

        return a.keySet().stream().min((l, j) -> d.apply(a.get(l), a.get(j)));
    }

    public List<Star> stars() {
        return catalogue.stars();
    }

    public List<Double> starsPosition() {
        return starsPositions;
    }


    public List<Planet> planets() {
        return planetList;
    }

    public List<Double> planetPosition() {
        return planetPositions;
    }

    public Sun sun() {
        return (Sun) sunMap.keySet().toArray()[0]; //note that the keySet's size is only 1, hence toArray costs 1 flop
    }

    public CartesianCoordinates sunPosition() {
        return (CartesianCoordinates) sunMap.values().toArray()[0];
    }

    public Moon moon() {
        return (Moon) moonMap.keySet().toArray()[0];
    }

    public CartesianCoordinates moonPosition() {
        return (CartesianCoordinates) moonMap.values().toArray()[0];
    }

    private static double euclidianDistance(CartesianCoordinates coord1, CartesianCoordinates coord2) {
        return Math.sqrt((coord1.x() - coord2.x())*(coord1.x() - coord2.x()) + (coord1.y() - coord2.y())*(coord1.y() - coord2.y()));
    }

    private <K extends CelestialObject> K getAt(final CelestialObjectModel<K> k) {
        return k.at(Epoch.J2010.daysUntil(dateT), new EclipticToEquatorialConversion(dateT));
    }

    private <T, K extends CelestialObject> Map<K, CartesianCoordinates> calculatePosition(final List<T> data, final Function<T, K> f) {
        return data.stream().map(f).collect(Collectors.toUnmodifiableMap(Function.identity(),
                l -> positionToObserve.apply(eqToHor.apply(l.equatorialPos())), (u, v) -> v));
    }

    private <K extends CelestialObject> List<Double> positionsToList(Map<K, CartesianCoordinates> objectsMap) {
        return objectsMap.values().stream().flatMap(l -> List.of(l.x(), l.y()).stream()).collect(Collectors.toList());
    }

    private static <K, V> Map<V, K> invertKeys(Map<K, V> map) {
        return map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    private static Map<CartesianCoordinates, CelestialObject> mergeMaps(List<Map<CartesianCoordinates, ? extends CelestialObject>> maps) {
        final Map<CartesianCoordinates, CelestialObject> bigMap = new HashMap<>();
        maps.forEach(bigMap::putAll);
        return bigMap;
    }

    /*public <T> List<T> getObj(Class<T> cls) throws IllegalAccessException {
        for (final Field f : ObservedSky.class.getDeclaredFields())
        {
            if (f.getType() == Map.class) {
                f.setAccessible(true);
                if(((Map<T, CartesianCoordinates>)f.get(this)).keySet().toArray()[0].getClass() == cls)
                {
                    return new ArrayList<T>(((Map<T, CartesianCoordinates>) f.get(this)).keySet());
                }
            }
        }
        throw new IllegalArgumentException();
    }*/

}
