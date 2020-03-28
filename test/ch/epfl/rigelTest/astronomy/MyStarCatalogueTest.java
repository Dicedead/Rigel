package ch.epfl.rigelTest.astronomy;

import ch.epfl.rigel.astronomy.Asterism;
import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.Star;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MyStarCatalogueTest {

    private static final String HYG_CATALOGUE_NAME =
            "/hygdata_v3.csv";
    private static final String AST_CATALOGUE_NAME =
            "/asterisms.txt";
    private static StarCatalogue.Builder builder;
    private static StarCatalogue catalogue;

    @BeforeAll
    static void init() throws IOException{
        try (InputStream hygStream = MyStarCatalogueTest.class.getResourceAsStream(HYG_CATALOGUE_NAME)) {
            builder = new StarCatalogue.Builder()
                    .loadFrom(hygStream, HygDatabaseLoader.INSTANCE);
        }
        try (InputStream astStream = MyStarCatalogueTest.class.getResourceAsStream(AST_CATALOGUE_NAME)) {
            catalogue = builder
                    .loadFrom(astStream, AsterismLoader.INSTANCE)
                    .build();
        }
    }

    @Test
    void listIndicesWork() {
        assertEquals(1019,catalogue.asterismIndices(builder.asterisms().get(94)).get(0));
        for(Asterism testAst : catalogue.asterisms()) {
            for(Star testStar : testAst.stars()) {
                Star currentStar = catalogue.stars().get(catalogue.asterismIndices(testAst).get(testAst.stars().indexOf(testStar)));
                assertEquals(currentStar,testStar);
            }
        }
    }

    @Test
    void constructorThrows() {
        assertThrows(IllegalArgumentException.class, () -> new StarCatalogue(List.of(new Star(242,"lol", EquatorialCoordinates.of(0,0),0,0)),List.of(
                new Asterism(List.of(new Star(242,"lol", EquatorialCoordinates.of(0,0),0,0)))
        )));
    }

    @Test
    void starsIsImmutable() {
        List<Star> starList = new ArrayList<>();
        starList.add(new Star(0456, "star0", EquatorialCoordinates.of(0, 0), -0.5f, -0.5f));
        starList.add(new Star(0443, "stir", EquatorialCoordinates.of(0, 0), -0.5f, 3.5f));
        starList.add(new Star(45789070, "staaar", EquatorialCoordinates.of(0, 0), -0.5f, 0.5f));

        assertThrows(UnsupportedOperationException.class, () -> { new Asterism(starList).stars().add(new Star(5676545, "starLight", EquatorialCoordinates.of(0,0), 0.2f, 1)); });
        assertThrows(UnsupportedOperationException.class, () -> { new Asterism(starList).stars().remove(1); });

        Asterism astres = new Asterism(starList);
        starList.remove(2);
        astres.stars().get(2);

        starList.add(new Star(45789070, "staaar", EquatorialCoordinates.of(0, 0), -0.5f, 0.5f));
        starList.add(new Star(45770, "staaoar", EquatorialCoordinates.of(0, 0), -0.5f, 0.2f));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {astres.stars().get(3);});



    }
}
