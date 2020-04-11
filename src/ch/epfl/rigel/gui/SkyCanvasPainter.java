package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.logging.RigelLogger;
import ch.epfl.rigel.math.ClosedInterval;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Transform;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static ch.epfl.rigel.coordinates.StereographicProjection.applyToAngle;
import static ch.epfl.rigel.math.Angle.ofDeg;

public class SkyCanvasPainter {

    private final static double CELEST_SIZE_COEFF = applyToAngle(ofDeg(0.5)) / 280;
    private final static ClosedInterval CLIP_INTERVAL = ClosedInterval.of(-2, 5);
    private final static Color YELLOW_HALO = Color.YELLOW.deriveColor(1, 1, 1, 0.25);
    private final static HorizontalCoordinates PARALLEL = HorizontalCoordinates.ofDeg(0, 0);

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
    }

    public boolean drawAsterisms(ObservedSky sky, Transform transform) {
        synchronized (graphicsContext) {
            graphicsContext.setStroke(Color.BLUE);
            graphicsContext.setLineWidth(1);

            sky.asterisms().forEach(
                    asterism -> {
                        graphicsContext.beginPath();
                        final CartesianCoordinates cartesStar0 =
                                transformedCartesCoords(getCartesFromIndex(sky, asterism, 0), transform);
                        graphicsContext.moveTo(cartesStar0.x(), cartesStar0.y());
                        asterismLineRecurr(cartesStar0, transformedCartesCoords(getCartesFromIndex(sky, asterism, 1), transform),
                                transform, 0, asterism.stars().size() - 1, asterism, sky);

                       }
            );
        }
        return true;
    }

    public boolean drawStars(final ObservedSky sky, final Transform transform) {
        pipeline(sky.starsMap(), SkyCanvasPainter::celestialSize, STAR_COLOR, transform);
        return true;
    }

    public boolean drawPlanets(final ObservedSky sky, final Transform transform) {
        pipeline(sky.planetsMap(), SkyCanvasPainter::celestialSize, planet -> Color.LIGHTGRAY, transform);
        return true;
    }

    public boolean drawSun(final ObservedSky sky, final Transform transform) {
        if (isInCanvas.apply(sky.sunPosition())) {
            final CartesianCoordinates transformedCoords = transformedCartesCoords(sky.sunPosition(), transform);
            final double innerSize = computeScreenRadius(applyToAngle(sky.sun().angularSize()) / 2, transform);
            drawOval(YELLOW_HALO, transformedCoords, innerSize * 2.2);
            drawOval(Color.YELLOW, transformedCoords, innerSize + 2);
            drawOval(Color.WHITE, transformedCoords, innerSize);
        }
        return true;
    }

    public boolean drawMoon(final ObservedSky sky, final Transform transform) {
        pipeline(sky.moonMap(), moon -> moon.angularSize() / 2, moon -> Color.WHITE, transform);
        return true;
    }

    public boolean drawHorizon(final StereographicProjection projection, final Transform T) {
        final double size = computeScreenRadius(projection.circleRadiusForParallel(PARALLEL), T);
        final CartesianCoordinates transformedCenter = transformedCartesCoords(projection.circleCenterForParallel(PARALLEL), T);

        synchronized (graphicsContext) {

            graphicsContext.setStroke(Color.RED);
            graphicsContext.setLineWidth(2);
            graphicsContext.strokeOval(transformedCenter.x() - size / 2, transformedCenter.y() - size / 2, size, size);
            graphicsContext.setFill(Color.RED);
            graphicsContext.setTextBaseline(VPos.TOP);
        }
        IntStream.range(0, 8).boxed().forEach(
                i -> {
                    final HorizontalCoordinates octantHorizCoords = HorizontalCoordinates.ofDeg(45 * i, -0.5);
                    final CartesianCoordinates octantTransCoords = transformedCartesCoords(projection.apply(octantHorizCoords), T);
                    graphicsContext.fillText(octantHorizCoords.azOctantName("N", "E", "S", "O"),
                            octantTransCoords.x(), octantTransCoords.y());
                }
        );
        return true;
    }


    private <T extends CelestialObject> void pipeline(final Map<T, CartesianCoordinates> positions,
                                                      final Function<T, Double> radiusFunction,
                                                      final Function<T, Paint> color,
                                                      final Transform transform) {

        drawCelestial(applyTransform(mask(positions.entrySet().stream()), transform), transform, radiusFunction, color);
    }

    private <E extends CelestialObject> Stream<Map.Entry<E, CartesianCoordinates>> applyTransform(
            final Stream<Map.Entry<E, CartesianCoordinates>> positions, Transform transform) {
        return positions.map(e -> Map.entry(e.getKey(), transformedCartesCoords(e.getValue(), transform)));
    }

    private <T extends CelestialObject> void drawCelestial(final Stream<Map.Entry<T, CartesianCoordinates>> positions,
                                                           final Transform transform,
                                                           final Function<T, Double> radiusFunction,
                                                           final Function<T, Paint> color) {
        positions.forEach(e ->
        {
            final double size = computeScreenRadius(radiusFunction.apply(e.getKey()), transform);
            drawOval(color.apply(e.getKey()), e.getValue(), size);
        });
    }

    private void drawOval(Paint color, CartesianCoordinates cartesCoords, double size) {
        synchronized (graphicsContext) {
            graphicsContext.setFill(color);
            graphicsContext.fillOval(cartesCoords.x() - size / 2, cartesCoords.y() - size / 2, size, size);
        }
    }

    private double computeScreenRadius(final double initialRadius, final Transform T) {
        final Point2D transformedPoint = T.deltaTransform(initialRadius, initialRadius);
        return Math.abs(transformedPoint.getX()) + Math.abs(transformedPoint.getY());
    }

    private <T extends CelestialObject> Stream<Map.Entry<T, CartesianCoordinates>> mask(
            final Stream<Map.Entry<T, CartesianCoordinates>> cartesStream) {
        return cartesStream.filter(e -> isInCanvas.apply(e.getValue()));
    }

    private void asterismLineRecurr(final CartesianCoordinates c1, final CartesianCoordinates c2, final Transform transform,
                                    final int currentStartStar, final int lastStar, final Asterism asterism, final ObservedSky sky) {

        graphicsContext.lineTo(c2.x(), c2.y());
        if (isInCanvas.apply(c1) || isInCanvas.apply(c2)) {
            graphicsContext.stroke();
        }
        if (currentStartStar < lastStar) {
            asterismLineRecurr(c2, transformedCartesCoords(getCartesFromIndex(sky, asterism, currentStartStar + 1)
                    , transform), transform, currentStartStar + 1, lastStar, asterism, sky);
        }
    }

    private CartesianCoordinates getCartesFromIndex(ObservedSky sky, Asterism aster, int index) {
        return sky.starsMap().get(sky.stars().get(sky.asterismIndices(aster).get(index)));
    }

    private static <T extends CelestialObject> Double celestialSize(final T s) {
        return (99 - 17 * CLIP_INTERVAL.clip(s.magnitude())) * CELEST_SIZE_COEFF;
    }

    private static CartesianCoordinates transformedCartesCoords(final CartesianCoordinates cartesCoord, final Transform t) {
        return CartesianCoordinates.of(t.getMxx() * cartesCoord.x() + t.getMxy() * cartesCoord.y() + t.getTx(),
                t.getMyx() * cartesCoord.x() + t.getMyy() * cartesCoord.y() + t.getTy());
    }
}
