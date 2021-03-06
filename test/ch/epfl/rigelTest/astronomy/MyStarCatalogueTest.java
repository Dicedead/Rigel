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

    //Essentially tested in corresponding loaders

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
            builder.loadFrom(astStream, AsterismLoader.INSTANCE);

            //long time0 = System.nanoTime();
            catalogue = builder.build();
            //System.out.println(System.nanoTime()-time0);
        }
        assertEquals(5067, catalogue.stars().size());
        assertEquals(153, catalogue.asterisms().size());
    }

    @Test
    void listIndicesWork() {
        assertEquals(1019,catalogue.asterismIndices(builder.asterisms().get(94)).get(0));
        for(Asterism testAst : catalogue.asterisms()) {
            assertThrows(UnsupportedOperationException.class, () -> catalogue.asterismIndices(testAst).set(0,5));
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
}
