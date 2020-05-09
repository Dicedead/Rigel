package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.astronomy.Orbit;
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
import javafx.scene.text.TextAlignment;

import java.util.EnumSet;
import java.util.Map;
import java.util.function.Function;
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

    private static final double CELEST_SIZE_COEFF = applyToAngle(ofDeg(0.5)) / 140;
    private static final double ORBIT_CIRCLE_SIZE = 3e-3;
    private static final double OCTANTS_ALT_OFFSET = -0.5;
    private static final Color ORBIT_COLOR = Color.CADETBLUE;
    private static final ClosedInterval CLIP_INTERVAL = ClosedInterval.of(-2, 5);
    private static final Color YELLOW_HALO = Color.YELLOW.deriveColor(1, 1, 1, 0.25);
    private static final HorizontalCoordinates PARALLEL = HorizontalCoordinates.ofDeg(0, 0);
    private static final Function<Star, Paint> STAR_COLOR = s -> BlackBodyColor.colorForTemperature(s.colorTemperature());

    private final Canvas canvas;
    private final GraphicsContext graphicsContext;

    /**
     * SkyCanvasPainter Constructor
     *
     * @param canvas (Canvas) canvas to be drawn on
     */
    public SkyCanvasPainter(Canvas canvas) {
        this.canvas = canvas;
        this.graphicsContext = this.canvas.getGraphicsContext2D();
    }

    /**
     * Resets the canvas to a black rectangle state of the same size
     */
    public void clear() {
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Draws objects with possible filters
     *
     * @param sky           (ObservedSky) current observed sky
     * @param transform     (PlanarTransformation) current transformation to the canvas
     * @param proj          (StereographicProjection) current projection to the 2D plane
     * @param objectsToDraw (EnumSet<DrawableObjects>) possible filters - taking advantage of EnumSet's inner order of
     *                      objects
     */
    public void drawMain(ObservedSky sky, PlanarTransformation transform, StereographicProjection proj,
                         EnumSet<DrawableObjects> objectsToDraw, Orbit<? extends CelestialObject> orbit,
                         int orbitUntil, int orbitStep) {

        clear();
        if (orbit != null) drawOrbit(orbit, sky, transform, orbitUntil, orbitStep);

        for (DrawableObjects toDraw : objectsToDraw)
            switch (toDraw) {
                case ASTERISMS:
                    drawAsterisms(sky, transform);
                    break;
                case STARS:
                    drawStars(sky, transform);
                    break;
                case PLANETS:
                    drawPlanets(sky, transform);
                    break;
                case SUN:
                    drawSun(sky, transform);
                    break;
                case MOON:
                    drawMoon(sky, transform);
                    break;
                case HORIZON:
                    drawHorizon(proj, transform);
                    break;
                default:
                    throw new IllegalStateException("SkyCanvasPainter: unknown drawable object type given.");
            }
    }


    /**
     * Draws predicted orbit of a celestial object with given parameters
     *
     * @param orbit     (Orbit<? extends CelestialObject>) a list of celestial object suppliers
     * @param sky       (ObservedSky) current observed sky
     * @param transform (PlanarTransformation) current transformation to the canvas
     * @param length    (int) show up to "length" circles representing the orbit
     * @param step      (int) show every "step" position computed (this is the resolution)
     */
    public void drawOrbit(Orbit<? extends CelestialObject> orbit, ObservedSky sky, PlanarTransformation transform,
                          int length, int step) {
        pipeline(sky.mapObjectToPosition(orbit.representatives(length, step), Function.identity()).entrySet().stream(),
                celest -> ORBIT_CIRCLE_SIZE, celest -> ORBIT_COLOR, transform);
    }

    /**
     * Draws blue lines representing asterisms in current sky
     *
     * @param sky       (ObservedSky) current sky
     * @param transform (PlanarTransformation) current transformation to the canvas
     */
    public void drawAsterisms(ObservedSky sky, PlanarTransformation transform) {
        graphicsContext.setStroke(Color.BLUE);
        graphicsContext.setLineWidth(1);
        sky.asterisms().forEach(
                asterism -> {
                    final CartesianCoordinates cartesStar0 = transform.apply(getCartesFromIndex(sky, asterism, 0));
                    asterismLineRecurr(cartesStar0, transform.apply(getCartesFromIndex(sky, asterism, 1)),
                            0, asterism, sky, transform);
                }
        );
    }

    /**
     * Places the stars in current sky, with radius depending of their magnitude
     *
     * @param sky       (ObservedSky) current sky
     * @param transform (PlanarTransformation) current transformation to the canvas
     */
    public void drawStars(ObservedSky sky, PlanarTransformation transform) {
        pipeline(sky.starsMap().entrySet().stream(), SkyCanvasPainter::apparentSize, STAR_COLOR,
                transform);
    }

    /**
     * Places the planets in current sky, with radius depending of their magnitude, in light grey
     *
     * @param sky       (ObservedSky) current sky
     * @param transform (PlanarTransformation) current transformation to the canvas
     */
    public void drawPlanets(ObservedSky sky, PlanarTransformation transform) {
        pipeline(sky.planetsMap().entrySet().stream(), SkyCanvasPainter::apparentSize, planet -> Color.LIGHTGRAY,
                transform);
    }

    /**
     * Draws the Sun in 3 layers in the current sky
     *
     * @param sky       (ObservedSky) current sky
     * @param transform (PlanarTransformation) current transformation to the canvas
     */
    public void drawSun(ObservedSky sky, PlanarTransformation transform) {
        final CartesianCoordinates transformedCoords = transform.apply(sky.sunPosition());

        if (isInCanvas(transformedCoords)) {
            double innerSize = transform.applyDistance(applyToAngle(sky.sun().angularSize()));
            drawCircle(YELLOW_HALO, transformedCoords, innerSize * 2.2);
            drawCircle(Color.YELLOW, transformedCoords, innerSize + 2);
            drawCircle(Color.WHITE, transformedCoords, innerSize);
        }
    }

    /**
     * Draws the Moon in the current sky in white
     *
     * @param sky       (ObservedSky) current sky
     * @param transform (PlanarTransformation) current transformation to the canvas
     */
    public void drawMoon(ObservedSky sky, PlanarTransformation transform) {
        pipeline(sky.moonMap().entrySet().stream(), moon -> applyToAngle(moon.angularSize()), moon -> Color.WHITE,
                transform);
    }

    /**
     * Draws the horizon and octant names in red
     *
     * @param projection (StereographicProjection) centered projection to the 2D plane
     * @param transform  (PlanarTransformation) current transformation to the canvas
     */
    public void drawHorizon(StereographicProjection projection, PlanarTransformation transform) {
        double size = transform.applyDistance(2 * projection.circleRadiusForParallel(PARALLEL));
        CartesianCoordinates transformedCenter = transform.apply(projection.circleCenterForParallel(PARALLEL));
        double halfSize = size / 2;

        synchronized (graphicsContext) {
            graphicsContext.setStroke(Color.RED);
            graphicsContext.setLineWidth(2);
            graphicsContext.strokeOval(transformedCenter.x() - halfSize, transformedCenter.y() - halfSize, size, size);
            graphicsContext.setFill(Color.RED);
            graphicsContext.setTextAlign(TextAlignment.CENTER);
            graphicsContext.setTextBaseline(VPos.TOP);
        }
        for (int i = 0; i < 8; ++i) {
            HorizontalCoordinates octantHorizCoords = HorizontalCoordinates.ofDeg(45 * i, OCTANTS_ALT_OFFSET);
            CartesianCoordinates octantTransCoords = transform.apply(projection.apply(octantHorizCoords));
            graphicsContext.fillText(octantHorizCoords.azOctantName("N", "E", "S", "O"),
                    octantTransCoords.x(), octantTransCoords.y());
        }
    }

    /**
     * Concatenation of operations, the last of which is the effective drawing
     *
     * @param positions      (Stream<Map.Entry<T, CartesianCoordinates>>) Stream or ParallelStream
     * @param radiusFunction (Function<T, Double>) how to compute radii for given stream of celestial objects
     * @param color          (Function<T, Paint>) how to find the color for given stream of celestial objects
     * @param transform      (PlanarTransformation) current transformation to the canvas
     * @param <T>            (extends CelestialObject)
     */
    private <T extends CelestialObject> void pipeline(Stream<Map.Entry<T, CartesianCoordinates>> positions,
            Function<T, Double> radiusFunction, Function<T, Paint> color, PlanarTransformation transform) {
        drawCelestial(checkInCanvas(applyTransform(positions, transform)), radiusFunction, color, transform);
        /*It's worth pointing out that positions is always a non parallel Stream in the first version of this program
         (ie pre step 12) but may become parallel in the case of drawStars for step 12.*/
    }

    /**
     * Applies transformation on celestial objects' positions
     *
     * @param positions (Stream<Map.Entry<T, CartesianCoordinates>>)
     * @param transform (PlanarTransformation) current transformation to the canvas
     * @param <T>       (extends CelestialObject)
     * @return (Stream<Map.Entry<T, CartesianCoordinates>>) transformed positions
     */
    private <T extends CelestialObject> Stream<Map.Entry<T, CartesianCoordinates>> applyTransform(
            Stream<Map.Entry<T, CartesianCoordinates>> positions, PlanarTransformation transform) {
        return positions.map(entry -> Map.entry(entry.getKey(), transform.apply(entry.getValue())));
    }

    /**
     * Filters out celestial objects that are not within canvas' bounds after transformation
     *
     * @param positions (Stream<Map.Entry<T, CartesianCoordinates>>)
     * @param <T>       (extends CelestialObject)
     * @return (Stream <Map.Entry<T, CartesianCoordinates>>) filtered positions
     */
    private <T extends CelestialObject> Stream<Map.Entry<T, CartesianCoordinates>> checkInCanvas(
            Stream<Map.Entry<T, CartesianCoordinates>> positions) {
        return positions.filter(entry -> isInCanvas(entry.getValue()));
    }

    /**
     * Draws circle shaped celestial objects on canvas
     *
     * @param positions      (Stream<Map.Entry<T, CartesianCoordinates>>) mapping celestial objects to their now transformed
     *                       coordinates
     * @param radiusFunction (Function<T, Double> radiusFunction) how to compute radii for given positions
     * @param color          (Function<T, Paint>) how to color given celestial objects
     * @param transform      (PlanarTransformation) current transformation to the canvas
     * @param <T>            (extends CelestialObject)
     */
    private <T extends CelestialObject> void drawCelestial(Stream<Map.Entry<T, CartesianCoordinates>> positions,
            Function<T, Double> radiusFunction, Function<T, Paint> color, PlanarTransformation transform) {
        positions.forEach(e ->
                drawCircle(color.apply(e.getKey()), e.getValue(), transform.applyDistance(radiusFunction.apply(e.getKey())))
        );
    }

    /**
     * Synchronized circle drawing helper method
     *
     * @param color        (Paint) color to apply
     * @param cartesCoords (CartesianCoordinates) transformed coordinates
     * @param size         (double) radius
     */
    private void drawCircle(Paint color, CartesianCoordinates cartesCoords, double size) {
        graphicsContext.setFill(color);
        final double halfSize = size / 2;
        graphicsContext.fillOval(cartesCoords.x() - halfSize, cartesCoords.y() - halfSize, size, size);
        //Used in drawSun and drawCelestial
    }

    /**
     * Recursive asterism drawing method: follows its list of stars from stars to finish, drawing lines on the way
     *
     * @param c1               (CartesianCoordinates) transformed
     * @param c2               (CartesianCoordinates) transformed
     * @param currentStartStar (int) index of the current starting star at starting point in asterism.stars()
     * @param asterism         (Asterism) current asterism being drawn
     * @param sky              (ObservedSky) current sky
     * @param transform        (PlanarTransformation) current transformation to the canvas
     */
    private void asterismLineRecurr(CartesianCoordinates c1, CartesianCoordinates c2,
                 int currentStartStar, Asterism asterism, ObservedSky sky, PlanarTransformation transform) {

        if (isInCanvas(c1) || isInCanvas(c2)) {
            graphicsContext.strokeLine(c1.x(), c1.y(), c2.x(), c2.y());
        }
        if (currentStartStar <= asterism.stars().size() - 2) {
            asterismLineRecurr(c2, transform.apply(getCartesFromIndex(sky, asterism, currentStartStar + 1)),
                    currentStartStar + 1, asterism, sky, transform);
        }
    }

    /**
     * Shortcut method for finding a Star's CartesianCoordinates through the Asterism it is a part of
     *
     * @param sky   (ObservedSky) current sky
     * @param aster (Asterism) the star's asterism
     * @param index (int) star's index in the asterism's list of stars
     * @return (CartesianCoordinates) star's position (non transformed)
     */
    private CartesianCoordinates getCartesFromIndex(ObservedSky sky, Asterism aster, int index) {
        return sky.starsMap().get(sky.stars().get(sky.asterismIndices(aster).get(index)));
    }

    /**
     * Shortcut method for testing if given coords are within canvas' current bounds
     *
     * @param coords (CartesianCoordinates coords)
     * @return (boolean)
     */
    private boolean isInCanvas(CartesianCoordinates coords) {
        return canvas.getBoundsInLocal().contains(coords.x(), coords.y());
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
