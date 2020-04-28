package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.PlanarTransformation;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;

/**
 * Draws the sky on canvas continuously
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class SkyCanvasManager {

    private static final int MAX_CANVAS_DISTANCE = 10;
    private static final RightOpenInterval AZDEG_INTERVAL = RightOpenInterval.of(0,360);
    private static final ClosedInterval ALTDEG_INTERVAL = ClosedInterval.of(5, 90);

    private final StarCatalogue catalogue;
    private final DateTimeBean dtBean;
    private final ViewingParametersBean viewBean;
    private final ObserverLocationBean obsLocBean;
    private final Canvas canvas;
    private final SkyCanvasPainter painter;

    private final ObjectBinding<StereographicProjection> projection;
    private final ObjectBinding<PlanarTransformation> planeToCanvas;
    private final ObjectBinding<PlanarTransformation> inversePlaneToCanvas;
    private final ObjectBinding<ObservedSky> observedSky;
    private final ObjectBinding<HorizontalCoordinates> mouseHorizontalPosition;
    private final DoubleBinding maxDistanceCanvasToCartesian;

    private final ObjectBinding<CelestialObject> objectUnderMouse;
    private final DoubleBinding mouseAzDeg;
    private final DoubleBinding mouseAltDeg;

    private final ObjectProperty<CartesianCoordinates> mousePosition = new SimpleObjectProperty<>();

    public SkyCanvasManager(StarCatalogue catalogue, DateTimeBean dtBean,
           ObserverLocationBean obsLocBean, ViewingParametersBean viewBean) {

        this.catalogue = catalogue;
        this.dtBean = dtBean;
        this.viewBean = viewBean;
        this.obsLocBean = obsLocBean;

        canvas = new Canvas();
        painter = new SkyCanvasPainter(canvas);

        //CREATING VARIOUS ASTRONOMICAL AND MATHEMATICAL BINDINGS
        projection = Bindings.createObjectBinding(
                () -> new StereographicProjection(viewBean.getCenter()),
                viewBean.centerProperty());

        planeToCanvas = Bindings.createObjectBinding(
                () -> PlanarTransformation.ofDilatAndTrans(
                        canvas.getWidth()/StereographicProjection.applyToAngle(
                                Angle.ofDeg(viewBean.getFieldOfViewDeg())),
                        canvas.getWidth()/2, canvas.getHeight()/2),
                canvas.widthProperty(), canvas.heightProperty(), viewBean.fieldOfViewDegProperty());

        inversePlaneToCanvas = Bindings.createObjectBinding(() -> planeToCanvas.get().invert(), planeToCanvas);

        observedSky = Bindings.createObjectBinding(
                () -> new ObservedSky(dtBean.getZonedDateTime(), obsLocBean.getCoords(), projection.get(),catalogue),
                dtBean.zdtProperty(), obsLocBean.coordsProperty(), projection);

        //TAKING CARE OF MOUSE'S POSITION AND USER INTERACTION
        mouseHorizontalPosition = Bindings.createObjectBinding(
                () -> projection.get().inverseApply(inversePlaneToCanvas.get().apply(mousePosition.get())),
                projection, inversePlaneToCanvas, mousePosition);

        mouseAzDeg = Bindings.createDoubleBinding(() -> mouseHorizontalPosition.get().azDeg(), mouseHorizontalPosition);

        mouseAltDeg = Bindings.createDoubleBinding(() -> mouseHorizontalPosition.get().altDeg(), mouseHorizontalPosition);

        maxDistanceCanvasToCartesian = Bindings.createDoubleBinding(
                () -> inversePlaneToCanvas.get().applyDistance(MAX_CANVAS_DISTANCE),
                inversePlaneToCanvas);

        objectUnderMouse = Bindings.createObjectBinding(
                () -> observedSky.get().objectClosestTo(mousePosition.get(), maxDistanceCanvasToCartesian.get()).orElse(null),
                observedSky, mousePosition, maxDistanceCanvasToCartesian);

        canvas.setOnMousePressed(mouse -> {
            if (mouse.isPrimaryButtonDown()) canvas.requestFocus();
        });

        canvas.setOnMouseMoved(mouse -> mousePosition.set(inversePlaneToCanvas.get().apply(mouse.getX(), mouse.getY())));

        canvas.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case LEFT: modifyView(-10, 0, viewBean.getCenter());
                case RIGHT: modifyView(+10, 0, viewBean.getCenter());
                case UP: modifyView(0, +5, viewBean.getCenter());
                case DOWN: modifyView(0, -5, viewBean.getCenter());
            }
            key.consume();
        });

        canvas.setOnScroll(scroll -> viewBean.setFieldOfViewDeg(
                viewBean.getFieldOfViewDeg() + (Math.abs(scroll.getDeltaX()) < Math.abs(scroll.getDeltaY()) ?
                scroll.getDeltaY() : scroll.getDeltaX())));

        //FINALLY, ADDING LISTENERS TO REDRAW SKY
        observedSky.addListener((p, o, n) -> painter.drawDefault(observedSky.get(), planeToCanvas.get(), projection.get()));
        planeToCanvas.addListener((p, o, n) -> painter.drawDefault(observedSky.get(), planeToCanvas.get(), projection.get()));
    }

    /**
     * @return (Canvas) current canvas
     */
    public Canvas canvas() {
        return canvas;
    }

    /**
     * @return (ObjectBinding<CelestialObject>) observable: Celestial object under mouse
     */
    public ObjectBinding<CelestialObject> objectUnderMouseProperty() {
        return objectUnderMouse;
    }

    /**
     * @return (CelestialObject) value of observable: Celestial object under mouse
     */
    public CelestialObject getObjectUnderMouse() {
        return objectUnderMouse.get();
    }

    /**
     * @return (DoubleBinding) observable: mouse's azimuthal coordinate in degrees
     */
    public DoubleBinding mouseAzDegProperty() {
        return mouseAzDeg;
    }

    /**
     * @return (double) value of observable: mouse's azimuthal coordinate in degrees
     */
    public double getMouseAzDeg() {
        return mouseAzDeg.get();
    }

    /**
     * @return (DoubleBinding) observable: mouse's altitude coordinate in degrees
     */
    public DoubleBinding mouseAltDegProperty() {
        return mouseAltDeg;
    }

    /**
     * @return (double) value of observable: mouse's altitude coordinate in degrees
     */
    public double getMouseAltDeg() {
        return mouseAltDeg.get();
    }

    private void modifyView(double azDegDelta, double altDegDelta, HorizontalCoordinates center) {
        if (!(AZDEG_INTERVAL.contains(center.azDeg() + azDegDelta)
                && ALTDEG_INTERVAL.contains(center.altDeg() + altDegDelta))) {
            return;
        }
        viewBean.setCenter(center.withDeltaDeg(azDegDelta, altDegDelta));
    }
}
