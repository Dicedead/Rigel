package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.coordinates.PlanarTransformation;
import ch.epfl.rigel.math.ClosedInterval;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static ch.epfl.rigel.coordinates.StereographicProjection.applyToAngle;
import static ch.epfl.rigel.math.Angle.ofDeg;

/**
 * Paints an ObservedSky onto the 2D plane
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class SkyCanvasPainter {

    private final static double CELEST_SIZE_COEFF = applyToAngle(ofDeg(0.5)) / 280;
    private final static ClosedInterval CLIP_INTERVAL = ClosedInterval.of(-2, 5);
    private final static Color YELLOW_HALO = Color.YELLOW.deriveColor(1, 1, 1, 0.25);
    private final static HorizontalCoordinates PARALLEL = HorizontalCoordinates.ofDeg(0, 0);

    private final Canvas canvas;
    private final GraphicsContext graphicsContext;

    private final static Function<Star, Paint> STAR_COLOR = s -> BlackBodyColor.colorForTemperature(s.colorTemperature());
    private final Function<CartesianCoordinates, Boolean> isInCanvas;
    private final Function<CartesianCoordinates, Boolean> isInCanvasTransformed;
    private final PlanarTransformation transform;

    /**
     * SkyCanvasPainter Constructor
     *
     * @param canvas (Canvas) canvas to be drawn on
     * @param transform (PlanarTransformation) transformation to be applied
     */
    public SkyCanvasPainter(final Canvas canvas, final PlanarTransformation transform) {
        this.canvas = canvas;
        this.graphicsContext = this.canvas.getGraphicsContext2D();
        this.transform = transform;
        final CartesianCoordinates invertedCanvasSizes = transform.invert().apply(canvas.getWidth(), canvas.getHeight());
        final double absInvertedX = Math.abs(invertedCanvasSizes.x());
        final double absInvertedY = Math.abs(invertedCanvasSizes.y());
        this.isInCanvas = coord -> Math.abs(coord.x()) <= absInvertedX && Math.abs(coord.y()) <= absInvertedY;
        //getInBounds creates Bounds object, unneeded.
        this.isInCanvasTransformed = coord -> coord.x() <= canvas.getWidth() && coord.y() <= canvas.getHeight();
    }

    /**
     * Resets the canvas to a black rectangle state of the same size
     */
    public void clear() {
        synchronized (graphicsContext) {
            graphicsContext.setFill(Color.BLACK);
            graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        }
    }

    /**
     * Draws blue lines representing asterisms in current sky
     *
     * @param sky (ObservedSky) current sky
     */
    public void drawAsterisms(ObservedSky sky) {
        synchronized (graphicsContext) {
            graphicsContext.setStroke(Color.BLUE);
            graphicsContext.setLineWidth(1);

            sky.asterisms().forEach(
                asterism -> {
                    graphicsContext.beginPath();
                    final CartesianCoordinates cartesStar0 = transform.apply(getCartesFromIndex(sky, asterism, 0));
                    graphicsContext.moveTo(cartesStar0.x(), cartesStar0.y());
                    asterismLineRecurr(cartesStar0, transform.apply(getCartesFromIndex(sky, asterism, 1)),
                            0, asterism, sky);
                    graphicsContext.closePath();
                    }
            );
        }
    }

    /**
     * Places the stars in current sky, with radius depending of their magnitude
     *
     * @param sky (ObservedSky) current sky
     */
    public void drawStars(final ObservedSky sky) {
        pipeline(sky.starsMap().entrySet().parallelStream(), SkyCanvasPainter::apparentSize, STAR_COLOR);
        //Once again, parallelStream shortened the initialisation time upon testing.
    }

    /**
     * Places the planets in current sky, with radius depending of their magnitude, in light grey
     *
     * @param sky (ObservedSky) current sky
     */
    public void drawPlanets(final ObservedSky sky) {
        pipeline(sky.planetsMap().entrySet().stream(), SkyCanvasPainter::apparentSize, planet -> Color.LIGHTGRAY);
    }

    /**
     * Draws the Sun in 3 layers in the current sky
     *
     * @param sky (ObservedSky) current sky
     */
    public void drawSun(final ObservedSky sky) {
        if (isInCanvas.apply(sky.sunPosition())) {
            final CartesianCoordinates transformedCoords = transform.apply(sky.sunPosition());
            final double innerSize = transform.applyDistance(applyToAngle(sky.sun().angularSize())/2);
            drawCircle(YELLOW_HALO, transformedCoords, innerSize * 2.2);
            drawCircle(Color.YELLOW, transformedCoords, innerSize + 2);
            drawCircle(Color.WHITE, transformedCoords, innerSize);
        }
    }

    /**
     * Draws the Moon in the current sky in white
     *
     * @param sky (ObservedSky) current sky
     */
    public void drawMoon(final ObservedSky sky) {
        pipeline(sky.moonMap().entrySet().stream(), moon -> applyToAngle(moon.angularSize()) / 2, moon -> Color.WHITE);
    }

    /**
     * Draws the horizon and octant names in red
     *
     * @param projection (StereographicProjection) centered projection to the 2D plane
     */
    public void drawHorizon(final StereographicProjection projection) {
        final double size = transform.applyDistance(projection.circleRadiusForParallel(PARALLEL));
        final CartesianCoordinates transformedCenter = transform.apply(projection.circleCenterForParallel(PARALLEL));

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
                    final CartesianCoordinates octantTransCoords = transform.apply(projection.apply(octantHorizCoords));
                    graphicsContext.fillText(octantHorizCoords.azOctantName("N", "E", "S", "O"),
                            octantTransCoords.x(), octantTransCoords.y());
                }
        );
    }

    /**
     * Concatenation of operations, the last of which is the effective drawing
     *
     * @param positions (Stream<Map.Entry<T, CartesianCoordinates>>) Stream or ParallelStream
     * @param radiusFunction (Function<T, Double>) how to compute radii for given stream of celestial objects
     * @param color (Function<T, Paint>) how to find the color for given stream of celestial objects
     * @param <T> (extends CelestialObject)
     */
    private <T extends CelestialObject> void pipeline(final Stream<Map.Entry<T, CartesianCoordinates>> positions,
    final Function<T, Double> radiusFunction, final Function<T, Paint> color) {
        drawCelestial(applyTransform(checkInCanvas(positions)), radiusFunction, color);
    }

    /**
     * Applies transformation on celestial objects' positions
     *
     * @param positions (Stream<Map.Entry<T, CartesianCoordinates>>)
     * @param <T> (extends CelestialObject)
     * @return (Stream<Map.Entry<T, CartesianCoordinates>>) transformed positions
     */
    private <T extends CelestialObject> Stream<Map.Entry<T, CartesianCoordinates>> applyTransform(
    final Stream<Map.Entry<T, CartesianCoordinates>> positions) {
        return positions.map(entry -> Map.entry(entry.getKey(), transform.apply(entry.getValue())));
    }

    /**
     * Filters out celestial objects that are not within canvas' bounds after transformation
     *
     * @param positions (Stream<Map.Entry<T, CartesianCoordinates>>)
     * @param <T> (extends CelestialObject)
     * @return (Stream<Map.Entry<T, CartesianCoordinates>>) filtered positions
     */
    private <T extends CelestialObject> Stream<Map.Entry<T, CartesianCoordinates>> checkInCanvas(
            final Stream<Map.Entry<T, CartesianCoordinates>> positions) {
        return positions.filter(entry -> isInCanvas.apply(entry.getValue()));
    }

    /**
     * Draws circle shaped celestial objects on canvas
     *
     * @param positions (Stream<Map.Entry<T, CartesianCoordinates>>) mapping celestial objects to their now transformed
     *                  coordinates
     * @param radiusFunction (Function<T, Double> radiusFunction) how to compute radii for given positions
     * @param color (Function<T, Paint>) how to color given celestial objects
     * @param <T> (extends CelestialObject)
     */
    private <T extends CelestialObject> void drawCelestial(final Stream<Map.Entry<T, CartesianCoordinates>> positions,
    final Function<T, Double> radiusFunction, final Function<T, Paint> color) {
        positions.forEach(e ->
        {
            final double size = transform.applyDistance(radiusFunction.apply(e.getKey()));
            drawCircle(color.apply(e.getKey()), e.getValue(), size);
        });
    }

    /**
     * Synchronized circle drawing helper method
     *
     * @param color (Paint) color to apply
     * @param cartesCoords (CartesianCoordinates) transformed coordinates
     * @param size (double) radius
     */
    private synchronized void drawCircle(Paint color, CartesianCoordinates cartesCoords, double size) {
            graphicsContext.setFill(color);
            graphicsContext.fillOval(cartesCoords.x() - size / 2, cartesCoords.y() - size / 2, size, size);
        //Used in drawSun and drawCelestial
    }

    /**
     * Recursive asterism drawing method: follows its list of stars from stars to finish, drawing lines on the way
     *
     * @param c1 (CartesianCoordinates) transformed
     * @param c2 (CartesianCoordinates) transformed
     * @param currentStartStar (int) index of the current starting star at starting point in asterism.stars()
     * @param asterism (Asterism) current asterism being drawn
     * @param sky (ObservedSky) current sky
     */
    private void asterismLineRecurr(final CartesianCoordinates c1, final CartesianCoordinates c2,
    final int currentStartStar, final Asterism asterism, final ObservedSky sky) {

        graphicsContext.lineTo(c2.x(), c2.y());
        if (isInCanvasTransformed.apply(c1) || isInCanvasTransformed.apply(c2)) {
            graphicsContext.stroke();
        }
        if (currentStartStar <= asterism.stars().size() - 2) {
            asterismLineRecurr(c2, transform.apply(getCartesFromIndex(sky, asterism, currentStartStar + 1)),
                    currentStartStar + 1, asterism, sky);
        }
    }

    /**
     * Shortcut method for finding a Star's CartesianCoordinates through the Asterism it is a part of
     *
     * @param sky (ObservedSky) current sky
     * @param aster (Asterism) the star's asterism
     * @param index (int) star's index in the asterism's list of stars
     * @return (CartesianCoordinates) star's position (non transformed)
     */
    private CartesianCoordinates getCartesFromIndex(ObservedSky sky, Asterism aster, int index) {
        return sky.starsMap().get(sky.stars().get(sky.asterismIndices(aster).get(index)));
    }

    /**
     * Computes planets' and stars' apparent radii using their magnitude
     *
     * @param celestObj (CelestialObject)
     * @return (double) apparent radius of celestObj on screen
     */
    private static double apparentSize(final CelestialObject celestObj) {
        return (99 - 17 * CLIP_INTERVAL.clip(celestObj.magnitude())) * CELEST_SIZE_COEFF;
    }
}
