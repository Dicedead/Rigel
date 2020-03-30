package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.*;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.Math.pow;

public final class ObservedSky {

    private final Map<Star, CartesianCoordinates> starMap;
    private final Map<Planet, CartesianCoordinates> planetMap;
    private final Map<Sun, CartesianCoordinates> sunMap;
    private final Map<Moon, CartesianCoordinates> moonMap;

    private final Map< CelestialObject , CartesianCoordinates> coordsToCelObjectsMap = new HashMap<>();

    private final StarCatalogue catalogue;
    private final List<Double> starsPositions;
    private final List<Planet> planetList;
    private final List<Double> planetPositions;

    private final StereographicProjection positionToObserve;
    private final EquatorialToHorizontalConversion eqToHor;
    private final ZonedDateTime dateT;

    static private final Function<CartesianCoordinates, BiFunction<CartesianCoordinates, CartesianCoordinates, Integer>>
            CLOSEST_TO_C = c -> (a, b) -> Double.compare((pow(a.x() - c.x(), 2) + pow(a.y() - c.y(), 2)) , (pow(b.x() - c.x(), 2)
                + pow(b.y()- c.y(), 2)));


    public ObservedSky(final ZonedDateTime date, final GeographicCoordinates geoCoords,
                       final StereographicProjection projection, final StarCatalogue catalogue) {
        this.positionToObserve = projection;
        this.eqToHor = new EquatorialToHorizontalConversion(date, geoCoords);
        this.dateT = date;
        this.catalogue = catalogue; //Kept for its immutable list of stars

        final List<PlanetModel> extraTerrPlanets = new ArrayList<>(Arrays.asList(PlanetModel.values()));
        extraTerrPlanets.remove(2); //removing the earth

        starMap = Map.copyOf(calculatePosition(catalogue.stars(), Function.identity()));
        planetMap = Map.copyOf(calculatePosition(List.copyOf(extraTerrPlanets), this::getAt));
        sunMap = Map.copyOf(calculatePosition(List.of(SunModel.SUN), this::getAt));
        moonMap = Map.copyOf(calculatePosition(List.of(MoonModel.MOON), this::getAt));

        planetList = List.copyOf(planetMap.keySet());
        planetPositions = List.copyOf(positionsToList(planetMap));
        starsPositions = List.copyOf(positionsToList(starMap));

        List.of(starMap, planetMap, sunMap, moonMap).forEach(coordsToCelObjectsMap::putAll);
    }

    public Optional<CelestialObject> objectClosestTo (final double distMax, final CartesianCoordinates me) {
        return coordsToCelObjectsMap.keySet().stream().min((l, j) -> CLOSEST_TO_C.apply(me)
                .apply(coordsToCelObjectsMap.get(l), coordsToCelObjectsMap.get(j)))
                .filter(i -> CLOSEST_TO_C.apply(me).apply(coordsToCelObjectsMap.get(i), me) < distMax);
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
        return (Sun) sunMap.keySet().toArray()[0]; //note that the keySet's size is only 1, hence toArray costs 1 flop,
                                                   //and one more to get
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



}
