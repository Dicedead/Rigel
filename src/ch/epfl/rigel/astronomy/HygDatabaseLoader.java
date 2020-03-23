package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

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
            String[] column = new String[0];
            if (reader.ready())
                column = reader.readLine().split(",");

            while (reader.ready()) {
                String[] line = reader.readLine().split(",");
                int hip = Integer.parseInt(line[Column.HIP.ordinal()].equals("") ? "0" : line[Column.HIP.ordinal()]);

                String proper = line[Column.PROPER.ordinal()].equals("") ? ((line[Column.BAYER.ordinal()].equals("") ? "?" :
                        line[Column.BAYER.ordinal()]) + " " + line[Column.CON.ordinal()]) : line[Column.PROPER.ordinal()];

                float magnitude = Float.parseFloat(line[Column.MAG.ordinal()].equals("") ? "0" : line[Column.MAG.ordinal()]);
                float colorIndex = Float.parseFloat(line[Column.CI.ordinal()].equals("") ? "0" : line[Column.CI.ordinal()]);

                builder.addStar(new Star(hip, proper, EquatorialCoordinates.of(Double.parseDouble(line[Column.RARAD.ordinal()]),
                        Double.parseDouble(line[Column.DECRAD.ordinal()])), magnitude, colorIndex));


            }
        }
    }
}
