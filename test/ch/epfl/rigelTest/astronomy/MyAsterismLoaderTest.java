package ch.epfl.rigelTest.astronomy;

import ch.epfl.rigel.astronomy.Asterism;
import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.Star;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
            Asterism testAst = new Asterism(List.of(new Star (0,"lul", EquatorialCoordinates.of(0,0),0,0)));
            int astCount = 0;
            for (Asterism ast : a) {
                ++astCount;
                for (Star s : ast.stars()) {
                    if (s.name().equalsIgnoreCase("Betelgeuse")) {
                        testAst = ast;
                        beltegeuse = s;
                    }
                }
            }
            assertNotNull(beltegeuse);
            assertEquals(2,astCount);
            for(Star testStar : testAst.stars()) {
                assertEquals(catalogue.stars().get(catalogue.asterismIndices(testAst).get(testAst.stars().indexOf(testStar))).name(),
                        testStar.name());
            }
        }
    }

}
