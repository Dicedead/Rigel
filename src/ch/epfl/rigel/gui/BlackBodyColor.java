package ch.epfl.rigel.gui;

import ch.epfl.rigel.Preconditions;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class BlackBodyColor {

    private static final String COLOR_FILE = "/bbr_color.txt";
    private static final int FILE_USABLE_LENGTH = 782;
    private static final int SKIP_FIRST_LINES = 19;

    private static final ArrayList<Color> COLOR_LIST = new ArrayList<>();

    //Non instantiable
    private BlackBodyColor() {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the Color corresponding to a temperature between 1000 and 40_000
     *
     * @param temperature (double) temperature in Kelvin
     * @return (Color) corresponding color
     * @throws IllegalArgumentException if temperature isn't in [1000,40000]
     * @throws UncheckedIOException     (I/O method)
     */
    public static Color colorForTemperature(final double temperature) {
        Preconditions.checkArgument(1000 <= temperature && temperature <= 40_000);

        if (COLOR_LIST.size() == 0) {
            //This list of size 381 is initialised only at first use of the method during execution, then the method
            //simply gets a Color from an index in COLOR_LIST.

            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(
                    BlackBodyColor.class.getResourceAsStream(COLOR_FILE)))) {

                final List<String> linesOfInterest = reader.lines().skip(SKIP_FIRST_LINES)
                        .limit(FILE_USABLE_LENGTH).collect(Collectors.toList());

                IntStream.range(0, FILE_USABLE_LENGTH / 2).forEach(
                        inc -> COLOR_LIST.add(Color.web(linesOfInterest.get(inc * 2 + 1).substring(80, 87)))
                );

            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }

        }

        return COLOR_LIST.get((int) Math.round(temperature / 100) - 10);
    }
}
