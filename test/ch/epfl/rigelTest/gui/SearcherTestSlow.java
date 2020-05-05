package ch.epfl.rigelTest.gui;


import ch.epfl.rigel.astronomy.Asterism;
import ch.epfl.rigel.astronomy.Star;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.gui.searchtool.Searcher;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.List;

public class SearcherTestSlow extends Application {

    void basicSearch() {
        List<Star> stars = List.of(new Star(1, "Sa", EquatorialCoordinates.of(0,0), 5f,5f),
                new Star(1, "Se", EquatorialCoordinates.of(0,0), 5f,5f),
                new Star(1, "Sia", EquatorialCoordinates.of(0,0), 5f,5f)) ;
        StarCatalogue cat = new StarCatalogue(stars, List.of(new Asterism(stars)));

        //Searcher searcher = new Searcher(10, p -> true, cat);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        basicSearch();
        Platform.exit();
    }
}
