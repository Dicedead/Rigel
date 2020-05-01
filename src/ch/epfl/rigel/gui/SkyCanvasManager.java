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
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;

import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Draws the sky on canvas continuously
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class SkyCanvasManager {

    public static final boolean ENABLE_ROTATION = true;

    private static final int MAX_CANVAS_DISTANCE = 10;
    private static final ClosedInterval ALT_INTERVAL = ClosedInterval.of(Angle.ofDeg(5), Math.PI/2);
    private static final double ALT_STEP = Angle.ofDeg(5);
    private static final double AZ_STEP = Angle.ofDeg(10);
    private static final ClosedInterval FOV_INTERVAL = ClosedInterval.of(30, 150);
    private static final double ROTATE_STEP = Angle.ofDeg(10);

    private final StarCatalogue catalogue;
    private final DateTimeBean dtBean;
    private final ViewingParametersBean viewBean;
    private final ObserverLocationBean obsLocBean;
    private final Canvas canvas;
    private final SkyCanvasPainter painter;

    private final ObjectBinding<StereographicProjection> projection;
    private final ObjectBinding<PlanarTransformation> planeToCanvas;
    private final ObjectBinding<PlanarTransformation> canvasToPlane;
    private final ObjectBinding<ObservedSky> observedSky;
    private final ObjectBinding<HorizontalCoordinates> mouseHorizontalPosition;
    private final DoubleBinding maxDistConverted;

    private final ObjectBinding<Optional<CelestialObject>> objectUnderMouse;
    private final DoubleBinding mouseAzDeg;
    private final DoubleBinding mouseAltDeg;

    //BONUS CONTENT
    private final ObjectProperty<CartesianCoordinates> mousePosition = new SimpleObjectProperty<>();
    private final DoubleProperty rotation = new SimpleDoubleProperty(0);
    private final ObjectProperty<TreeSet<DrawableObjects>> objectsToDraw = new SimpleObjectProperty<>(
            new TreeSet<>(Set.of(DrawableObjects.values())));
    //objectsToDraw is not a collection of observables but rather one observable that happened to be a set of immutable
    //objects, it thus made sense for us not to use ObservableSet
    private final ObjectBinding<Set<Class<?>>> drawableClasses;

    /**
     * SkyCanvasManager constructor
     *
     * @param catalogue (StarCatalogue) set of stars and asterisms
     * @param dtBean (DateTimeBean) bean representing a mutable ZonedDateTime object
     * @param obsLocBean (ObserverLocationBean) bean representing a mutable GeographicCoordinates object
     * @param viewBean (ViewingParametersBean) bean comprised of an fov parameter and the center of projection property
     */
    public SkyCanvasManager(StarCatalogue catalogue, DateTimeBean dtBean,
           ObserverLocationBean obsLocBean, ViewingParametersBean viewBean) {

        this.catalogue = catalogue;
        this.dtBean = dtBean;
        this.viewBean = viewBean;
        this.obsLocBean = obsLocBean;

        canvas = new Canvas(1,1); //avoids some ugliness down in planeToCanvas and its inverse
        painter = new SkyCanvasPainter(canvas);

        //CREATING VARIOUS ASTRONOMICAL AND MATHEMATICAL BINDINGS
        projection = Bindings.createObjectBinding(
                () -> new StereographicProjection(viewBean.getCenter()),
                viewBean.centerProperty());

        planeToCanvas = Bindings.createObjectBinding(
                () -> {
                    final PlanarTransformation transAndDilat = PlanarTransformation.ofDilatAndTrans(
                        canvas.getWidth()/StereographicProjection.applyToAngle(
                                Angle.ofDeg(viewBean.getFieldOfViewDeg())),
                        canvas.getWidth()/2, canvas.getHeight()/2);
                    return ENABLE_ROTATION ? transAndDilat.concat(PlanarTransformation.rotation(rotation.get())) :
                            transAndDilat;},
                canvas.widthProperty(), canvas.heightProperty(), viewBean.fieldOfViewDegProperty(), rotation);

        canvasToPlane = Bindings.createObjectBinding(() -> planeToCanvas.get().invert(), planeToCanvas);

        observedSky = Bindings.createObjectBinding(
                () -> new ObservedSky(dtBean.getZonedDateTime(), obsLocBean.getCoords(), projection.get(),catalogue),
                dtBean.zdtProperty(), obsLocBean.coordsProperty(), projection);

        drawableClasses = Bindings.createObjectBinding(
                () -> objectsToDraw.get().stream().map(DrawableObjects::getCorrespondingClass).collect(Collectors.toSet()),
                objectsToDraw);

        //TAKING CARE OF MOUSE'S POSITION AND USER INTERACTION
        mouseHorizontalPosition = Bindings.createObjectBinding(
                () -> mousePosition.get() != null ?
                projection.get().inverseApply(canvasToPlane.get().apply(mousePosition.get())) : null,
                projection, canvasToPlane, mousePosition);

        mouseAzDeg = Bindings.createDoubleBinding(() -> mouseHorizontalPosition.get().azDeg(), mouseHorizontalPosition);

        mouseAltDeg = Bindings.createDoubleBinding(() -> mouseHorizontalPosition.get().altDeg(), mouseHorizontalPosition);

        maxDistConverted = Bindings.createDoubleBinding(
                () -> canvasToPlane.get().applyDistance(MAX_CANVAS_DISTANCE),
                canvasToPlane);

        objectUnderMouse = Bindings.createObjectBinding(
                () -> {
                if(mousePosition.get() == null) return Optional.empty();
                Optional<CelestialObject> celest = observedSky.get().objectClosestTo(mousePosition.get(), maxDistConverted.get());
                if (celest.isEmpty()) return Optional.empty();
                return drawableClasses.get().contains(celest.get().getClass()) ? celest : Optional.empty(); },
                observedSky, mousePosition, maxDistConverted, drawableClasses);
        /* Adding the class filter as a parameter to ObservedSky.objectClosestTo almost breaks the MVC design pattern
           and actually slows down the execution. */

        canvas.setOnMouseMoved(mouse -> mousePosition.set(canvasToPlane.get().apply(mouse.getX(), mouse.getY())));

        canvas.setOnMousePressed(mouse -> {
            if (mouse.isPrimaryButtonDown()) canvas.requestFocus();
        });

        canvas.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case LEFT: modifyViewBean(-AZ_STEP, 0, viewBean.getCenter()); break;
                case RIGHT: modifyViewBean(+AZ_STEP, 0, viewBean.getCenter()); break;
                case UP: modifyViewBean(0, +ALT_STEP, viewBean.getCenter()); break;
                case DOWN: modifyViewBean(0, -ALT_STEP, viewBean.getCenter()); break;
                case J: modifyRotation(ROTATE_STEP); break;
                case L: modifyRotation(-ROTATE_STEP); break;
                case K: if (rotation.get() != 0) modifyRotation(-rotation.get());
            }
            key.consume();
        });

        canvas.setOnScroll(scroll -> viewBean.setFieldOfViewDeg(FOV_INTERVAL.clip(
                viewBean.getFieldOfViewDeg() - (Math.abs(scroll.getDeltaX()) < Math.abs(scroll.getDeltaY()) ?
                scroll.getDeltaY() : scroll.getDeltaX()))));

        //FINALLY, ADDING LISTENERS TO REDRAW SKY
        ChangeListener<Object> painterEvent =
                (p, o, n) -> painter.draw(observedSky.get(), planeToCanvas.get(), projection.get(), objectsToDraw.get());

        observedSky.addListener(painterEvent);
        planeToCanvas.addListener(painterEvent);
        objectsToDraw.addListener(painterEvent);
    }

    /**
     * @return (Canvas) current canvas
     */
    public Canvas canvas() {
        return canvas;
    }

    /**
     * @return (ObjectBinding<Optional<CelestialObject>>) observable: Celestial object under mouse
     */
    public ObjectBinding<Optional<CelestialObject>> objectUnderMouseProperty() {
        return objectUnderMouse;
    }

    /**
     * @return (CelestialObject) value of observable: Celestial object under mouse
     */
    public Optional<CelestialObject> getObjectUnderMouse() {
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

    /**
     * @return (Set<DrawableObjects>) value of observable: set of objects to draw
     */
    public Set<DrawableObjects> getObjectsToDraw() {
        return objectsToDraw.get();
    }

    /**
     * @return (ObjectProperty<TreeSet<DrawableObjects>>) observable: set of objects to draw
     */
    public ObjectProperty<TreeSet<DrawableObjects>> objectsToDrawProperty() { return objectsToDraw; }

    /**
     * Setter for observable: set of objects to draw
     *
     * @param setToDraw (Set<DrawableObjects>) potentially new set of DrawableObjects
     */
    public void setObjectsToDraw(Set<DrawableObjects> setToDraw) {
        if (!setToDraw.equals(objectsToDraw.get()))
        objectsToDraw.set((setToDraw instanceof TreeSet) ? ((TreeSet<DrawableObjects>)setToDraw) : new TreeSet<>(setToDraw));
    }

    private void modifyViewBean(double azDelta, double altDelta, HorizontalCoordinates center) {
        viewBean.setCenter(HorizontalCoordinates.of(
                Angle.normalizePositive(center.az() + azDelta),
                ALT_INTERVAL.clip(center.alt() + altDelta)
        ));
    }

    private void modifyRotation(double deltaRad) {
        if (ENABLE_ROTATION) {
            rotation.set(rotation.get() + deltaRad);
        }
    }
}
