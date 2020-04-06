package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.logging.RigelLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

/**
 * Utility class for loading the HYG database
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public enum HygDatabaseLoader implements StarCatalogue.Loader {

    INSTANCE;

    private enum Column {
        ID, HIP, HD, HR, GL, BF, PROPER, RA, DEC, DIST, PMRA, PMDEC,
        RV, MAG, ABSMAG, SPECT, CI, X, Y, Z, VX, VY, VZ,
        RARAD, DECRAD, PMRARAD, PMDECRAD, BAYER, FLAM, CON,
        COMP, COMP_PRIMARY, BASE, LUM, VAR, VAR_MIN, VAR_MAX
    }

    /**
     * Loads an HYG database into a builder
     *
     * @param inputStream (InputStream)
     * @param builder     (StarCatalogue.Builder)
     * @throws IOException (as expected from I/O methods)
     */
    @Override
    public void load(InputStream inputStream, StarCatalogue.Builder builder) throws IOException {

        RigelLogger.getFileLogger().info("Loading star file");
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,
                StandardCharsets.US_ASCII))) {

            //Skipping the first line
            reader.readLine();

            reader.lines().forEach(lineInFile -> {
                final String[] line = lineInFile.split(",");
                builder.addStar(
                        new Star(
                                /*hipparcos*/ buildWithDefault(line[Column.HIP.ordinal()], 0, Integer::parseInt),

                                /*name*/ buildWithDefault(line[Column.PROPER.ordinal()], buildWithDefault(line[Column.BAYER.ordinal()],
                                "? " + line[Column.CON.ordinal()], x -> (x + " " + line[Column.CON.ordinal()])), Function.identity()),

                                /*EquatorialCoords*/ EquatorialCoordinates.of(Double.parseDouble(line[Column.RARAD.ordinal()]),
                                Double.parseDouble(line[Column.DECRAD.ordinal()])),

                                /*magnitude*/ buildWithDefault(line[Column.MAG.ordinal()], 0, Float::parseFloat).floatValue(),

                                /*colorIndex*/ buildWithDefault(line[Column.CI.ordinal()], 0, Float::parseFloat).floatValue()
                        ));
            });
            RigelLogger.getFileLogger().fine("Finished loading Star file " );

        } catch (UncheckedIOException e) { //Streams throw UncheckedIOExceptions, and need not to modify the API
            throw e.getCause();
        }
    }

    /**
     * Auxiliary method associating a string to a return value of type T, either default if string's empty or
     * obtained via function convert
     *
     * @param sub     (String) targeted string
     * @param def     (T) default return value
     * @param convert (Function<String, T>) transformation to apply upon sub if non-empty
     * @param <T>     return value type
     * @return (T)
     */
    private static <T> T buildWithDefault(final String sub, final T def, final Function<String, T> convert) {
        return sub.equals("") ? def : convert.apply(sub);
    }
}
