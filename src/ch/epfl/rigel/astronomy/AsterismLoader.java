package ch.epfl.rigel.astronomy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

            final Map<Integer, Star> hipparcosToStarMap = builder.stars().stream()
                    .collect(Collectors.toUnmodifiableMap(Star::hipparcosId, Function.identity(), (v1, v2) -> v2)); //(*)
            //Using the function: star -> (hipparcosOf(star),star), and wrapping the result in a Map

            reader.lines().forEach(
                    line -> builder.addAsterism(new Asterism(Arrays.stream(line.split(","))
                            .map(hipparcos -> hipparcosToStarMap.get(Integer.parseInt(hipparcos))).collect(
                                    Collectors.toUnmodifiableList())))); //(**)
            //Mapping each line in inputStream to a List of stars via their hipparcos

        }
    }
    /* (*) Even though, upon source code inspection, HashMap is the default return type of Collectors.toMap, the
       following statement in its Javadoc pushed us to specify it anyway:
       "There are no guarantees on the type, mutability, serializability, or thread-safety of the Map returned."
       (**) The same goes for ArrayList. (and for HashSet)
 */
}