package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.PlanarTransformation;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.gui.searchtool.Searcher;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ch.epfl.rigel.Preconditions.epsilonIfZero;

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
    private static final double EPSILON = 5e-2;
    private static final ClosedInterval NORMAL_ALT_INTERVAL =
            ClosedInterval.of(Angle.ofDeg(5) + EPSILON, Math.PI / 2 - EPSILON);
    private static final ClosedInterval EXTENDED_ALT_INTERVAL =
            ClosedInterval.of(-Math.PI / 2 + EPSILON, Math.PI / 2 - EPSILON);
    private static final double ALT_STEP_POS = Angle.ofDeg(5);
    private static final double AZ_STEP_POS = Angle.ofDeg(10);
    private static final double ALT_STEP_NEG = -ALT_STEP_POS;
    private static final double AZ_STEP_NEG = -AZ_STEP_POS;
    private static final ClosedInterval FOV_INTERVAL = ClosedInterval.of(30, 150);

    private final StarCatalogue         catalogue;
    private final DateTimeBean          dtBean;
    private final ViewingParametersBean viewBean;
    private final ObserverLocationBean  obsLocBean;

    private final Canvas            canvas;
    private final SkyCanvasPainter  painter;
    private final Searcher          searcher;

    private final ObjectBinding<StereographicProjection>    projection;
    private final ObjectBinding<PlanarTransformation>       planeToCanvas;
    private final ObjectBinding<PlanarTransformation>       canvasToPlane;
    private final ObjectBinding<ObservedSky>                observedSky;
    private final ObjectBinding<HorizontalCoordinates>      mouseHorizontalPosition;
    private final DoubleBinding maxDistConverted;

    private final ObjectBinding<Optional<CelestialObject>>  objectUnderMouse;
    private final ObjectProperty<CartesianCoordinates>      mousePosition;
    private final DoubleBinding mouseAzDeg;
    private final DoubleBinding mouseAltDeg;

    //BONUS CONTENT
    private static final int SEARCH_CACHE_CAPACITY = 14;
    private static final List<TimeAccelerator> PAUSE_IF_SEARCH_LIST =
            List.of(NamedTimeAccelerator.DAY.getAccelerator(),
            NamedTimeAccelerator.SIDEREAL_DAY.getAccelerator(),
            NamedTimeAccelerator.TIMES_3000.getAccelerator());
    private static final int ORBIT_SIMULATION_LENGTH_DEFAULT = 1200;

    private final BooleanProperty extendedAltitudeIsOn  = new SimpleBooleanProperty(true);
    private final DoubleProperty mouseXstartOfDrag      = new SimpleDoubleProperty();
    private final DoubleProperty mouseYstartOfDrag      = new SimpleDoubleProperty();
    private static final double MOUSE_DRAG_FACTOR       = 2e4;
    private final DoubleProperty mouseDragSensitivity   = new SimpleDoubleProperty(1 / MOUSE_DRAG_FACTOR);
    //suggested interval is [1/4e4; 4/1e4]
    private final DoubleProperty mouseScrollSensitivity     = new SimpleDoubleProperty(0.75);
    private final BooleanProperty nonFunctionalKeyPressed   = new SimpleBooleanProperty(false);

    private static final double ROTATION_ATTENUATION    = 1 / 2d;
    private static final double ROTATE_STEP_POS         = Angle.ofDeg(10);
    private static final double ROTATE_STEP_NEG         = -ROTATE_STEP_POS;
    private final DoubleProperty rotation               = new SimpleDoubleProperty(0);

    private final ObjectBinding<PlanarTransformation> rotationMatrix;
    private final ObjectBinding<PlanarTransformation> inverseRotation;

    private final ObjectProperty<EnumSet<DrawableObjects>> objectsToDraw;
    //objectsToDraw is not a collection of observables but rather one observable that happened to be a set of immutable
    //objects, it thus made sense for us not to use ObservableSet
    private final ObjectBinding<Set<Class<?>>> drawableClasses;

    private static final List<Color> COLORS_LIST            = List.of(Color.BLUE, Color.CADETBLUE, Color.FIREBRICK
            .deriveColor(1,1,1,0.5), Color.RED);
    private final ObjectProperty<Color> orbitColor          = new SimpleObjectProperty<>(COLORS_LIST.get(1));
    private final ObjectProperty<Color> horizonColor        = new SimpleObjectProperty<>(COLORS_LIST.get(3));
    private final ObjectProperty<Color> gridColor           = new SimpleObjectProperty<>(COLORS_LIST.get(2));
    private final ObjectProperty<Color> asterismColor       = new SimpleObjectProperty<>(COLORS_LIST.get(0));
    private final List<ObjectProperty<Color>> colorsList    = List.of(asterismColor, orbitColor, gridColor, horizonColor);
    private static final List<String> DRAWABLES_LABELS      = List.of("Astérismes ", "Orbites ", "Grille ", "Horizon ");
    private static final int RESOLUTION_DEFAULT             = 5;
    private final IntegerProperty drawOrbitUntil            = new SimpleIntegerProperty(ORBIT_SIMULATION_LENGTH_DEFAULT);
    private final IntegerProperty orbitDrawingStep          = new SimpleIntegerProperty(5);

    private final ObjectProperty<CelestialObject> wantNewInformationPanel = new SimpleObjectProperty<>();

    private final ObjectProperty<Orbit<? extends CelestialObject>> orbitProperty = new SimpleObjectProperty<>();
    private final BooleanBinding orbitIsNull;
    private static final List<String> SUGGESTED_GRID_SPACINGS = List.of("5°","10°", "15°", "30°", "45°", "90°");
    private final IntegerProperty horizCoordsGridSpacingDeg = new SimpleIntegerProperty(15);
    private static final List<TimeAccelerator> NON_NULL_ACC_ORBIT_LIST =
            List.of(NamedTimeAccelerator.SIDEREAL_DAY.getAccelerator(),
                    NamedTimeAccelerator.DAY.getAccelerator());

    /**
     * SkyCanvasManager constructor
     *
     * @param animator   (TimeAnimator) time accelerating class
     * @param catalogue  (StarCatalogue) set of stars and asterisms
     * @param dtBean     (DateTimeBean) bean representing a mutable ZonedDateTime object
     * @param obsLocBean (ObserverLocationBean) bean representing a mutable GeographicCoordinates object
     * @param viewBean   (ViewingParametersBean) bean comprised of an fov parameter and the center of projection property
     * @param execServ   (ExecutorService) executor service for ObservedSky.mapObjectToPosition
     */
    public SkyCanvasManager(TimeAnimator animator, StarCatalogue catalogue, DateTimeBean dtBean,
                            ObserverLocationBean obsLocBean, ViewingParametersBean viewBean, ExecutorService execServ) {

        this.catalogue  = catalogue;
        this.dtBean     = dtBean;
        this.viewBean   = viewBean;
        this.obsLocBean = obsLocBean;

        canvas  = new Canvas(INIT_WIDTH, INIT_HEIGHT); //avoids some ugliness down in planeToCanvas and its inverse
        painter = new SkyCanvasPainter(canvas);

        mousePosition = new SimpleObjectProperty<>(CartesianCoordinates.ORIGIN);

        projection = Bindings.createObjectBinding(
                () -> new StereographicProjection(viewBean.getCenter()),
                viewBean.centerProperty());

        observedSky = Bindings.createObjectBinding(

                () -> new ObservedSky(dtBean.getZonedDateTime(), obsLocBean.getCoords(), projection.get(), catalogue, execServ),
                dtBean.zdtProperty(), obsLocBean.coordsProperty(), projection);

        orbitProperty.set(orbitFactory(PlanetModel.MERCURY));
        animator.runningProperty().addListener((p, o, n) -> {
            if (n && !NON_NULL_ACC_ORBIT_LIST.contains(animator.getAccelerator())) {
                orbitProperty.set(null);
            }
        });

        orbitIsNull = Bindings.createBooleanBinding(() -> Objects.isNull(orbitProperty.get()), orbitProperty);

        EnumSet<DrawableObjects> allDrawablesExceptGrid = EnumSet.allOf(DrawableObjects.class);
        allDrawablesExceptGrid.remove(DrawableObjects.GRID);
        objectsToDraw = new SimpleObjectProperty<>(allDrawablesExceptGrid);

        drawableClasses = Bindings.createObjectBinding(
                () -> objectsToDraw.get().stream().map(DrawableObjects::getCorrespondingClass).collect(Collectors.toSet()),
                objectsToDraw);

        searcher = new Searcher(SEARCH_CACHE_CAPACITY, observedSky.get());
        searcher.lastSelectedNameProperty().addListener((p, o, n) -> {
            if (n != null) {
                viewBean.setCenter(projection.get().inverseApply(observedSky.get().celestialObjMap().get(
                        observedSky.get().celestialObjMap().keySet()
                                .stream()
                                .filter(celest -> celest.name().equals(n))
                                .findFirst().orElseThrow())));
                if (animator.isRunning() && PAUSE_IF_SEARCH_LIST.contains(animator.getAccelerator())) {
                    animator.stop();
                }
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
        //--------------------------------------------------------------------------------------------------------------
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
                if (objectUnderMouse.get().isPresent()) {
                    wantNewInformationPanel.set(objectUnderMouse.get().get());

                    if (!(celestClass = objectUnderMouse.get().get().getClass()).equals(Star.class)
                    && (!animator.isRunning() || NON_NULL_ACC_ORBIT_LIST.contains(animator.getAccelerator()))) {
                        orbitProperty.set(orbitFactory(getModel(celestClass)));
                    } else {
                        orbitProperty.set(null);
                    }
                } else {
                    orbitProperty.set(null);
                    wantNewInformationPanel.set(null);
                }
            }
        });

        canvas.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case LEFT:
                    applyRotationThenModifyViewBean(AZ_STEP_NEG, 0);
                    break;
                case RIGHT:
                    applyRotationThenModifyViewBean(AZ_STEP_POS, 0);
                    break;
                case UP:
                    applyRotationThenModifyViewBean(0, ALT_STEP_POS);
                    break;
                case DOWN:
                    applyRotationThenModifyViewBean(0, ALT_STEP_NEG);
                    break;
                case J:
                    modifyRotation(ROTATE_STEP_POS);
                    break;
                case L:
                    modifyRotation(ROTATE_STEP_NEG);
                    break;
                case K:
                    if (rotation.get() != 0) modifyRotation(-rotation.get());
                    break;
                case O:
                    orbitProperty.set(null);
                    break;
                case I:
                    resetInformationPanel();
                    break;
                default:
                    if (!key.getCode().equals(KeyCode.ESCAPE)) nonFunctionalKeyPressed.set(true);
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
                        orbitProperty.get(), drawOrbitUntil.get(), orbitDrawingStep.get(), asterismColor.get(), horizonColor.get(),
                        orbitColor.get(), gridColor.get(), horizCoordsGridSpacingDeg.get());

        Stream.of(observedSky, planeToCanvas, objectsToDraw, orbitProperty, orbitColor, asterismColor, horizonColor,
                gridColor, horizCoordsGridSpacingDeg, drawOrbitUntil, orbitDrawingStep)
                .forEach(prop -> prop.addListener(painterEvent));

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
     * @return (DoubleBinding) observable: mouse's azimuthal coordinate in degrees
     */
    public DoubleBinding mouseAzDegProperty() {
        return mouseAzDeg;
    }

    /**
     * @return (DoubleBinding) observable: mouse's altitude coordinate in degrees
     */
    public DoubleBinding mouseAltDegProperty() {
        return mouseAltDeg;
    }

    /**
     * @return (Set < DrawableObjects >) value of observable: set of objects to draw
     */
    public Set<DrawableObjects> getObjectsToDraw() {
        return objectsToDraw.get();
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
     * @return (double) value of observable: mouse drag moving sensitivity
     */
    public double getMouseDragFactor() {
        return MOUSE_DRAG_FACTOR;
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
     * @return (BooleanProperty) observable: enabled extended altitude interval
     */
    public BooleanProperty extendedAltitudeIsOnProperty() {
        return extendedAltitudeIsOn;
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
     * @return (ObjectProperty<CelestialObject>) property of the potential selected celestial object the user wants more
     *         information on
     */
    public ObjectProperty<CelestialObject> wantNewInformationPanelProperty() {
        return wantNewInformationPanel;
    }

    /**
     * Removes the right information panel, if it was present
     */
    public void resetInformationPanel() { wantNewInformationPanel.set(null); }

    /**
     *
     * @return the distance between two vertical lines defining the grid
     */
    public int getHorizCoordsGridSpacingDeg() {
        return horizCoordsGridSpacingDeg.get();
    }

    /**
     *  Sets the distance between two vertical lines defining the grid
      * @param horizCoordsGridSpacingDeg the wanted distance
     */
    public void setHorizCoordsGridSpacingDeg(int horizCoordsGridSpacingDeg) {
        this.horizCoordsGridSpacingDeg.set(horizCoordsGridSpacingDeg);
    }

    /**
     * Recommended grid spacings. If you do not use this method, bear in mind that the grid spacing has to divide both
     * 90 and 360 (as it is given in degrees).
     *
     * @return (List<String>) list of suggested grid spacings
     */
    public static List<String> suggestedGridSpacings() {
        return SUGGESTED_GRID_SPACINGS;
    }

    /**
     * List of observables: colors of drawable objects, has the same order as the labels in {@code colorLabelsList()}
     *
     * @return (List<ObjectProperty<Color>>) list of observable colors
     */
    public List<ObjectProperty<Color>> colorPropertiesList() {
        return colorsList;
    }

    /**
     * List of drawables objects' names, same order as in {@code colorPropertiesList()}
     *
     * @return (List<String>) list of names of drawable objects
     */
    public static List<String> colorLabelsList() {
        return DRAWABLES_LABELS;
    }

    /**
     * List of default drawable objects' colors, same order as in {@code colorLabelsList()}
     *
     * @return (List<Color>) list of default colors for drawable objects
     */
    public static List<Color> getDefaultColorsList() {
        return COLORS_LIST;
    }

    /**
     * @return (int) value of observable: orbit drawing length
     */
    public int getDrawOrbitUntil() {
        return drawOrbitUntil.get();
    }

    /**
     * @return (IntegerProperty) observable: orbit drawing length
     */
    public IntegerProperty drawOrbitUntilProperty() {
        return drawOrbitUntil;
    }

    /**
     * @return (int) value of observable: orbit drawing step
     */
    public int getOrbitDrawingStep() {
        return orbitDrawingStep.get();
    }

    /**
     * @return (IntegerProperty) observable: orbit drawing step
     */
    public IntegerProperty orbitDrawingStepProperty() {
        return orbitDrawingStep;
    }

    /**
     * @return (BooleanBinding) observable: orbit is null
     */
    public BooleanBinding orbitIsNullProperty() {
        return orbitIsNull;
    }

    /**
     * Modifies the center of projection with given deltas after applying rotation (or rather: reversing it).
     * Inverting the rotation avoids confusing mouse movements when the rotation is not close to zero, like dragging
     * the mouse sideways and having the projection on a point upwards from the original point.
     *
     * @param azDelta  (double) change in azimuth
     * @param altDelta (double) change in altitude
     */
    private void applyRotationThenModifyViewBean(double azDelta, double altDelta) {
        modifyViewBean(rotation.get() == 0 ? CartesianCoordinates.of(azDelta, altDelta) :
                inverseRotation.get().apply(azDelta, altDelta));
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
                        EXTENDED_ALT_INTERVAL.clip(epsilonIfZero(viewBean.getCenter().alt() + modifVector.y())) :
                        NORMAL_ALT_INTERVAL.clip(viewBean.getCenter().alt() + modifVector.y())
                //the altitude is never zero in interval [5, 90]
        ));
    }

    private void modifyRotation(double deltaRad) {
        rotation.set(Angle.normalizePositive(rotation.get() + deltaRad));
    }

    private CelestialObjectModel<? extends CelestialObject> getModel(Class<? extends CelestialObject> celestClass) {

        if (celestClass.equals(Planet.class)) {
            return PlanetModel.getPlanetModelFromString(objectUnderMouse.get().orElseThrow().name());
        } else if (celestClass.equals(Moon.class)) {
            return MoonModel.MOON;
        } else {
            return SunModel.SUN;
        }
    }

    private Orbit<? extends CelestialObject> orbitFactory(CelestialObjectModel<? extends CelestialObject> modelClass) {
         return new Orbit<>(dtBean.getZonedDateTime(),
                RESOLUTION_DEFAULT, ORBIT_SIMULATION_LENGTH_DEFAULT, modelClass,
                new EclipticToEquatorialConversion(dtBean.getZonedDateTime()));
    }
}
