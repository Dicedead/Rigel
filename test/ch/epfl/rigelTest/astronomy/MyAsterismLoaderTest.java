package ch.epfl.rigelTest.astronomy;

import ch.epfl.rigel.astronomy.Asterism;
import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.Star;
import ch.epfl.rigel.astronomy.StarCatalogue;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MyAsterismLoaderTest {

    private static final String HYG_CATALOGUE_NAME =
            "/hygdata_v3.csv";
    private static final String AST_CATALOGUE_NAME =
            "/asterisms.txt";

    @Test
    void loadWorks() throws IOException {
        StarCatalogue.Builder builder;
        Queue<Asterism> a = new ArrayDeque<>();
        Star beltegeuse = null;
        try (InputStream hygStream = getClass()
                .getResourceAsStream(HYG_CATALOGUE_NAME)) {
            builder = new StarCatalogue.Builder()
                    .loadFrom(hygStream, HygDatabaseLoader.INSTANCE);
        }
        try (InputStream astStream = getClass()
                .getResourceAsStream(AST_CATALOGUE_NAME)) {
            StarCatalogue catalogue = builder
                    .loadFrom(astStream, AsterismLoader.INSTANCE)
                    .build();
            for (Asterism ast : catalogue.asterisms()) {
                for (Star s : ast.stars()) {
                    if (s.name().equalsIgnoreCase("Rigel")) {
                        a.add(ast);
                    }
                }
            }
            for (Asterism ast : a) {
                for (Star s : ast.stars()) {
                    if (s.name().equalsIgnoreCase("Betelgeuse")) {
                        beltegeuse = s;
                    }
                }
            }
            assertNotNull(beltegeuse);
        }
    }

}
