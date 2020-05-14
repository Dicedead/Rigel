package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.CelestialObjectModel;
import ch.epfl.rigel.astronomy.Moon;
import ch.epfl.rigel.astronomy.MoonModel;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.Planet;
import ch.epfl.rigel.astronomy.PlanetModel;
import ch.epfl.rigel.astronomy.Star;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.astronomy.Orbit;
import ch.epfl.rigel.astronomy.SunModel;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.PlanarTransformation;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.gui.searchtool.Searcher;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseButton;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Draws the sky on canvas continuously
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class SkyCanvasManager {

    private static final int INIT_WIDTH = 800;
    private static final int INIT_HEIGHT = 600;

    private static final int MAX_CANVAS_DISTANCE = 10;
    private static final ClosedInterval NORMAL_ALT_INTERVAL = ClosedInterval.of(Angle.ofDeg(5), Math.PI / 2);
    private static final ClosedInterval EXTENDED_ALT_INTERVAL = ClosedInterval.of(-Math.PI / 2, Math.PI / 2);
    private static final double ALT_STEP = Angle.ofDeg(5);
    private static final double AZ_STEP = Angle.ofDeg(10);
    private static final ClosedInterval FOV_INTERVAL = ClosedInterval.of(30, 150);

    private final StarCatalogue catalogue;
    private final DateTimeBean dtBean;
    private final ViewingParametersBean viewBean;
    private final ObserverLocationBean obsLocBean;

    private final Canvas canvas;
    private final SkyCanvasPainter painter;
    private final Searcher searcher;

    private final ObjectBinding<StereographicProjection> projection;
    private final ObjectBinding<PlanarTransformation> planeToCanvas;
    private final ObjectBinding<PlanarTransformation> canvasToPlane;
    private final ObjectBinding<ObservedSky> observedSky;
    private final ObjectBinding<HorizontalCoordinates> mouseHorizontalPosition;
    private final DoubleBinding maxDistConverted;

    private final ObjectBinding<Optional<CelestialObject>> objectUnderMouse;
    private final DoubleBinding mouseAzDeg;
    private final DoubleBinding mouseAltDeg;
    private final ObjectProperty<CartesianCoordinates> mousePosition;

    //BONUS CONTENT
    private static final int SEARCH_CACHE_CAPACITY = 14;

    private static final int ORBIT_SIMULATION_DAYS_DEFAULT = 1000;

    private final BooleanProperty extendedAltitudeIsOn = new SimpleBooleanProperty(true);

    private final DoubleProperty mouseXstartOfDrag = new SimpleDoubleProperty();
    private final DoubleProperty mouseYstartOfDrag = new SimpleDoubleProperty();
    private final DoubleProperty mouseDragSensitivity = new SimpleDoubleProperty(1 / 2e4);
    //suggested interval is [1/4e4; 4/1e4]
    private final DoubleProperty mouseScrollSensitivity = new SimpleDoubleProperty(0.75);

    private final BooleanProperty nonFunctionalKeyPressed = new SimpleBooleanProperty(false);

    private static final double ROTATION_ATTENUATION = 1 / 2d;
    private static final double ROTATE_STEP = Angle.ofDeg(10);
    private final DoubleProperty rotation = new SimpleDoubleProperty(0);
    private final ObjectBinding<PlanarTransformation> rotationMatrix;
    private final ObjectBinding<PlanarTransformation> inverseRotation;

    private final ObjectProperty<EnumSet<DrawableObjects>> objectsToDraw = new SimpleObjectProperty<>(
            EnumSet.allOf(DrawableObjects.class));
    //objectsToDraw is not a collection of observables but rather one observable that happened to be a set of immutable
    //objects, it thus made sense for us not to use ObservableSet
    private final ObjectBinding<Set<Class<?>>> drawableClasses;

    private final IntegerProperty resolutionInHours = new SimpleIntegerProperty(5);
    private final IntegerProperty drawOrbitUntil = new SimpleIntegerProperty(ORBIT_SIMULATION_DAYS_DEFAULT);
    private final IntegerProperty orbitDrawingStep = new SimpleIntegerProperty(5);
    private final ObjectProperty<Orbit<? extends CelestialObject>> orbitProperty = new SimpleObjectProperty<>();

    /**
     * SkyCanvasManager constructor
     *
     * @param catalogue  (StarCatalogue) set of stars and asterisms
     * @param dtBean     (DateTimeBean) bean representing a mutable ZonedDateTime object
     * @param obsLocBean (ObserverLocationBean) bean representing a mutable GeographicCoordinates object
     * @param viewBean   (ViewingParametersBean) bean comprised of an fov parameter and the center of projection property
     */
    public SkyCanvasManager(StarCatalogue catalogue, DateTimeBean dtBean,
                            ObserverLocationBean obsLocBean, ViewingParametersBean viewBean) {

        this.catalogue = catalogue;
        this.dtBean = dtBean;
        this.viewBean = viewBean;
        this.obsLocBean = obsLocBean;

        canvas = new Canvas(INIT_WIDTH, INIT_HEIGHT); //avoids some ugliness down in planeToCanvas and its inverse
        painter = new SkyCanvasPainter(canvas);

        mousePosition = new SimpleObjectProperty<>(CartesianCoordinates.ORIGIN);

        projection = Bindings.createObjectBinding(
                () -> new StereographicProjection(viewBean.getCenter()),
                viewBean.centerProperty());

        observedSky = Bindings.createObjectBinding(
                () -> new ObservedSky(dtBean.getZonedDateTime(), obsLocBean.getCoords(), projection.get(), catalogue),
                dtBean.zdtProperty(), obsLocBean.coordsProperty(), projection);

        drawableClasses = Bindings.createObjectBinding(
                () -> objectsToDraw.get().stream().map(DrawableObjects::getCorrespondingClass).collect(Collectors.toSet()),
                objectsToDraw);

        searcher = new Searcher(SEARCH_CACHE_CAPACITY, observedSky.get(), obsLocBean, dtBean);
        searcher.lastSelectedNameProperty().addListener((p, o, n) -> {
            if (n != null) {
                viewBean.setCenter(projection.get().inverseApply(observedSky.get().celestialObjMap().get(
                        observedSky.get().celestialObjMap().keySet()
                                .stream()
                                .filter(celest -> celest.name().equals(n))
                                .findFirst().get())));
                searcher.setLastSelectedName(null);
            }
        });

        rotationMatrix = Bindings.createObjectBinding(
                () -> PlanarTransformation.rotation(rotation.get()), rotation);

        inverseRotation = Bindings.createObjectBinding(() -> rotationMatrix.get().invert(), rotationMatrix);

        planeToCanvas = Bindings.createObjectBinding(
                () ->
                        PlanarTransformation.ofDilatAndTrans(
                                canvas.getWidth() / StereographicProjection.applyToAngle(
                                        Angle.ofDeg(viewBean.getFieldOfViewDeg())),
                                canvas.getWidth() / 2, canvas.getHeight() / 2)
                                .concat(rotationMatrix.get()),
                canvas.widthProperty(), canvas.heightProperty(), viewBean.fieldOfViewDegProperty(), rotationMatrix);

        canvasToPlane = Bindings.createObjectBinding(() -> planeToCanvas.get().invert(), planeToCanvas);

        //TAKING CARE OF MOUSE'S POSITION AND USER INTERACTION
        mouseHorizontalPosition = Bindings.createObjectBinding(
                () -> projection.get().inverseApply(mousePosition.get()),
                projection, canvasToPlane, mousePosition);

        mouseAzDeg = Bindings.createDoubleBinding(() -> mouseHorizontalPosition.get().azDeg(), mouseHorizontalPosition);

        mouseAltDeg = Bindings.createDoubleBinding(() -> mouseHorizontalPosition.get().altDeg(), mouseHorizontalPosition);

        maxDistConverted = Bindings.createDoubleBinding(
                () -> canvasToPlane.get().applyDistance(MAX_CANVAS_DISTANCE),
                canvasToPlane);

        objectUnderMouse = Bindings.createObjectBinding(
                () -> {
                    if (mousePosition.get() == CartesianCoordinates.ORIGIN) return Optional.empty();
                    Optional<CelestialObject> celest = observedSky.get().objectClosestTo(mousePosition.get(), maxDistConverted.get());
                    if (celest.isEmpty()) return Optional.empty();
                    return drawableClasses.get().contains(celest.get().getClass()) ? celest : Optional.empty();
                },
                observedSky, mousePosition, maxDistConverted, drawableClasses);

        canvas.setOnMouseMoved(mouse -> mousePosition.set(canvasToPlane.get().apply(mouse.getX(), mouse.getY())));
        /* We've chosen to convert the mousePosition to the plane's referential (unlike suggested) to avoid having to
           convert it later. */

        canvas.setOnMousePressed(mouse -> {
            if (!canvas.isFocused()) canvas.requestFocus();
            mouseXstartOfDrag.set(mouse.getX());
            mouseYstartOfDrag.set(mouse.getY());

            if (mouse.isSecondaryButtonDown()) {
                Class<? extends CelestialObject> celestClass;
                if (objectUnderMouse.get().isPresent() &&
                        !(celestClass = objectUnderMouse.get().get().getClass()).equals(Star.class)) {
                    orbitProperty.set(new Orbit<>(dtBean.getZonedDateTime(),
                            resolutionInHours.get(),
                            ORBIT_SIMULATION_DAYS_DEFAULT, getModel(celestClass),
                            new EclipticToEquatorialConversion(dtBean.getZonedDateTime())));
                } else {
                    orbitProperty.set(null);
                }
            }
        });

        canvas.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case LEFT:
                    applyRotationThenModifyViewBean(-AZ_STEP, 0);
                    break;
                case RIGHT:
                    applyRotationThenModifyViewBean(+AZ_STEP, 0);
                    break;
                case UP:
                    applyRotationThenModifyViewBean(0, +ALT_STEP);
                    break;
                case DOWN:
                    applyRotationThenModifyViewBean(0, -ALT_STEP);
                    break;
                case J:
                    modifyRotation(ROTATE_STEP);
                    break;
                case L:
                    modifyRotation(-ROTATE_STEP);
                    break;
                case K:
                    if (rotation.get() != 0) modifyRotation(-rotation.get());
                    break;
                default:
                    nonFunctionalKeyPressed.set(true);
            }
            key.consume();
        });

        canvas.setOnScroll(scroll -> viewBean.setFieldOfViewDeg(FOV_INTERVAL.clip(
                viewBean.getFieldOfViewDeg() - mouseScrollSensitivity.get() *
                        (Math.abs(scroll.getDeltaX()) < Math.abs(scroll.getDeltaY()) ? scroll.getDeltaY() : scroll.getDeltaX()))));

        canvas.setOnMouseDragged(mouse -> {
            if (mouse.isPrimaryButtonDown()) {
                applyRotationThenModifyViewBean(
                        mouseDragSensitivity.get() * (mouse.getX() - mouseXstartOfDrag.get()),
                        mouseDragSensitivity.get() * (mouseYstartOfDrag.get() - mouse.getY()));
            }
            if (mouse.getButton() == MouseButton.MIDDLE) {
                modifyRotation(mouseDragSensitivity.get() * ROTATION_ATTENUATION *
                        (mouseXstartOfDrag.get() + mouseYstartOfDrag.get() - mouse.getX() - mouse.getY()));
            }
        });

        extendedAltitudeIsOn.addListener((p, o, n) -> applyRotationThenModifyViewBean(0, 0));
        //clips to smaller [5; -90] if extentedAltitude is turned off.

        //ADDING LISTENERS TO REDRAW SKY
        ChangeListener<Object> painterEvent =
                (p, o, n) -> painter.drawMain(observedSky.get(), planeToCanvas.get(), projection.get(), objectsToDraw.get(),
                        orbitProperty.get(), drawOrbitUntil.get(), orbitDrawingStep.get());

        observedSky.addListener(painterEvent);
        planeToCanvas.addListener(painterEvent);
        objectsToDraw.addListener(painterEvent);
        orbitProperty.addListener(painterEvent);
    }

    /**
     * @return (Canvas) current canvas
     */
    public Canvas canvas() {
        return canvas;
    }

    /**
     * @return (Searcher) search window
     */
    public Searcher searcher() {
        return searcher;
    }

    /**
     * @return (ObjectBinding < Optional < CelestialObject > >) observable: Celestial object under mouse
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
     * @return (Set < DrawableObjects >) value of observable: set of objects to draw
     */
    public Set<DrawableObjects> getObjectsToDraw() {
        return objectsToDraw.get();
    }

    /**
     * @return (ObjectProperty < EnumSet < DrawableObjects > >) observable: set of objects to draw
     */
    public ObjectProperty<EnumSet<DrawableObjects>> objectsToDrawProperty() {
        return objectsToDraw;
    }

    /**
     * Setter for observable: set of objects to draw
     *
     * @param setToDraw (EnumSet<DrawableObjects>) potentially new set of DrawableObjects
     */
    public void setObjectsToDraw(EnumSet<DrawableObjects> setToDraw) {
        if (!setToDraw.equals(objectsToDraw.get()))
            objectsToDraw.set(setToDraw);
    }

    /**
     * @return (double) value of observable: mouse's drag sensitivity
     */
    public double getMouseDragSensitivity() {
        return mouseDragSensitivity.get();
    }

    /**
     * @return (DoubleProperty) observable: mouse's drag sensitivity
     */
    public DoubleProperty mouseDragSensitivityProperty() {
        return mouseDragSensitivity;
    }

    /**
     * Setter for observable: mouse's drag sensitivity
     *
     * @param mouseDragSensitivity (double) mouse sensitivity to be set to
     */
    public void setMouseDragSensitivity(double mouseDragSensitivity) {
        this.mouseDragSensitivity.set(mouseDragSensitivity);
    }

    /**
     * @return (double) value of observable: mouse scroll sensitivity
     */
    public double getMouseScrollSensitivity() {
        return mouseScrollSensitivity.get();
    }

    /**
     * @return (DoubleProperty) observable: scroll sensitivity
     */
    public DoubleProperty mouseScrollSensitivityProperty() {
        return mouseScrollSensitivity;
    }

    /**
     * Setter for observable: mouse scroll sensitivity
     *
     * @param mouseScrollSensitivity (double) sensitivity to be set to
     */
    public void setMouseScrollSensitivity(double mouseScrollSensitivity) {
        this.mouseScrollSensitivity.set(mouseScrollSensitivity);
    }

    /**
     * @return (boolean) value of observable: enabled extended altitude interval
     */
    public boolean isExtendedAltitudeIsOn() {
        return extendedAltitudeIsOn.get();
    }

    /**
     * @return (BooleanProperty) observable: enabled extended altitude interval
     */
    public BooleanProperty extendedAltitudeIsOnProperty() {
        return extendedAltitudeIsOn;
    }

    /**
     * Setter for observable: enabled extended altitude interval
     *
     * @param extendedAltitudeIsOn (boolean) value to be set to
     */
    public void setExtendedAltitudeIsOn(boolean extendedAltitudeIsOn) {
        this.extendedAltitudeIsOn.set(extendedAltitudeIsOn);
    }

    /**
     * @return (boolean) value of observable: non functional key has been pressed (ie a key that does nothing in the canvas)
     */
    public boolean isNonFunctionalKeyPressed() {
        return nonFunctionalKeyPressed.get();
    }

    /**
     * @return (BooleanProperty) observable: non functional key pressed
     */
    public BooleanProperty nonFunctionalKeyPressedProperty() {
        return nonFunctionalKeyPressed;
    }

    /**
     * Setter for observable: non functional key pressed
     *
     * @param nonFunctionalKeyPressed (boolean)
     */
    public void setNonFunctionalKeyPressed(boolean nonFunctionalKeyPressed) {
        this.nonFunctionalKeyPressed.set(nonFunctionalKeyPressed);
    }

    /**
     * Modifies the center of projection with given deltas after applying rotation (or rather: reversing it).
     *
     * @param azDelta  (double) change in azimuth
     * @param altDelta (double) change in altitude
     */
    private void applyRotationThenModifyViewBean(double azDelta, double altDelta) {
        modifyViewBean(inverseRotation.get().apply(azDelta, altDelta));
    }

    /**
     * Modifies the center of projection with given delta vector. It also gives the option to use the full altitude
     * interval instead of the suggested [5, 90] one - would have been a waste without the added mouse movement
     *
     * @param modifVector (CartesianCoordinates) delta vector
     */
    private void modifyViewBean(CartesianCoordinates modifVector) {
        viewBean.setCenter(HorizontalCoordinates.of(
                Angle.normalizePositive(viewBean.getCenter().az() + modifVector.x()),
                (extendedAltitudeIsOn.get()) ?
                        EXTENDED_ALT_INTERVAL.clip(viewBean.getCenter().alt() + modifVector.y()) :
                        NORMAL_ALT_INTERVAL.clip(viewBean.getCenter().alt() + modifVector.y())
        ));
    }

    private void modifyRotation(double deltaRad) {
        rotation.set(Angle.normalizePositive(rotation.get() + deltaRad));
    }

    private CelestialObjectModel<? extends CelestialObject> getModel(Class<? extends CelestialObject> celestClass) {
        if (celestClass.equals(Planet.class)) {
            return PlanetModel.getPlanetModelFromString(objectUnderMouse.get().get().name());
        } else if (celestClass.equals(Moon.class)) {
            return MoonModel.MOON;
        } else {
            return SunModel.SUN;
        }
    }
}
