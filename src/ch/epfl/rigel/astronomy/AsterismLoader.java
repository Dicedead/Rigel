package ch.epfl.rigel.astronomy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Utility class for loading asterisms
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public enum AsterismLoader implements StarCatalogue.Loader {

    INSTANCE;
    /**
     * Loads a catalogue of asterisms into a builder
     *
     * @param inputStream (InputStream)
     * @param builder (StarCatalogue.Builder)
     * @throws IOException (as expected from I/O methods)
     */
    @Override
    public void load(InputStream inputStream, StarCatalogue.Builder builder) throws IOException {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,
                StandardCharsets.US_ASCII))) {

            //Making this map avoids the need of another for loop in the main while loop
            Map<Integer,Star> hipparcosToStarMap = new HashMap<>();
            for(Star star : builder.stars()) {
                hipparcosToStarMap.put(star.hipparcosId(),star);
            }

            while (reader.ready()) {
                List<Star> starsInAsterism = new ArrayList<>();
                String[] line = reader.readLine().split(",");

                for(String hipparcosString : line) {
                    starsInAsterism.add(hipparcosToStarMap.get(Integer.parseInt(hipparcosString)));

                }
                builder.addAsterism(new Asterism(starsInAsterism));

            }
        }
    }
}