package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.PlanarTransformation;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.math.Angle;
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

    private final static int MAX_CANVAS_DISTANCE = 10;

    private final StarCatalogue catalogue;
    private final DateTimeBean dtBean;
    private final ViewingParametersBean viewBean;
    private final ObserverLocationBean obsLocBean;
    private final Canvas canvas;

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

    public SkyCanvasManager(final StarCatalogue catalogue, final DateTimeBean dtBean,
           final ObserverLocationBean obsLocBean, final ViewingParametersBean viewBean) {

        canvas = new Canvas();

        this.catalogue = catalogue;
        this.dtBean = dtBean;
        this.viewBean = viewBean;
        this.obsLocBean = obsLocBean;

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

        //TODO mouse position - is it even a CartesianCoordinates?
        //TODO SkyCanvasPainter
        //TODO listeners
    }

    //TODO other methods

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
}
