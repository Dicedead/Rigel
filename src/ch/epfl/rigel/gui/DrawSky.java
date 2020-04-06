package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.logging.RigelLogger;
import ch.epfl.rigel.parallelism.RigelThreadFactory;
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
import java.time.temporal.ChronoUnit;
import java.util.concurrent.*;

public final class DrawSky extends Application {
    public static void main(String[] args) { launch(args); }

    private InputStream resourceStream(final String file) {
        return getClass().getResourceAsStream(file);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        ThreadManager.initThreads();

        ThreadManager.getLogger().execute(() -> RigelLogger.init(new File("logs/Step8"), RigelLogger.runType.DEBUG));


                try (InputStream hs = resourceStream("/hygdata_v3.csv"); InputStream ast = resourceStream("/asterisms.txt")) {
                    BlackBodyColor.init();

                    final Future<StarCatalogue> catalogue = ThreadManager.getGui().submit(() -> new StarCatalogue.Builder()
                            .loadFrom(hs, HygDatabaseLoader.INSTANCE).loadFrom(ast, AsterismLoader.INSTANCE)
                            .build());
                    final Future<ZonedDateTime> when = ThreadManager.getGui().submit( () -> ZonedDateTime.parse("2020-02-17T20:15:00+01:00"));

                    final Future<GeographicCoordinates> where = ThreadManager.getAstronomy().submit(() ->  GeographicCoordinates.ofDeg(6.57, 46.52));
                    final Future<HorizontalCoordinates> projCenter = ThreadManager.getAstronomy().submit(() ->  HorizontalCoordinates.ofDeg(180, 45));
                    final Future<StereographicProjection> projection = ThreadManager.getAstronomy().submit(() -> new StereographicProjection(projCenter.get()));

                    final Future<Canvas> canvasFuture = ThreadManager.getGui().submit(() -> new Canvas(800, 600));

                    final Future<ObservedSky> skyFuture = ThreadManager.getAstronomy().submit(() -> new ObservedSky(when.get(), where.get(), projection.get(), catalogue.get()));
                    final Future<Transform> transformFutureFuture = ThreadManager.getGui().submit(() -> Transform.affine(1300, 0, 0, -1300, 400, 300));

                    final Future<SkyCanvasPainter> painterFuture = ThreadManager.getGui().submit(() -> new SkyCanvasPainter(canvasFuture.get()));

                    RigelLogger.getBackendLogger().info("Beginning gui");
                    var a = ThreadManager.getGui().submit(()-> {

                        try {
                            painterFuture.get().clear();
                            painterFuture.get().drawAsterisms(skyFuture.get(), transformFutureFuture.get());
                            painterFuture.get().drawSun(skyFuture.get(), projection.get(), transformFutureFuture.get());
                            painterFuture.get().drawStars(skyFuture.get(), projection.get(), transformFutureFuture.get());
                            painterFuture.get().drawPlanets(skyFuture.get(), projection.get(), transformFutureFuture.get());
                            painterFuture.get().drawMoon(skyFuture.get(), projection.get(), transformFutureFuture.get());
                            painterFuture.get().drawHorizon(skyFuture.get(), projection.get(), transformFutureFuture.get());
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                    );
                    a.get();

                    while (!a.isDone())
                        RigelLogger.getBackendLogger().info("drawing stars");

                    ImageIO.write(SwingFXUtils.fromFXImage(canvasFuture.get().snapshot(null, null),
                                    null), "png", new File("sky.png"));

                    ThreadManager.getGui().shutdownNow();
                    ThreadManager.getAstronomy().shutdownNow();
                    ThreadManager.getIo().shutdown();
                    ThreadManager.getLogger().shutdownNow();

                } catch (InterruptedException | ExecutionException | IOException e) {
                    e.printStackTrace();
                }
        Platform.exit();

    }
}
