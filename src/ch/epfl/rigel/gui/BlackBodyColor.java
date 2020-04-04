package ch.epfl.rigel.gui;

import ch.epfl.rigel.Preconditions;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Utility class for translating temperatures into colors
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class BlackBodyColor {

    //Non instantiable
    private BlackBodyColor() {
        throw new UnsupportedOperationException();
    }
    /*
      The constructor of a non instantiable class throwing a UO Exception rather than just being private:
         a) guarantees that the following code does not create an instance, and
         b) is immune to reflection (Field.setAccessible)
     */

    static private class colorListSingleton
    {
        private final static List<Color> COLOR_LIST= (initMap());;
        private static final String COLOR_FILE = "/bbr_color.txt";
        private static final int FILE_USABLE_LENGTH = 782;
        private static final int SKIP_LINES_FILTERINT = 80;

        private colorListSingleton()
        {}

        private static List<Color> initMap()
        {
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(
                    BlackBodyColor.class.getResourceAsStream(COLOR_FILE), StandardCharsets.US_ASCII))) {

                final List<String> linesOfInterest = reader.lines().filter(line -> line.length() > SKIP_LINES_FILTERINT)
                        .collect(Collectors.toUnmodifiableList());

                return IntStream.range(0, FILE_USABLE_LENGTH / 2).mapToObj(
                        i -> Color.web(linesOfInterest.get(i * 2 + 1).substring(81, 87))).collect(Collectors.toUnmodifiableList());

            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        public static List<Color> getInstance()
        {
            return COLOR_LIST;
        }
    }

    public static int init() {
        return colorListSingleton.getInstance().size();
    }
    /**
     * Get the Color corresponding to a temperature between 1000 and 40_000
     *
     * @param temperature (double) temperature in Kelvin
     * @return (Color) corresponding color
     * @throws IllegalArgumentException if temperature isn't in [1000,40000]
     */
    public static Color colorForTemperature(final double temperature) {

        Preconditions.checkArgument(1000 <= temperature && temperature <= 40_000);

        return colorListSingleton.getInstance().get((int) Math.round(temperature / 100) - 10);
    }
}
