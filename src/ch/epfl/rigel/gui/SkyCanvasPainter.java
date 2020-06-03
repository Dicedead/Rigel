package ch.epfl.rigel.gui;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.astronomy.Orbit;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.coordinates.PlanarTransformation;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;
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

    private static final HorizontalCoordinates EQUATOR = HorizontalCoordinates.ofDeg(0, 0);

    private static final int ASTERISMS_LINE_WIDTH   = 1;
    private static final int HORIZON_LINE_WIDTH     = 2;
    private static final double GRID_LINE_WIDTH     = 0.5;
    private static final double CELEST_SIZE_COEFF   = applyToAngle(ofDeg(0.5)) / 140;
    private static final double OCTANTS_ALT_OFFSET  = -0.5;
    private static final double ORBIT_CIRCLE_SIZE   = 3e-3;
    private static final int AZIMUTH_DEGREES        = 180;
    private static final int ALTITUDE_DEGREES       = 360;

    private static final ClosedInterval CLIP_INTERVAL       = ClosedInterval.of(-2, 5);
    private static final RightOpenInterval INTERVAL_SYM180  = RightOpenInterval.symmetric(180);

    private static final Color SUN_COLOR_1_HALO = Color.YELLOW.deriveColor(1, 1, 1,
            0.25);
    private static final Color SUN_COLOR_2_YELLOW   = Color.YELLOW;
    private static final Color SUN_COLOR_3_WHITE    = Color.WHITE;

    private static final Function<Star, Paint> STAR_COLOR       = s -> BlackBodyColor.colorForTemperature(s.colorTemperature());
    private static final Function<Planet, Paint> PLANET_COLOR   = planet -> Color.LIGHTGRAY;
    private static final Function<Moon, Paint> MOON_COLOR       = moon -> Color.WHITE;

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
     * @param objectsToDraw (EnumSet<DrawableObjects>) possible filters
     * @param orbit         (Orbit<? extends CelestialObject>) possible orbit to draw
     * @param orbitUntil    (int) max number of representatives to draw
     * @param orbitStep     (int) draw every orbitStep representative
     * @param astColor      (Color) desired asterisms color
     * @param horColor      (Color) desired horizon color
     * @param orbitColor    (Color) desired orbit color
     * @param gridColor     (Color) desired grid color
     * @param gridSpaceDeg  (int) in degrees: the angular spacing between grid lines
     */
    public void drawMain(ObservedSky sky, PlanarTransformation transform, StereographicProjection proj,
                         EnumSet<DrawableObjects> objectsToDraw, Orbit<? extends CelestialObject> orbit,
                         int orbitUntil, int orbitStep, Color astColor, Color horColor, Color orbitColor,
                         Color gridColor, int gridSpaceDeg) {

        clear();
        for (DrawableObjects toDraw : objectsToDraw)
            switch (toDraw) {
                case ORBIT:
                    if (orbit != null) drawOrbit(orbit, sky, transform, orbitUntil, orbitStep, orbitColor);
                    break;
                case ASTERISMS:
                    drawAsterisms(sky, transform, astColor);
                    break;
                case GRID:
                    drawGrid(proj, transform, gridColor, gridSpaceDeg);
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
                    drawHorizon(proj, transform, horColor);
                    break;
                default:
                    throw new IllegalStateException("SkyCanvasPainter: unknown drawable object type given.");
            }
    }

    /**
     * Draws the horizontal coordinates grid
     *
     * @param projection (StereographicProjection) current projection
     * @param transform  (PlanarTransformation) current transformation to the canvas
     * @param gridColor  (Color) current grid color
     * @param spacingDeg (int) current spacing between parallels/meridians, in deg
     * @throws IllegalArgumentException if spacing deg does not divide 360 or does not divide 90
     */
    public void drawGrid(StereographicProjection projection, PlanarTransformation transform,
                         Color gridColor, int spacingDeg) {
        Preconditions.checkArgument(360 % spacingDeg == 0 && 90 % spacingDeg == 0,
                "SkyCanvasPainter.drawGrid: given grid spacing does not divide 360 and 90.");

        HorizontalCoordinates currHorizCoords;
        final int maxLength =  AZIMUTH_DEGREES / spacingDeg;
        for (int i = 0; i < maxLength; ++i) {
            currHorizCoords = HorizontalCoordinates.ofDeg(0, INTERVAL_SYM180.reduce(i * spacingDeg));
            drawStrokeCircle(transform.apply(projection.circleCenterForParallel(currHorizCoords)),
                    transform.applyDistance(2 * projection.circleRadiusForParallel(currHorizCoords)),
                    gridColor, GRID_LINE_WIDTH);
        }
        final int maxLength2 = ALTITUDE_DEGREES / spacingDeg;
        for (int i = 0; i < maxLength2; ++i) {
            currHorizCoords = HorizontalCoordinates.ofDeg(i * spacingDeg, 0);
            drawStrokeCircle(transform.apply(projection.circleCenterForMeridian(currHorizCoords)),
                    transform.applyDistance(2 * projection.circleRadiusForMeridian(currHorizCoords)),
                    gridColor, GRID_LINE_WIDTH);
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
     * @param orbColor  (Color) current orbit color
     */
    public void drawOrbit(Orbit<? extends CelestialObject> orbit, ObservedSky sky, PlanarTransformation transform,
                          int length, int step, Color orbColor) {
        pipeline(sky.mapObjectToPosition(orbit.representatives(length, step), Function.identity()).entrySet().stream(),
                orb -> ORBIT_CIRCLE_SIZE, orb -> orbColor, transform);
    }

    /**
     * Draws blue lines representing asterisms in current sky
     * We've separated asterism drawing from star drawing in view of step 12 where the possibility of not drawing the
     * stars (for example) is given in the settings menu.
     *
     * @param sky       (ObservedSky) current sky
     * @param transform (PlanarTransformation) current transformation to the canvas
     * @param astColor  (Color) current asterism color
     */
    public void drawAsterisms(ObservedSky sky, PlanarTransformation transform, Color astColor) {
        graphicsContext.setStroke(astColor);
        graphicsContext.setLineWidth(ASTERISMS_LINE_WIDTH);
        sky.asterisms().forEach(
                asterism -> {
                    final CartesianCoordinates mapOfStar0 = transform.apply(getCartesFromIndex(sky, asterism, 0));

                    asterismLineRecurr(mapOfStar0,
                            transform.apply(getCartesFromIndex(sky, asterism, 1)),
                            0,
                            asterism,
                            sky,
                            transform,
                            isInCanvas(mapOfStar0));
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
        pipeline(sky.planetsMap().entrySet().stream(), SkyCanvasPainter::apparentSize, PLANET_COLOR,
                transform);
    }

    /**
     * Draws the Sun in 3 layers in the current sky
     *
     * @param sky       (ObservedSky) current sky
     * @param transform (PlanarTransformation) current transformation to the canvas
     */
    public void drawSun(ObservedSky sky, PlanarTransformation transform) {
        CartesianCoordinates transformedCoords = transform.apply(sky.sunPosition());

        if (isInCanvas(transformedCoords)) {
            double innerSize = transform.applyDistance(applyToAngle(sky.sun().angularSize()));
            drawCircle(SUN_COLOR_1_HALO, transformedCoords, innerSize * 2.2);
            drawCircle(SUN_COLOR_2_YELLOW, transformedCoords, innerSize + 2);
            drawCircle(SUN_COLOR_3_WHITE, transformedCoords, innerSize);
        }
    }

    /**
     * Draws the Moon in the current sky in white
     *
     * @param sky       (ObservedSky) current sky
     * @param transform (PlanarTransformation) current transformation to the canvas
     */
    public void drawMoon(ObservedSky sky, PlanarTransformation transform) {
        pipeline(sky.moonMap().entrySet().stream(), moon -> applyToAngle(moon.angularSize()), MOON_COLOR,
                transform);
    }

    /**
     * Draws the horizon and octant names in red
     *
     * @param projection (StereographicProjection) centered projection to the 2D plane
     * @param transform  (PlanarTransformation) current transformation to the canvas
     * @param horColor   (Color) current horizon color
     */
    public void drawHorizon(StereographicProjection projection, PlanarTransformation transform, Color horColor) {
        drawStrokeCircle(transform.apply(projection.circleCenterForParallel(EQUATOR)),
                transform.applyDistance(2 * projection.circleRadiusForParallel(EQUATOR)),
                horColor,
                HORIZON_LINE_WIDTH);

        graphicsContext.setFill(horColor);
        graphicsContext.setTextAlign(TextAlignment.CENTER);
        graphicsContext.setTextBaseline(VPos.TOP);
        for (int i = 0; i < 8; ++i)
        {
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
     * Circle drawing helper method
     *
     * @param color        (Paint) color to apply
     * @param cartesCoords (CartesianCoordinates) transformed coordinates
     * @param size         (double) radius
     */
    private void drawCircle(Paint color, CartesianCoordinates cartesCoords, double size) {
        graphicsContext.setFill(color);
         double halfSize = size / 2;
        graphicsContext.fillOval(cartesCoords.x() - halfSize, cartesCoords.y() - halfSize, size, size);
        //Used in drawSun and drawCelestial
    }

    /**
     * Stroke circle drawing method
     *
     * @param transformedCenter (CartesianCoordinates) center of the circle
     * @param radius            (double) circle's radius
     * @param color             (Color) circle's color
     * @param width             (double) circle's stroke's width
     */
    private void drawStrokeCircle(CartesianCoordinates transformedCenter, double radius, Color color, double width) {
        double halfRadius = radius / 2;
        graphicsContext.setStroke(color);
        graphicsContext.setLineWidth(width);
        graphicsContext.strokeOval(transformedCenter.x() - halfRadius, transformedCenter.y() - halfRadius,
                radius, radius);
        //used in drawGrid and drawHorizon
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
                                    int currentStartStar, Asterism asterism, ObservedSky sky, PlanarTransformation transform,
                                    boolean c1InCanvas) {

        boolean c2InCanvas = isInCanvas(c2);
        if (c1InCanvas || isInCanvas(c2)) {
            graphicsContext.strokeLine(c1.x(), c1.y(), c2.x(), c2.y());
        }

        if (currentStartStar <= asterism.stars().size() - 2) {
            asterismLineRecurr(c2, transform.apply(getCartesFromIndex(sky, asterism, currentStartStar + 1)),
                    currentStartStar + 1, asterism, sky, transform, c2InCanvas);
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