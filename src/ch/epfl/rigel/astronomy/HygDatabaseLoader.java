package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        COMP, COMP_PRIMARY, BASE, LUM, VAR, VAR_MIN, VAR_MAX;
    }

    private <T> T buildWithDefault(String sub, T def, Function<String, T> convert)
    {
        return  sub.equals("") ? def : convert.apply(sub);
    }
    /**
     * Loads an HYG database into a builder
     *
     * @param inputStream (InputStream)
     * @param builder (StarCatalogue.Builder)
     * @throws IOException (as expected from I/O methods)
     */
    @Override
    public void load(InputStream inputStream, StarCatalogue.Builder builder) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,
                StandardCharsets.US_ASCII))) {

            if (reader.ready())
                reader.readLine();

            while (reader.ready()) {
                final String[] line = reader.readLine().split(",");
                builder.addStar(
                        new Star(
                            buildWithDefault(line[Column.HIP.ordinal()], 0, Integer::parseInt),
                            buildWithDefault(line[Column.PROPER.ordinal()], buildWithDefault(line[Column.BAYER.ordinal()], "? ", x -> (x + " " + line[Column.CON.ordinal()])), Function.identity()),
                            EquatorialCoordinates.of(Double.parseDouble(line[Column.RARAD.ordinal()]), Double.parseDouble(line[Column.DECRAD.ordinal()])),
                            buildWithDefault(line[Column.MAG.ordinal()], 0, Float::parseFloat).floatValue(),
                            buildWithDefault(line[Column.CI.ordinal()], 0, Float::parseFloat).floatValue()
                        )
                );
            }
        }
    }
}
