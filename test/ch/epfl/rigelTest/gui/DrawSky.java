package ch.epfl.rigelTest.gui;

import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.coordinates.PlanarTransformation;
import ch.epfl.rigel.gui.BlackBodyColor;
import ch.epfl.rigel.gui.NamedTimeAccelerator;
import ch.epfl.rigel.gui.SkyCanvasPainter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.ZonedDateTime;

/**
 * Main GUI class
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class DrawSky extends Application {

    public static void main(String[] args) { launch(args); }

    private InputStream resourceStream(final String file) {
        return getClass().getResourceAsStream(file);
    }

    @Override
    public void start(Stage primaryStage) {

        try (InputStream hs = resourceStream("/hygdata_v3.csv"); InputStream ast = resourceStream("/asterisms.txt")) {
            BlackBodyColor.init();
            //££final Future<StarCatalogue> catalogue = ThreadManager.getIo().submit(() -> new StarCatalogue.Builder()
            //££        .loadFrom(hs, HygDatabaseLoader.INSTANCE).loadFrom(ast, AsterismLoader.INSTANCE).build());
            final StarCatalogue catalogue = new StarCatalogue.Builder()
                    .loadFrom(hs, HygDatabaseLoader.INSTANCE).loadFrom(ast, AsterismLoader.INSTANCE).build();

            final StereographicProjection proj = new StereographicProjection(HorizontalCoordinates.ofDeg(277, -23));

            //££final Future<ObservedSky> skyFuture = ThreadManager.getAstronomy().submit(() -> new ObservedSky(
            //££        ZonedDateTime.parse("2020-02-17T20:15:00+01:00"), GeographicCoordinates.ofDeg(6.57, 46.52), proj, catalogue.get()));
            //££final ObservedSky sky = skyFuture.get();
            final ObservedSky sky = new ObservedSky(ZonedDateTime.parse("2020-02-17T20:15:00+01:00"),
                    GeographicCoordinates.ofDeg(6.57, 46.52), proj, catalogue);

            final Canvas canvasFuture = new Canvas(800, 600);

            final PlanarTransformation transform = PlanarTransformation.ofDilatAndTrans(1300, 400, 300);

            final SkyCanvasPainter paint = new SkyCanvasPainter(canvasFuture);

            paint.clear();

            //##RigelLogger.getBackendLogger().info("Beginning Celestial object drawing");
            //££ThreadManager.getGui().execute(
                //££    () ->
                    //££{
                paint.drawAsterisms(sky, transform);
                paint.drawStars(sky, transform);
                paint.drawPlanets(sky, transform);
                paint.drawSun(sky, transform);
                paint.drawMoon(sky, transform);
                paint.drawHorizon(proj, transform);
                //££});

            ImageIO.write(SwingFXUtils.fromFXImage(canvasFuture.snapshot(null, null),
                    null), "png", new File("sky.png"));

            //££ThreadManager.getGui().shutdownNow();
            //££ThreadManager.getAstronomy().shutdownNow();
            //££ThreadManager.getIo().shutdown();
            //££ThreadManager.getLogger().shutdownNow();

        } //££catch (InterruptedException | ExecutionException | IOException e) {
        catch (IOException e) {
            e.printStackTrace();
        }
        Platform.exit();

    }
}
