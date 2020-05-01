package ch.epfl.rigelTest.gui;

import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialToHorizontalConversion;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.gui.searchtool.SearchBar;
import ch.epfl.rigel.math.sets.implement.MathSet;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class    SearchBarTest {
    private static final String HYG_CATALOGUE_NAME =
            "/hygdata_v3.csv";
    private static final String AST_CATALOGUE_NAME =
            "/asterisms.txt";
    private static StarCatalogue catalogue;
    private static ObservedSky sky;
    private static StereographicProjection stereo;
    private static GeographicCoordinates geoCoords;
    private static ZonedDateTime time;
    private static EquatorialToHorizontalConversion convEquToHor;
    private static EclipticToEquatorialConversion convEcltoEqu;

    @Test
    void init() throws IOException {

        if (catalogue == null) {
            long time0 = System.nanoTime();
            StarCatalogue.Builder builder;
            try (InputStream hygStream = getClass()
                    .getResourceAsStream(HYG_CATALOGUE_NAME)) {
                builder = new StarCatalogue.Builder()
                        .loadFrom(hygStream, HygDatabaseLoader.INSTANCE);
            }
            try (InputStream astStream = getClass()
                    .getResourceAsStream(AST_CATALOGUE_NAME)) {
                catalogue = builder
                        .loadFrom(astStream, AsterismLoader.INSTANCE)
                        .build();
            }

            time = ZonedDateTime.of(
                    LocalDate.of(2020, Month.APRIL, 4),
                    LocalTime.of(0, 0), ZoneOffset.UTC
            );

            geoCoords = GeographicCoordinates.ofDeg(30, 45);

            stereo = new StereographicProjection(HorizontalCoordinates.ofDeg(20, 22));

            convEquToHor = new EquatorialToHorizontalConversion(time, geoCoords);

            convEcltoEqu = new EclipticToEquatorialConversion(time);

            sky = new ObservedSky(time, geoCoords, stereo, catalogue);
            //System.out.println(System.nanoTime()-time0);
        }
    }

    @Test
    void stats() throws IOException {
        init();
        MathSet<CelestialObject> celestSet;
        Map<Character, Integer> charMap = new HashMap<>();
        for (char i = 'A'; i <= 'Z'; ++i) {
            char finalI = Character.toLowerCase(i);
            celestSet = sky.celestialObjMap().keySet().stream()
                    .filter(celest -> celest.name().substring(0,2).contains("" + finalI))
                    .collect(MathSet.toMathSet());
            charMap.put(finalI, celestSet.cardinality());

            celestSet = sky.celestialObjMap().keySet().stream()
                    .filter(celest -> celest.name().length() <= 2)
                    .collect(MathSet.toMathSet());
            //ALL CELEST OBJECTS HAVE NAME LENGTH AT LEAST 3
            if (celestSet.cardinality() != 0) {
                //System.out.println(celestSet);
            }
        }
        //System.out.println(charMap);
        //Final charmap:
        // {A=82, B=58, C=42, D=84, E=141, F=1, G=74, H=4, I=65, J=1, K=74, L=59, M=78, N=66, O=77, P=149, Q=0, R=54,
        //  S=58, T=126, U=43, V=3, W=1, X=51, Y=0, Z=81}

        System.out.println(sky.celestialObjMap().keySet().stream().collect(MathSet.toMathSet()).suchThat(
                celest -> !Character.isAlphabetic(celest.name().charAt(0)) && !Character.isAlphabetic(celest.name().charAt(2))
        ));
    }

    @Test
    void searchBarTest() throws IOException{
        init();
        SearchBar search = new SearchBar(sky);
        Set<CelestialObject> lul = search.search("S", SearchBar.Filters.ALL, SearchBar.SearchBy.NAME, sky);
        assertTrue(sky.celestialObjMap().keySet().containsAll(lul));
        Set<CelestialObject> lul2 = search.search("Sc", SearchBar.Filters.ALL, SearchBar.SearchBy.NAME, sky);
        assertTrue(lul.containsAll(lul2) && !lul2.containsAll(lul));

        assertEquals("Sch",search.suggestions("Sc", SearchBar.SearchBy.NAME).stream().findAny().get());

        search.endSearch();

        Set<CelestialObject> test = search.search("2", SearchBar.Filters.STARS, SearchBar.SearchBy.HIPPARCOS, sky);
        assertEquals(sky.starsMap().keySet().stream().filter(star -> String.valueOf(star.hipparcosId()).startsWith("2"))
                .collect(Collectors.toSet()), test);

        Set<CelestialObject> test2 = search.search("22", SearchBar.Filters.STARS, SearchBar.SearchBy.HIPPARCOS, sky);
        assertEquals(sky.starsMap().keySet().stream().filter(star -> String.valueOf(star.hipparcosId()).startsWith("22"))
                .collect(Collectors.toSet()), test2);

        assertEquals(0, search.search("a", SearchBar.Filters.STARS, SearchBar.SearchBy.HIPPARCOS, sky).size());

        search.endSearch();

    }

}
