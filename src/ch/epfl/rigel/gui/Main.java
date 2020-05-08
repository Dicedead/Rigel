package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;

public final class Main extends Application {

    private static final GeographicCoordinates INITIAL_GEO_COORDS = GeographicCoordinates.ofDeg(6.57, 46.52);
    private static final HorizontalCoordinates INITIAL_CENTER = HorizontalCoordinates.ofDeg(180.000000000001, 15);
    private static final double INITIAL_FOV = 100;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        try (InputStream hs = resourceStream("/hygdata_v3.csv");
             InputStream ast = resourceStream("/asterisms.txt");
             InputStream fs = resourceStream("/Font Awesome 5 Free-Solid-900.otf")) {
            Font fontAwesome = Font.loadFont(fs, 15);

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

            Controller m = new Controller(catalogue, dateTimeBean, observerLocationBean, viewingParametersBean, fontAwesome,
                    primaryStage);
            m.getThisStage().setFullScreen(true);
            m.getThisStage().show();
            m.canvasRequestFocus();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private InputStream resourceStream(String s) {
        return getClass().getResourceAsStream(s);
    }

}
