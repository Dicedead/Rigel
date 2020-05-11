package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;

public final class Main extends Application {

    private static final GeographicCoordinates INITIAL_GEO_COORDS = GeographicCoordinates.ofDeg(6.57, 46.52);
    private static final HorizontalCoordinates INITIAL_CENTER = HorizontalCoordinates.ofDeg(180.000000000001, 15);
    private static final double INITIAL_FOV = 100;
    private static final int MIN_WIDTH = 800;
    private static final int MIN_HEIGHT = 600;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try (InputStream hs = resourceStream("/hygdata_v3.csv");
             InputStream ast = resourceStream("/asterisms.txt");
             InputStream fs = resourceStream("/Font Awesome 5 Free-Solid-900.otf")) {
            Font fontAwesome = Font.loadFont(fs, 15);
            BlackBodyColor.init();

            StarCatalogue catalogue = new StarCatalogue.Builder()
                    .loadFrom(hs, HygDatabaseLoader.INSTANCE)
                    .loadFrom(ast, AsterismLoader.INSTANCE)
                    .build();

            ZonedDateTime when = ZonedDateTime.now();
            DateTimeBean dateTimeBean = new DateTimeBean();
            dateTimeBean.setZonedDateTime(when);

            ObserverLocationBean observerLocationBean = new ObserverLocationBean();
            observerLocationBean.setCoordinates(INITIAL_GEO_COORDS);

            ViewingParametersBean viewingParametersBean = new ViewingParametersBean();
            viewingParametersBean.setCenter(INITIAL_CENTER);
            viewingParametersBean.setFieldOfViewDeg(INITIAL_FOV);

            Controller m = new Controller(catalogue, dateTimeBean, observerLocationBean, viewingParametersBean, fontAwesome);
            primaryStage.setMinWidth(MIN_WIDTH);
            primaryStage.setMinHeight(MIN_HEIGHT);
            primaryStage.setTitle("Rigel");
            primaryStage.setScene(new Scene(m.getLoader().load()));
            primaryStage.setFullScreen(true);
            primaryStage.show();
            m.canvasRequestFocus();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private InputStream resourceStream(String s) {
        return getClass().getResourceAsStream(s);
    }

}
