package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.AsterismLoader;
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
import javafx.scene.transform.Transform;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
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
            final Future<StarCatalogue> catalogue = ThreadManager.getIo().submit(() -> new StarCatalogue.Builder()
                    .loadFrom(hs, HygDatabaseLoader.INSTANCE).loadFrom(ast, AsterismLoader.INSTANCE).build());

            final StereographicProjection proj = new StereographicProjection(HorizontalCoordinates.ofDeg(180, 45));

            final Canvas canvasFuture = new Canvas(800, 600);

            final Transform transform =   Transform.affine(1300, 0, 0, -1300, 400, 300);

            final SkyCanvasPainter paint = new SkyCanvasPainter(canvasFuture);

            paint.clear();

            final Future<ObservedSky> skyFuture = ThreadManager.getAstronomy().submit(() -> new ObservedSky(
                    ZonedDateTime.parse("2020-02-17T20:15:00+01:00"), GeographicCoordinates.ofDeg(6.57, 46.52), proj, catalogue.get()));

            final var sky = skyFuture.get();

            RigelLogger.getBackendLogger().info("Beginning Celestial object drawing");
            ThreadManager.getGui().execute(
                    () ->
                    {
                        paint.drawAsterisms(sky, transform);
                        paint.drawStars(sky, proj, transform);
                        paint.drawPlanets(sky, proj, transform);
                        paint.drawSun(sky, proj, transform);
                        paint.drawMoon(sky, proj, transform);
                        paint.drawHorizon(sky, proj, transform);});

            RigelLogger.getBackendLogger().info("Finished drawing stars");
            ImageIO.write(SwingFXUtils.fromFXImage(canvasFuture.snapshot(null, null),
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
