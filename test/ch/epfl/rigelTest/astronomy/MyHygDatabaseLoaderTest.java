package ch.epfl.rigelTest.astronomy;

import ch.epfl.rigel.astronomy.Asterism;
import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.Star;
import ch.epfl.rigel.astronomy.StarCatalogue;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MyHygDatabaseLoaderTest {
    private static final String HYG_CATALOGUE_NAME =
            "/hygdata_v3.csv";

    private static final String ASTERISM_CATALOGUE_NAME =
            "/asterisms.txt";

    @Test
    void hygDatabaseIsCorrectlyInstalled() throws IOException {
        try (InputStream hygStream = getClass()
                .getResourceAsStream(HYG_CATALOGUE_NAME)) {
            assertNotNull(hygStream);
        }
    }

    @Test
    void hygDatabaseContainsRigel() throws IOException {
        try (InputStream hygStream = getClass()
                .getResourceAsStream(HYG_CATALOGUE_NAME)) {
            StarCatalogue catalogue = new StarCatalogue.Builder()
                    .loadFrom(hygStream, HygDatabaseLoader.INSTANCE)
                    .build();
            Star rigel = null;
            for (Star s : catalogue.stars()) {
                if (s.name().equalsIgnoreCase("rigel"))
                    rigel = s;
            }
            assertNotNull(rigel);
        }
    }

    @Test
    void hygDatabaseTreatsNamesCorrectly() throws IOException {
        try (InputStream hygStream = getClass()
                .getResourceAsStream(HYG_CATALOGUE_NAME)) {
            StarCatalogue.Builder builder = new StarCatalogue.Builder();
            builder.loadFrom(hygStream, HygDatabaseLoader.INSTANCE);

            int i = 0;
            for (Star star : builder.stars()) {
                if (star.name().charAt(0) == '?') {
                    i = 1;
                    assertEquals(' ', star.name().charAt(1));
                }
            }
            assertEquals(1, i);
        }
    }

    @Test
    void hygDatabaseDefaultValues() throws IOException {
        try (InputStream hygStream = getClass()
                .getResourceAsStream(HYG_CATALOGUE_NAME)) {
            StarCatalogue.Builder builder = new StarCatalogue.Builder();
            builder.loadFrom(hygStream, HygDatabaseLoader.INSTANCE);
            for (Star star : builder.stars()) {
                if (star.name().equals("Tau")) {

                    assertEquals(0, star.colorTemperature());
                }

            }
        }
    }

    @Test
    void variousTestsAndReadablePrintfOnCompletelyFinishedStarCatalogue() throws IOException {
        try (InputStream hygStream = getClass()
                .getResourceAsStream(HYG_CATALOGUE_NAME)) {
            InputStream asterismStream = getClass()
                    .getResourceAsStream(ASTERISM_CATALOGUE_NAME);
            StarCatalogue catalogue = new StarCatalogue.Builder()
                    .loadFrom(hygStream, HygDatabaseLoader.INSTANCE).loadFrom(asterismStream, AsterismLoader.INSTANCE)
                    .build();
            Star rigel = null;
            for (Star s : catalogue.stars()) {
                if (s.name().equalsIgnoreCase("rigel"))
                    rigel = s;
            }
            assertNotNull(rigel);

            List<Star> allStar = new ArrayList<Star>();
            allStar.addAll(catalogue.stars());

            int i;

            //vérifier visuellement en utilisant CTRL-F que les astérismes contenu dans ASTERISMS sont bien les memes
            //flemme de coder une méthode qui vérifie automatiquement
            for (Asterism asterism : catalogue.asterisms()) {
                List<Integer> cAstInd = catalogue.asterismIndices(asterism);
                i = 0;
                for (Star star : asterism.stars()) {

                /*TEST : l'index stoqué dans asterismIndices renvoie le meme hipparcosId que
                l'index stoqué dans l'astérisme voulu : */
                    assertEquals(allStar.get(cAstInd.get(i)).hipparcosId(), star.hipparcosId());
                    i++;
                }
            }
        }

    }
}
