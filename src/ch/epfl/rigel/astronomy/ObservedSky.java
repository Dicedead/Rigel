package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.*;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ObservedSky {

    private final Map<Star, CartesianCoordinates> stars;
    private final Map<Planet, CartesianCoordinates> planets;
    private final Map<Sun, CartesianCoordinates> sun;
    private final Map<Moon, CartesianCoordinates> moon;

    private final StereographicProjection positionToObserve;
    private final EquatorialToHorizontalConversion eqToHor;
    private final ZonedDateTime dateT;

    public List<Star> stars() {
        return new ArrayList<>(stars.keySet());
    }

    public List<Double> starsPosition() {
        return stars.values().stream().flatMap(l -> List.of(l.x(), l.y()).stream()).collect(Collectors.toList());
    }


    public List<Planet> planets() {
        return new ArrayList<>(planets.keySet());
    }

    public List<Double> planetPosition() {
        return planets.values().stream().flatMap(l -> List.of(l.x(), l.y()).stream()).collect(Collectors.toList());
    }

    public Sun sun() {
        return new ArrayList<>(sun.keySet()).get(0);
    }

    public CartesianCoordinates sunPosition ()
    {
        return new ArrayList<>(sun.values()).get(0);
    }

    public Moon moon() {
        return new ArrayList<>(moon.keySet()).get(0);
    }

    public CartesianCoordinates moonPosition ()
    {
        return new ArrayList<>(moon.values()).get(0);
    }

    ObservedSky (final ZonedDateTime date, final GeographicCoordinates pO, final StereographicProjection pTO, final StarCatalogue sTO)
    {
        this.positionToObserve = pTO;
        this.eqToHor = new EquatorialToHorizontalConversion(date, pO);
        this.dateT = date;

        stars   = Map.copyOf(CalculatePosition(sTO.stars(), Function.identity()));
        planets = Map.copyOf(CalculatePosition(List.of(PlanetModel.values()), this::getAt));
        sun     = Map.copyOf(CalculatePosition(List.of(SunModel.SUN), this::getAt));
        moon    = Map.copyOf(CalculatePosition(List.of(MoonModel.MOON), this::getAt));


    }

    private<K extends CelestialObject> K getAt (final CelestialObjectModel<K> k)
    {
        return k.at(Epoch.J2010.daysUntil(dateT), new EclipticToEquatorialConversion(dateT));
    }

    private <T, K extends CelestialObject > Map<K, CartesianCoordinates> CalculatePosition (final List<T> data, final Function<T, K> f)
    {
        return data.stream().map(f).collect(Collectors.toUnmodifiableMap(Function.identity(),
                l -> positionToObserve.apply(eqToHor.apply(l.equatorialPos())), (u, v) -> v));
    }

}
