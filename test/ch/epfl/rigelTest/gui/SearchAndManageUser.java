package ch.epfl.rigelTest.gui;

import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.gui.BlackBodyColor;
import ch.epfl.rigel.gui.DateTimeBean;
import ch.epfl.rigel.gui.ObserverLocationBean;
import ch.epfl.rigel.gui.SkyCanvasManager;
import ch.epfl.rigel.gui.ViewingParametersBean;
import ch.epfl.rigel.gui.searchtool.Searcher;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;


public final class SearchAndManageUser extends Application {
    public static void main(String[] args) { launch(args); }

    private InputStream resourceStream(String resourceName) {
        return getClass().getResourceAsStream(resourceName);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        try (InputStream hs = resourceStream("/hygdata_v3.csv");
             InputStream ast = resourceStream("/asterisms.txt")) {
            StarCatalogue catalogue = new StarCatalogue.Builder()
                    .loadFrom(hs, HygDatabaseLoader.INSTANCE)
                    .loadFrom(ast, AsterismLoader.INSTANCE)
                    .build();

            BlackBodyColor.init();

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

            SkyCanvasManager canvasManager = null; /*new SkyCanvasManager(
                    catalogue,
                    dateTimeBean,
                    observerLocationBean,
                    viewingParametersBean);*/

            canvasManager.objectUnderMouseProperty().addListener(
                    (p, o, n) -> n.ifPresent(System.out::println));

            Canvas sky = canvasManager.canvas();
            Searcher searcher = canvasManager.searcher();
            BorderPane root = new BorderPane(null,sky,null, searcher, null);

            sky.widthProperty().bind(root.widthProperty());
            sky.heightProperty().bind(root.heightProperty().multiply(0.95));

            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);

            primaryStage.setY(100);

            primaryStage.setTitle("Rigel");
            primaryStage.setScene(new Scene(root));
            //primaryStage.setFullScreen(true);
            primaryStage.show();

            sky.requestFocus();
        }
    }
}