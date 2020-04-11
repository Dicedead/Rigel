package ch.epfl.rigel.gui;

import ch.epfl.rigel.logging.RigelLogger;
import ch.epfl.rigel.math.ClosedInterval;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ch.epfl.rigel.Preconditions.checkInInterval;

/**
 * Utility class for translating temperatures into colors
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class BlackBodyColor {

    private final static ClosedInterval TEMP_INTERVAL = ClosedInterval.of(1_000, 40_000);

    //Non instantiable
    private BlackBodyColor() {
        throw new UnsupportedOperationException();
    }
    /*
      The constructor of a non instantiable class throwing a UO Exception rather than just being private:
         a) guarantees that the following code does not create an instance, and
         b) is immune to reflection (Field.setAccessible)
     */

    static private class ColorListSingleton {

        private final static List<Color> COLOR_LIST = initList();
        //Singleton pattern used to enforce instantiation on first call while maintaining immutability

        private static final String COLOR_FILE = "/bbr_color.txt";
        private static final int FILE_USABLE_LENGTH = 782;
        private static final int SKIP_LINES_FILTERINT = 80;

        private ColorListSingleton() {
        }

        private static List<Color> initList() {
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(
                    BlackBodyColor.class.getResourceAsStream(COLOR_FILE), StandardCharsets.US_ASCII))) {

                final List<String> linesOfInterest = reader.lines().filter(line -> line.length() > SKIP_LINES_FILTERINT)
                        .collect(Collectors.toUnmodifiableList());

                //##RigelLogger.getGuiLogger().fine("COLOR_LIST has been successfully initialised");
                return IntStream.range(0, FILE_USABLE_LENGTH / 2).mapToObj(
                        i -> Color.web(linesOfInterest.get(i * 2 + 1).substring(81, 87))).collect(Collectors.toUnmodifiableList());

            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        private static List<Color> getInstance() {
            return COLOR_LIST;
        }
    }

    /**
     * @return (int) Size of list of colors, a way to initialise and (eventually) debug the colors list
     */
    public static int init() {
        //##RigelLogger.getGuiLogger().info("Building COLOR_LIST");
        return ColorListSingleton.getInstance().size();
    }


    /**
     * Get the Color corresponding to a temperature between 1000 and 40_000
     *
     * @param temperature (double) temperature in Kelvin
     * @return (Color) corresponding color
     * @throws IllegalArgumentException if temperature isn't in [1000,40000]
     */
    public static Color colorForTemperature(final double temperature) {
        return ColorListSingleton.getInstance().get((int) Math.round(checkInInterval(TEMP_INTERVAL, temperature) / 100) - 10);
    }
}
