package ch.epfl.rigel.astronomy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


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
     * @param builder     (StarCatalogue.Builder)
     * @throws IOException (as expected from I/O methods)
     */
    @Override
    public void load(InputStream inputStream, StarCatalogue.Builder builder) throws IOException {

        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,
                StandardCharsets.US_ASCII))) {

            //Making this map avoids the need of another for loop in the main while loop
            final Map<Integer, Star> hipparcosToStarMap = builder.stars().stream()
                    .collect(Collectors.toMap(Star::hipparcosId, Function.identity(), (v1, v2) -> v2));
            //Using the function: star -> (hipparcosOf(star),star), and wrapping the result in a Map

            while (reader.ready()) {
                builder.addAsterism(new Asterism(Arrays.stream(reader.readLine().split(","))
                        .map(s -> hipparcosToStarMap.get(Integer.parseInt(s))).collect(Collectors.toList())));
                //Mapping each line in inputStream to a List of stars via their hipparcos
            }
        }
    }
}