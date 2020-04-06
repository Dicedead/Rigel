package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.logging.RigelLogger;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Transform;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Math.tan;

public class SkyCanvasPainter {

    private final static double COEFF = 2 * tan(Angle.ofDeg(0.5) / 4) / 140;
    private final static Color YELLOW_HALO = Color.YELLOW.deriveColor(0, 0, 0, -0.75);

    private final Canvas canvas;
    private final GraphicsContext graphicsContext;

    private final static Function<Star, Paint> STAR_COLOR = s -> BlackBodyColor.colorForTemperature(s.colorTemperature());
    private final Function<CartesianCoordinates, Boolean> isInCanvas;

    public SkyCanvasPainter(Canvas canvas) {
        this.canvas = canvas;
        this.graphicsContext = this.canvas.getGraphicsContext2D();
        this.isInCanvas = coord -> coord.x() <= canvas.getWidth() && coord.y() <= canvas.getHeight();
        RigelLogger.getGuiLogger().info("Canvas initialised ready to draw");
    }

    public void clear() {
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        graphicsContext.setStroke(Color.BLUE);
        graphicsContext.setLineWidth(1);
    }

    public boolean drawAsterisms(ObservedSky sky, Transform T) {
        sky.asterisms().forEach(
                asterism -> IntStream.range(0, sky.asterismIndices(asterism).size() - 1).boxed().forEach(
                        i -> asterismLine(getCartesFromIndex(sky, asterism, i),
                                getCartesFromIndex(sky, asterism, i + 1), T)));
        return true;
    }

    public boolean drawStars(ObservedSky sky, StereographicProjection projection, Transform T) {
        pipeline(sky.starCartesianCoordinatesMap(), star -> projection.applyToAngle(celestialSize(star)), STAR_COLOR, T);
        return true;
    }

    public boolean drawPlanets(ObservedSky sky, StereographicProjection projection, Transform T) {
        pipeline(sky.planetCartesianCoordinatesMap(), planet -> projection.applyToAngle(celestialSize(planet)), planet -> Color.LIGHTGRAY, T);
        return true;
    }

    public boolean drawSun(ObservedSky sky, StereographicProjection projection, Transform T) {
        pipeline(sky.sunMap(), sun -> projection.applyToAngle(sun.angularSize() * 2.2), sun -> YELLOW_HALO, T);
        pipeline(sky.sunMap(), sun -> projection.applyToAngle(sun.angularSize() + 2), sun -> Color.YELLOW, T);
        pipeline(sky.sunMap(), sun -> projection.applyToAngle(sun.angularSize()), sun -> Color.WHITE, T);
        return true;
    }

    public boolean drawMoon(ObservedSky sky, StereographicProjection projection, Transform T) {
        pipeline(sky.moonMap(), moon -> projection.applyToAngle(moon.angularSize()), moon -> Color.WHITE, T);
        return true;
    }

    public boolean drawHorizon(ObservedSky sky, StereographicProjection projection, Transform T) {
        //projection.circleCenterForParallel()
        return true;
    }


    private <T extends CelestialObject> void pipeline(final Map<T, CartesianCoordinates> positions,
                                                      final Function<T, Double> diameter,
                                                      final Function<T, Paint> color,
                                                      final Transform t) {

        drawCelestial(applyTransform(mask(positions.entrySet().stream()), t), diameter, color);
    }

    private <T extends CelestialObject> Stream<Map.Entry<T, CartesianCoordinates>> applyTransform(final Stream<Map.Entry<T, CartesianCoordinates>> positions, Transform t) {
        return positions.map(e -> Map.entry(e.getKey(), transformedCartesCoords(e.getValue(), t)));
    }

    private <T extends CelestialObject> void drawCelestial(final Stream<Map.Entry<T, CartesianCoordinates>> positions,
                                                           final Function<T, Double> diameter,
                                                           final Function<T, Paint> color) {
        positions.forEach(e ->
        {
            synchronized (graphicsContext) {
                final double size = 3500 * diameter.apply(e.getKey());
                graphicsContext.setFill(color.apply(e.getKey()));
                graphicsContext.fillOval(e.getValue().x()-size/2, e.getValue().y()-size/2, size, size);
            }
        });
    }

    private <T extends CelestialObject> Stream<Map.Entry<T, CartesianCoordinates>> mask(final Stream<Map.Entry<T, CartesianCoordinates>> list) {
        return list.filter(e -> isInCanvas.apply(e.getValue()));
    }

    //TODO: can be redone with beginPath, moveTo, lineTo, stroke - but would it be better?
    private void asterismLine(final CartesianCoordinates c1, final CartesianCoordinates c2, final Transform t) {
        if (isInCanvas.apply(c1) || isInCanvas.apply(c2)) {
            final CartesianCoordinates c1S = transformedCartesCoords(c1, t);
            final CartesianCoordinates c2S = transformedCartesCoords(c2, t);
            graphicsContext.strokeLine(c1S.x(), c1S.y(), c2S.x(), c2S.y());
        }
    }

    private CartesianCoordinates getCartesFromIndex(ObservedSky sky, Asterism aster, int index) {
        return sky.starCartesianCoordinatesMap().get(sky.stars().get(sky.asterismIndices(aster).get(index)));
    }

    private static <T extends CelestialObject> Double celestialSize(final T s) {
        return (99 - 17 * ClosedInterval.of(-2, 5).clip(s.magnitude())) * COEFF;
    }

    private static CartesianCoordinates transformedCartesCoords(final CartesianCoordinates cartesCoord, final Transform t) {
        return CartesianCoordinates.of(t.getMxx() * cartesCoord.x() + t.getMxy() * cartesCoord.y() + t.getTx(),
                t.getMyx() * cartesCoord.x() + t.getMyy() * cartesCoord.y() + t.getTy());
    }
}

