package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.logging.RigelLogger;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Transform;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.StrictMath.tan;

public class SkyCanvasPainter {

    final private static double COEFF = 2 * tan(Angle.ofDeg(0.5) / 4) / 140;
    final private Canvas canvas;
    final private GraphicsContext graphicsContext;
    final static private Function<Star, Paint> starColor = s -> BlackBodyColor.colorForTemperature(s.colorTemperature());

    public SkyCanvasPainter(Canvas canevas) {
        this.canvas = canevas;
        this.graphicsContext = canvas.getGraphicsContext2D();
        RigelLogger.getGuiLogger().info("Canvas initialised ready to draw");
    }

    public void clear() {
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void drawAsterisms(ObservedSky sky, StereographicProjection projection, Transform T) {
        sky.asterisms().forEach(
                asterism -> IntStream.range(0, sky.asterismIndices(asterism).size()).boxed().forEach(
                        i -> {
                            if ((i != sky.asterismIndices(asterism).size() - 1)) {
                                final Map<Star, CartesianCoordinates> duoMap = new HashMap<>();
                                duoMap.put(sky.stars().get(i),sky.starCartesianCoordinatesMap().get(sky.stars().get(i)));
                                duoMap.put(sky.stars().get(i+1),sky.starCartesianCoordinatesMap().get(sky.stars().get(i+1)));
                            }
                        }
                )
        );
    }

    public void drawStars(ObservedSky sky, StereographicProjection projection, Transform T) {
        pipeline(sky.starCartesianCoordinatesMap(), star -> projection.applyToAngle(celestialSize(star)), starColor, T);
    }

    public void drawPlanets(ObservedSky sky, StereographicProjection projection, Transform T) {
        pipeline(sky.planetCartesianCoordinatesMap(), planet -> projection.applyToAngle(celestialSize(planet)), planet -> Color.LIGHTGRAY, T);
    }

    //TODO: INSTEAD OF MAP.OF -> GETTERS FOR THE SUN AND MOON MAPS IN OBSERVEDSKY
    public void drawSun(ObservedSky sky, StereographicProjection projection, Transform T) {
        pipeline(Map.of(sky.sun(), sky.sunPosition()), sun -> projection.applyToAngle(sun.angularSize()), sun -> Color.WHITE, T);
    }

    public void drawMoon(ObservedSky sky, StereographicProjection projection, Transform T) {
        pipeline(Map.of(sky.moon(), sky.moonPosition()), moon -> projection.applyToAngle(moon.angularSize()), moon -> Color.WHITE, T);

    }

    public void drawHorizon(ObservedSky sky, StereographicProjection projection, Transform T) {
    }


    private <T extends CelestialObject> void pipeline(final Map<T, CartesianCoordinates> positions,
                                                      final Function<T, Double> diameter,
                                                      final Function<T, Paint> color,
                                                      final Transform t) {
        drawCelestial(applyTransform(mask(positions.entrySet().stream()), t), diameter, color);
    }

    private <T extends CelestialObject> Stream<Map.Entry<T, CartesianCoordinates>> applyTransform(final Stream<Map.Entry<T, CartesianCoordinates>> positions, Transform t) {
        return positions.map(e ->
        {
            final double x = e.getValue().x();
            final double y = e.getValue().y();

            return Map.entry(e.getKey(), CartesianCoordinates.of(t.getMxx() * x + t.getMxy() * y + t.getTx(),
                    t.getMyx() * x + t.getMyy() * y + t.getTy()));

        });
    }

    private <T extends CelestialObject> void drawCelestial(final Stream<Map.Entry<T, CartesianCoordinates>> positions, final Function<T, Double> diameter, final Function<T, Paint> color) {
        positions.forEach(e ->
        {
            Paint c = color.apply(e.getKey());
            graphicsContext.setFill(c);
            final double d = 2000 * diameter.apply(e.getKey());
            graphicsContext.fillOval(e.getValue().x(), e.getValue().y(), d, d);
        });
    }

    private <T extends CelestialObject> Stream<Map.Entry<T, CartesianCoordinates>> mask(final Stream<Map.Entry<T, CartesianCoordinates>> list) {
        return list.filter(e -> e.getValue().x() <= canvas.getWidth() && e.getValue().y() <= canvas.getHeight());

    }

    private static <T extends CelestialObject> Double celestialSize(final T s) {
        return (99 - 17 * ClosedInterval.of(-2, 5).clip(s.magnitude())) * COEFF;
    }
}

