package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.logging.RigelLogger;
import ch.epfl.rigel.parallelism.ThreadManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public final class DrawSky extends Application {
    public static void main(String[] args) { launch(args); }

    private InputStream resourceStream() {
        return getClass().getResourceAsStream("/hygdata_v3.csv");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        ThreadManager T = new ThreadManager();

        try (InputStream hs = resourceStream()){

            T.getGui().execute(() -> RigelLogger.init(new File("logs/Step8"), RigelLogger.runType.DEBUG));

            final Future<StarCatalogue> catalogue = T.getGui().submit(() -> new StarCatalogue.Builder()
                    .loadFrom(hs, HygDatabaseLoader.INSTANCE)
                    .build());

            final Future<ZonedDateTime> when = T.getGui().submit( () -> ZonedDateTime.parse("2020-02-17T20:15:00+01:00"));
            final Future<GeographicCoordinates> where = T.getAstronomy().submit(() ->  GeographicCoordinates.ofDeg(6.57, 46.52));
            final Future<HorizontalCoordinates> projCenter = T.getAstronomy().submit(() ->  HorizontalCoordinates.ofDeg(180, 45));
            final Future<StereographicProjection> projection = T.getAstronomy().submit(() -> new StereographicProjection(projCenter.get()));

            final Future<Canvas> canvasFuture = T.getGui().submit(() -> new Canvas(800, 600));
            final Future<ObservedSky> skyFuture = T.getAstronomy().submit(() -> new ObservedSky(when.get(), where.get(), projection.get(), catalogue.get()));
            final Future<Transform> transformFutureFuture = T.getGui().submit(() -> Transform.affine(1300, 0, 0, -1300, 400, 300));
            final Future<SkyCanvasPainter> painterFuture = T.getGui().submit(() -> new SkyCanvasPainter(canvasFuture.get()));

            T.getGui().execute(() ->
            {

                try {
                    painterFuture.get().clear();

                    painterFuture.get().drawStars(skyFuture.get(), projection.get(), transformFutureFuture.get());
                    painterFuture.get().drawPlanets(skyFuture.get(), projection.get(), transformFutureFuture.get());
                    painterFuture.get().drawSun(skyFuture.get(), projection.get(), transformFutureFuture.get());

                    ImageIO.write(SwingFXUtils.fromFXImage(canvasFuture.get().snapshot(null, null),
                            null), "png", new File("sky.png"));

                } catch (InterruptedException | ExecutionException | IOException e) {
                    e.printStackTrace();
                }
            });

        T.getGui().shutdownNow();
        T.getAstronomy().shutdownNow();
        T.getIo().shutdown();

        }
        Platform.exit();
    }
}
