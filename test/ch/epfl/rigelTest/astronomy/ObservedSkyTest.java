package ch.epfl.rigelTest.astronomy;

import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public class ObservedSkyTest {

    private static final String HYG_CATALOGUE_NAME =
            "/hygdata_v3.csv";
    private static final String AST_CATALOGUE_NAME =
            "/asterisms.txt";
    private static StarCatalogue catalogue;
    private static ObservedSky sky;

    @Test
    void init() throws IOException {

        if(catalogue == null) {
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

            ZonedDateTime observationTime = ZonedDateTime.of(
                    LocalDate.of(2020, Month.APRIL,4),
                    LocalTime.of(0,0), ZoneOffset.UTC
            );

            GeographicCoordinates geoCoords = GeographicCoordinates.ofDeg(30,45);

            StereographicProjection stereo = new StereographicProjection(HorizontalCoordinates.ofDeg(20,22));

            sky = new ObservedSky(observationTime,geoCoords,stereo,catalogue);
        }
    }

    @Test
    void objectClosestToWorks() throws IOException {
        init();
        double distance = 200;
        CartesianCoordinates cartesCoords = CartesianCoordinates.of(45,23);


    }
}
