package ch.epfl.rigelTest.gui;

import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.gui.DateTimeBean;
import ch.epfl.rigel.gui.ObserverLocationBean;
import ch.epfl.rigel.gui.ViewingParametersBean;
import ch.epfl.rigel.gui.searchtool.Searcher;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;

public class SearcherTest extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        try (InputStream hs = resourceStream()) {
            StarCatalogue catalogue = new StarCatalogue.Builder()
                    .loadFrom(hs, HygDatabaseLoader.INSTANCE)
                    .build();

            ZonedDateTime when =
                    ZonedDateTime.parse("2020-02-17T20:15:00+01:00");
            DateTimeBean dateTimeBean = new DateTimeBean();
            dateTimeBean.setZonedDateTime(when);

            ObserverLocationBean observerLocationBean =
                    new ObserverLocationBean();
            observerLocationBean.setCoordinates(
                    GeographicCoordinates.ofDeg(6.57, 46.52));

            ViewingParametersBean viewingParametersBean =
                    new ViewingParametersBean();
            viewingParametersBean.setCenter(
                    HorizontalCoordinates.ofDeg(180.000000000001, 15));
            viewingParametersBean.setFieldOfViewDeg(70);
            ObservedSky sky = new ObservedSky(when, observerLocationBean.getCoords(),
                    new StereographicProjection(viewingParametersBean.getCenter()),
                    catalogue);

            Searcher searcher = new Searcher(5, sky);

            StackPane root = new StackPane();
            root.getChildren().add(searcher);
            searcher.setEditable(true);
            searcher.setVisible(true);

            primaryStage.setScene(new Scene(root, 300, 250));
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private InputStream resourceStream() {
        return getClass().getResourceAsStream("/hygdata_v3.csv");
    }
}