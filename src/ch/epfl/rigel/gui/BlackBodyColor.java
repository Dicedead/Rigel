package ch.epfl.rigel.gui;

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
 * Utility class for converting temperatures into colors
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class BlackBodyColor {

    private final static ClosedInterval TEMP_INTERVAL = ClosedInterval.of(1_000, 40_000);

    //Non instantiable
    private BlackBodyColor() {
        throw new UnsupportedOperationException("Fatal error: Tried to instantiate non" +
                "instantiable class BlackBodyColor.");
    }

    /**
     * @return (int) Size of list of colors, a way to initialise and (eventually) debug the colors list
     */
    public static int init() {
        return ColorListSingleton.getInstance().size();
    }


    /**
     * Get the Color corresponding to a temperature between 1000 and 40_000
     *
     * @param temperature (double) temperature in Kelvin
     * @return (Color) corresponding color
     * @throws IllegalArgumentException if temperature isn't in [1000,40000]
     */
    public static Color colorForTemperature(double temperature) {
        return ColorListSingleton.getInstance().get((int) Math.round(checkInInterval(TEMP_INTERVAL, temperature) / 100) - 10);
    }

    static private class ColorListSingleton {

        private final static List<Color> COLOR_LIST = initList();
        //Singleton pattern used to enforce instantiation on first call while maintaining immutability

        private static final String COLOR_FILE          = "/bbr_color.txt";
        private static final int FILE_USABLE_LENGTH     = 782;
        private static final int SKIP_LINES_FILTERINT   = 80;
        private static final int START_USABLE           = 81;
        private static final int END_USABLE             = 87;

        private ColorListSingleton() {
            throw new UnsupportedOperationException("Fatal error: tried to instantiate non instantiable class" +
                    "ColorListSingleton.");
        }

        private static List<Color> initList() {
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(
                    BlackBodyColor.class.getResourceAsStream(COLOR_FILE), StandardCharsets.US_ASCII))) {

                List<String> linesOfInterest = reader.lines().filter(line -> line.length() > SKIP_LINES_FILTERINT)
                        .collect(Collectors.toUnmodifiableList());

                return IntStream.range(0, FILE_USABLE_LENGTH / 2)
                        .mapToObj(i -> Color.web(linesOfInterest.get(i * 2 + 1).substring(START_USABLE, END_USABLE)))
                        .collect(Collectors.toUnmodifiableList());

            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        private static List<Color> getInstance() {
            return COLOR_LIST;
        }
    }
}
