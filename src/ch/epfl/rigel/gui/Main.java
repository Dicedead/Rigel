package ch.epfl.rigel.gui;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.Moon;
import ch.epfl.rigel.astronomy.Star;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.astronomy.Sun;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.math.Angle;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.util.converter.NumberStringConverter;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Main program class (version: step 12)
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Main extends Application {

    private static final GeographicCoordinates INITIAL_GEO_COORDS = GeographicCoordinates.ofDeg(6.57, 46.52);
    private static final HorizontalCoordinates INITIAL_CENTER =
            HorizontalCoordinates.ofDeg(180.05, 15.001);
    private static final NamedTimeAccelerator INITIAL_ACCELERATOR = NamedTimeAccelerator.TIMES_300;
    private static final double INITIAL_FOV = 100;
    private static final int MIN_WIDTH = 800;
    private static final int MIN_HEIGHT = 600;
    private static final double MOUSE_DRAG_DEFAULTSENS = 1;
    private static final double MOUSE_SCROLL_DEFAULTSENS = 0.75;

    private static final int CUSTOM_FONT_DEFAULT_SIZE = 15;
    private static final int CUSTOM_FONT_SMALL_SIZE = 10;
    private static final String INPUT_HYGDATA = "/hygdata_v3.csv";
    private static final String INPUT_ASTERISMS = "/asterisms.txt";
    private static final String INPUT_FONT = "/Font Awesome 5 Free-Solid-900.otf";

    //Styles:
    private static final String CONTROL_BAR_STYLE = "-fx-spacing: 4; -fx-padding: 4;";
    private static final String SEARCH_HBOX_STYLE = "-fx-spacing: inherit; -fx-alignment: baseline-left;";
    private static final String POSITION_HBOX_STYLE = "-fx-spacing: inherit; -fx-alignment: baseline-left;";
    private static final String POSITION_TXTFIELDS_STYLE = "-fx-pref-width: 60; -fx-alignment: baseline-right;";
    private static final String TIME_HBOX_STYLE = "-fx-spacing: inherit; -fx-alignment: baseline-left;";
    private static final String TIME_DATEPICK_STYLE = "-fx-pref-width: 120;";
    private static final String TIME_TXTFIELD_STYLE = "-fx-pref-width: 75;-fx-alignment: baseline-right;";
    private static final String TIME_ZONECOMBOBOX_STYLE = "-fx-pref-width: 180;";
    private static final String ACC_HBOX_STYLE = "-fx-spacing: inherit;";
    private static final String INFO_BAR_STYLE = "-fx-padding: 4; -fx-background-color: white;";
    private static final String BONUS_BOXES_STYLE = "-fx-spacing: 10; -fx-padding: 4; -fx-background-color: #c3c3c3;";
    private static final String SEPARATOR_STYLE = "-fx-background-color: #4f4f4f";
    private static final String COLORPICKER_STYLE = "-fx-color-label-visible: false ;";
    private static final String CELEST_TITLE_STYLE = "-fx-font-weight: bold";

    private static final String TOOLTIP_DEFAULT_STYLE = "-fx-background-color: #FF0000;";
    private static final Duration TOOLTIP_SHOW_WAIT = Duration.millis(250);
    private static final Duration TOOLTIP_HIDE_WAIT = Duration.ZERO;

    private static final String PLAY_BUTTON_TEXT = "\uf04b";
    private static final String PAUSE_BUTTON_TEXT = "\uf04c";
    private static final String RESET_BUTTON_TEXT = "\uf0e2";
    private static final String SETTINGS_BUTTON_TXT = "\uf013";
    private static final String TOGGLE_GUI = "\uF03E";
    private static final String FULLSCREEN_SET_ON = "\uF065";
    private static final String FULLSCREEN_SET_OFF = "\uF066";
    private static final String RIGHT_PANEL_IS_ON = "\uF06E\uF05A";
    private static final String RIGHT_PANEL_IS_OFF = "\uF070\uF05A";
    private static final String EXTEND_ALT_IS_ON = "\uF058\uF0DC";
    private static final String EXTEND_ALT_IS_OFF = "\uF05E\uF0DC";
    private static final String TRANSLATION_LABEL = "\uF0B2";
    private static final String SCROLL_LABEL = "\uF00E";
    private static final String SEARCH_LABEL = "\uF002";
    private static final String RESETDEFAULT_BUTTON = "\uF021";
    private static final String GRID_LABEL = "\uF0AC";
    private static final String PHASE_LABEL = "\uF042";
    private static final String ORBIT_LENGTH_LABEL = "\uF060\uF061";
    private static final String ORBIT_RESO_LABEL = "\uF041";
    private static final String MIN_LENGTH_LABEL = "-";
    private static final String MAX_LENGTH_LABEL = "+";

    private static final String HELPTXT_LONGITUDE = "Longitude géographique d'observation. (°)";
    private static final String HELPTXT_LATITUDE = "Latitude géographique d'observation. (°)";
    private static final String HELPTXT_DATE = "Date d'observation.";
    private static final String HELPTXT_HOUR = "Instant d'observation.";
    private static final String HELPTXT_ZONE = "Fuseau horaire d'observation.";
    private static final String HELPTXT_ACC = "Accélérateur de temps.";
    private static final String HELPTXT_RESET_TIME = "Met la date et l'instant\nd'observation à l'instant\n" +
            "actuel réel, ie la date,\nl'instant et le fuseau\nhoraire du système.";
    private static final String HELPTXT_PAUSE_ACC = "Pause la simulation.";
    private static final String HELPTXT_START_ACC = "Commence la simulation avec\nl'accélérateur sélectionné.";
    private static final String HELPTXT_SEARCH = "Recherche d'un corps céleste\nà partir du nom, cliquez\nsur un résultat pour" +
            "\ncentrer la projection sur\nl'objet choisi, même s'il\nne figure pas parmi les\nobjets à dessiner (configurés\n" +
            "dans les paramètres).";
    private static final String HELPTXT_TOGGLEGUI = "Cache la GUI, appuyez\nune touche non utilisée du\nclavier (ex: F)" +
            " pour la\nfaire réapparaître.";
    private static final String HELPTXT_FULLSCREEN_ON = "Désactiver le plein écran.";
    private static final String HELPTXT_FULLSCREEN_OFF = "Activer le plein écran.";
    private static final String HELPTXT_RIGHT_IS_ON = "Désactiver le panneau d'infos\n" +
            "à droite s'affichant sur clic droit\nd'un objet céleste.";
    private static final String HELPTXT_RIGHT_IS_OFF = "Activer le panneau d'infos\n" +
            "à droite s'affichant sur clic droit\nd'un objet céleste.";
    private static final String HELPTXT_EXTEND_IS_ON = "Restreindre l'altitude à\nl'intervalle [5°,90°].";
    private static final String HELPTXT_EXTEND_IS_OFF = "Etendre l'altitude à\nl'intervalle [-90°,90°].";
    private static final String HELPTXT_PARAMS_OFF = "Montrer les paramètres.";
    private static final String HELPTXT_PARAMS_ON = "Cacher les paramètres.";
    private static final String HELPTXT_DRAG_SENS = "Maintenez enfoncé le bouton\ndroit de la souris en\nle déplaçant dans la\n" +
            "direction de changement\nde centre de projection\nvoulue. Ce slider change\nla sensibilité de ces\n" +
            "déplacements.";
    private static final String HELPTXT_SCROLL_SENS = "Ce slider change la\nsensibilité de la roulette de\nla souris, gérant le\n" +
            "zoom, ie le champ\nde vue.";
    private static final String HELPTXT_RESETDEF = "Remet les valeurs des\nsensibilités par défaut.";
    private static final String HELPTXT_OBJECTS_TO_DRAW = "Sélectionnez les objets à dessiner.";
    private static final String HELPTXT_FULLSCREEN = "Appuyez sur ECHAP pour quitter le mode plein écran.";
    private static final String HELPTXT_GRIDSPACE = "Choisissez l'espacement entre les\nparallèles représentés sur la grille.\n" +
            "(unité : degrés) Possible perte de\nfluidité dans les déplacements\nà glissements de souris pour <= 15°:\n" +
            "augmentez la sensibilité au besoin.";
    private static final String HELPTXT_RA = "Première coordonnée du système\néquatorial, angle entre le point" +
            "\nvernal et le corps considéré.";
    private static final String HELPTXT_DEC = "Deuxième coordonnée du système\néquatorial, angle entre le plan" +
            "\nde l'équateur et le corps considéré.";
    private static final String HELPTXT_ANGSIZE = "Angle formé entre 2 points\ndiamétralement opposés de l'objet\nconsidéré.";
    private static final String HELPTXT_MAG = "Mesure de l'irradiance d'un objet\ncéleste vu depuis la Terre.";
    private static final String HELPTXT_INFODATE = "Date à l'instant d'observation\nde ce corps céleste.";
    private static final String HELPTXT_INFOHOUR = "Heure à l'instant d'observation\nde ce corps céleste.";
    private static final String HELPTXT_INFOZONE = "Zone à l'instant d'observation\nde ce corps céleste.";
    private static final String HELPTXT_INFOLON = "Longitude géographique à l'instant\n d'observationde ce corps céleste.";
    private static final String HELPTXT_INFOLAT = "Latitude géographique à l'instant\n d'observationde ce corps céleste.";
    private static final String HELPTXT_HIPPARCOS = "Numéro d'identification stellaire.";
    private static final String HELPTXT_PHASE = "Pourcentage illuminé de la Lune.";
    private static final String HELPTXT_MEANANOM = "Pas assez de place pour expliquer ici :)";
    private static final String HELPTXT_ECLIPLON = "Angle formé entre le point vernal,\npoint d'intersection du plan\n" +
            "équatorial, l'écliptique ie\nle plan dans lequel tourne la\nTerre autour du Soleil, et\nla sphère terrestre,\n" +
            "et la projection du corps\nconsidéré sur l'écliptique.\n\nPar construction, la latitude\nécliptique du soleil " +
            "est\ntoujours nulle.";
    private static final String HELPTXT_RESOLUTION = "Résolution de l'orbite, ie\nla densité de représentants.";
    private static final String HELPTXT_LENGTH = "Longueur de l'orbite simulée.";
    private static final String HELPTXT_ORBITPARAMS = "Modification de la résolution\net la longueur de l'orbite,\nla couleur" +
            " peut être modifiée\ndans le menu de paramètres.";
    private static final String HELPTXT_GUILESS_POPUP = "Appuyez sur F pour afficher la GUI.\nCliquez pour cacher ce message.";
    //Actually, any key that is not mapped to anything else works, and any mouse input suffices to
    //hide this tooltip, but let's keep things simple.

    private static final Color CONTROLBAR_TEXT_COLOR = Color.color(0.225, 0.225, 0.225);
    private static final double PARAMS_GRIDGAP = 4d;
    private static final double RIGHTBOX_GRIDGAP = 2d;
    private static final int GRID_TODRAW_WIDTH = 2;
    private static final int GRID_TODRAW_HEIGHT = 4;
    private static final double PARAMS_SLIDER_WIDTH = 125d;
    private static final double ORBIT_SLIDER_WIDTH = 160d;
    private static final double TRANSSLIDER_MIN = 0.25;
    private static final double TRANSSLIDER_MAX = 6;
    private static final double SCROLLSLIDER_MIN = 0.5;
    private static final double SCROLLSLIDER_MAX = 1.5;
    private static final double RESOSLIDER_MIN = 1;
    private static final double RESOSLIDER_MAX = 40;
    private static final double LENGTHSLIDER_MIN = 200;
    private static final double LENGTHSLIDER_MAX = 3000;

    private static BorderPane mainBorder;
    private static final List<Tooltip> toolTipList = new ArrayList<>();
    private static final int DEFAULT_NBR_DECIMALS = 2;
    private static final int STRINGS_PER_LINE = 3;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * JavaFX application start method, creating all links, nodes, and other items
     *
     * @param primaryStage (Stage) used window
     */
    @Override
    public void start(Stage primaryStage) {
        try (InputStream hs = resourceStream(INPUT_HYGDATA);
             InputStream ast = resourceStream(INPUT_ASTERISMS);
             InputStream fs = resourceStream(INPUT_FONT);
             InputStream fsSmall = resourceStream(INPUT_FONT)) {

            // I/ Initialization -------------------------------------------
            Font fontAwesomeDefault = Font.loadFont(fs, CUSTOM_FONT_DEFAULT_SIZE);
            Font fontAwesomeSmall = Font.loadFont(fsSmall, CUSTOM_FONT_SMALL_SIZE);
            //Using the same InputStream was causing an NPE even StackOverflow had no answer for.
            BlackBodyColor.init();

            StarCatalogue catalogue = new StarCatalogue.Builder()
                    .loadFrom(hs, HygDatabaseLoader.INSTANCE)
                    .loadFrom(ast, AsterismLoader.INSTANCE)
                    .build();

            ZonedDateTime when = ZonedDateTime.now();
            DateTimeBean dateTimeBean = new DateTimeBean();
            dateTimeBean.setZonedDateTime(when);

            ObserverLocationBean observerLocationBean = new ObserverLocationBean();
            observerLocationBean.setCoordinates(INITIAL_GEO_COORDS);

            ViewingParametersBean viewingParametersBean = new ViewingParametersBean();
            viewingParametersBean.setCenter(INITIAL_CENTER);
            viewingParametersBean.setFieldOfViewDeg(INITIAL_FOV);

            TimeAnimator animator = new TimeAnimator(dateTimeBean);
            SkyCanvasManager manager = new SkyCanvasManager(catalogue, dateTimeBean, observerLocationBean, viewingParametersBean);

            List<Label> labelList = new ArrayList<>();

            // II/ Top bar: ------------------------------------------------
            // NEW a/ Search and options:
            Label searchLabel = new Label(SEARCH_LABEL);
            searchLabel.setFont(fontAwesomeDefault);
            labelList.add(searchLabel);
            Button toOptionsButton = new Button(SETTINGS_BUTTON_TXT);
            toOptionsButton.setFont(fontAwesomeDefault);
            Tooltip paramsTip = new Tooltip();
            toOptionsButton.setTooltip(paramsTip);
            toolTipList.add(paramsTip);
            addTooltip(searchLabel, HELPTXT_SEARCH);
            addTooltip(manager.searcher(), HELPTXT_SEARCH);

            HBox searchHBox = new HBox(searchLabel, manager.searcher(), toOptionsButton);
            searchHBox.setStyle(SEARCH_HBOX_STYLE);

            // b/ Position:
            Label longLabel = new Label("Longitude (°) :");
            Label latLabel = new Label("Latitude (°) :");
            labelList.add(longLabel);
            labelList.add(latLabel);
            TextField longTextField = new TextField();
            TextField latTextField = new TextField();
            longTextField.setStyle(POSITION_TXTFIELDS_STYLE);
            latTextField.setStyle(POSITION_TXTFIELDS_STYLE);

            addTooltip(longLabel, HELPTXT_LONGITUDE);
            addTooltip(latLabel, HELPTXT_LATITUDE);

            NumberStringConverter positionConverter = new NumberStringConverter("#0.00");

            UnaryOperator<TextFormatter.Change> lonFilter = coordFilter(positionConverter, true);
            UnaryOperator<TextFormatter.Change> latFilter = coordFilter(positionConverter, false);

            TextFormatter<Number> lonTextFormatter = new TextFormatter<>(positionConverter, 0, lonFilter);
            TextFormatter<Number> latTextFormatter = new TextFormatter<>(positionConverter, 0, latFilter);

            longTextField.setTextFormatter(lonTextFormatter);
            latTextField.setTextFormatter(latTextFormatter);

            lonTextFormatter.valueProperty().bindBidirectional(observerLocationBean.lonDegProperty());
            latTextFormatter.valueProperty().bindBidirectional(observerLocationBean.latDegProperty());

            HBox positionHBox = new HBox(longLabel, longTextField, latLabel, latTextField);
            positionHBox.setStyle(POSITION_HBOX_STYLE);

            // c/ Date, time and zone:
            Label dateLabel = new Label("Date :");
            labelList.add(dateLabel);
            DatePicker datePicker = new DatePicker();
            datePicker.setStyle(TIME_DATEPICK_STYLE);
            datePicker.valueProperty().bindBidirectional(dateTimeBean.dateProperty());
            addTooltip(dateLabel, HELPTXT_DATE);

            Label hourLabel = new Label("Heure :");
            labelList.add(hourLabel);
            TextField hourTextField = new TextField();
            hourTextField.setStyle(TIME_TXTFIELD_STYLE);
            addTooltip(hourLabel, HELPTXT_HOUR);

            DateTimeFormatter hmsFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalTimeStringConverter hourConverter = new LocalTimeStringConverter(hmsFormatter, hmsFormatter);
            TextFormatter<LocalTime> timeFormatter = new TextFormatter<>(hourConverter);
            hourTextField.setTextFormatter(timeFormatter);
            timeFormatter.valueProperty().bindBidirectional(dateTimeBean.timeProperty());

            ComboBox<ZoneId> zoneList = new ComboBox<>();
            zoneList.setStyle(TIME_ZONECOMBOBOX_STYLE);
            zoneList.getItems().addAll(ZoneId.getAvailableZoneIds()
                    .stream()
                    .sorted()
                    .map(ZoneId::of)
                    .collect(Collectors.toList()));
            zoneList.valueProperty().bindBidirectional(dateTimeBean.zoneProperty());
            addTooltip(zoneList, HELPTXT_ZONE);

            HBox timeHBox = new HBox(dateLabel, datePicker, hourLabel, hourTextField, zoneList);
            timeHBox.setStyle(TIME_HBOX_STYLE);

            // d/ Acceleration control:
            ComboBox<NamedTimeAccelerator> acceleratorList =
                    new ComboBox<>(FXCollections.observableArrayList(NamedTimeAccelerator.values()));
            acceleratorList.valueProperty().addListener((p, o, n) -> animator.setAccelerator(n.getAccelerator()));
            /* This does the same job as the recommended Bindings.select way but seems more concise. */
            /* We also thought a comboBox looks better here... */
            acceleratorList.setValue(INITIAL_ACCELERATOR);
            addTooltip(acceleratorList, HELPTXT_ACC);

            Button resetButton = new Button(RESET_BUTTON_TEXT);
            Button startPauseButton = new Button();
            resetButton.setFont(fontAwesomeDefault);
            startPauseButton.setFont(fontAwesomeDefault);
            bindTextToBoolean(startPauseButton.textProperty(),
                    animator.runningProperty(), PAUSE_BUTTON_TEXT, PLAY_BUTTON_TEXT);
            addTooltip(resetButton, HELPTXT_RESET_TIME);

            Tooltip startPauseTooltip = new Tooltip();
            toolTipList.add(startPauseTooltip);
            startPauseButton.setTooltip(startPauseTooltip);
            bindTextToBoolean(startPauseTooltip.textProperty(), animator.runningProperty(),
                    HELPTXT_PAUSE_ACC, HELPTXT_START_ACC);

            startPauseButton.setOnAction(e -> {
                if (animator.isRunning()) {
                    animator.stop();
                } else {
                    animator.start();
                }
            });

            resetButton.setOnAction(click -> dateTimeBean.setZonedDateTime(ZonedDateTime.now()));

            HBox accelerationHbox = new HBox(acceleratorList, resetButton, startPauseButton);
            accelerationHbox.setStyle(ACC_HBOX_STYLE);

            // e/ Misc:
            Stream.of(hourTextField, datePicker, zoneList, acceleratorList, resetButton)
                    .forEach(node -> node.disableProperty().bind(animator.runningProperty()));

            labelList.forEach(node -> node.setTextFill(CONTROLBAR_TEXT_COLOR));

            HBox controlBar = new HBox(searchHBox, new Separator(Orientation.VERTICAL),
                    positionHBox, new Separator(Orientation.VERTICAL), timeHBox,
                    new Separator(Orientation.VERTICAL), accelerationHbox);
            controlBar.setStyle(CONTROL_BAR_STYLE);

            // III/ Canvas: ------------------------------------------------
            Pane canvasPane = new Pane(manager.canvas());
            manager.canvas().widthProperty().bind(canvasPane.widthProperty());
            manager.canvas().heightProperty().bind(canvasPane.heightProperty());

            // IV/ Bottom bar: ---------------------------------------------
            Text fovText = new Text();
            fovText.textProperty().bind(Bindings.format(Locale.ROOT, "Champ de vue : %.1f°",
                    viewingParametersBean.fieldOfViewDegProperty()));

            Text objectUnderMouseText = new Text();
            manager.objectUnderMouseProperty().addListener((p, o, n) ->
                    n.ifPresentOrElse(celestialObject -> objectUnderMouseText.setText(celestialObject.info()),
                            () -> objectUnderMouseText.setText("")));

            Text mousePosText = new Text();
            mousePosText.textProperty().bind(Bindings.format(Locale.ROOT, "Azimut : %.2f°, hauteur : %.2f°",
                    manager.mouseAzDegProperty(), manager.mouseAltDegProperty()));

            BorderPane informationBar = new BorderPane(objectUnderMouseText, null, mousePosText, null, fovText);
            informationBar.setStyle(INFO_BAR_STYLE);

            // NEW V/ Parameters menu (left border): -----------------------
            // a/ Top check buttons:
            /* Using buttons instead of checkboxes for graphical malleability */
            Button toggleGUI = new Button(TOGGLE_GUI);
            toggleGUI.setFont(fontAwesomeDefault);
            addTooltip(toggleGUI, HELPTXT_TOGGLEGUI);

            BooleanProperty guiIsON = new SimpleBooleanProperty(true);

            Tooltip guiPopup = new Tooltip(HELPTXT_GUILESS_POPUP);
            guiPopup.setAutoHide(true);

            toggleGUI.setOnAction(e -> {
                manager.setNonFunctionalKeyPressed(false);
                guiIsON.set(false);
                manager.canvas().requestFocus();
                guiPopup.show(primaryStage);
            });

            guiIsON.addListener((p, o, newBoolean) ->
                    Stream.of(mainBorder.getTop(), mainBorder.getRight(), mainBorder.getLeft(), mainBorder.getBottom())
                            .forEach(e -> {
                                e.setVisible(newBoolean);
                                e.setManaged(newBoolean);
                            }));

            manager.nonFunctionalKeyPressedProperty().addListener((p, o, n) -> {
                if (!guiIsON.get() && n) {
                    guiIsON.set(true);
                    guiPopup.hide();
                }
            });

            Button toggleFullscreen = new Button();
            Tooltip toggleFullscreenTooltip = new Tooltip();
            toolTipList.add(toggleFullscreenTooltip);
            toggleFullscreen.setFont(fontAwesomeDefault);
            toggleFullscreen.setTooltip(toggleFullscreenTooltip);
            toggleFullscreen.setOnAction(e -> primaryStage.setFullScreen(!primaryStage.isFullScreen()));
            /* FullScreenProperty is readonly in Stage, not allowing us to use the nifty link method below. */
            bindTextToBoolean(toggleFullscreenTooltip.textProperty(), primaryStage.fullScreenProperty(),
                    HELPTXT_FULLSCREEN_ON, HELPTXT_FULLSCREEN_OFF);
            bindTextToBoolean(toggleFullscreen.textProperty(),
                    primaryStage.fullScreenProperty(), FULLSCREEN_SET_OFF, FULLSCREEN_SET_ON);

            Button rightClickInfo = new Button();
            rightClickInfo.setFont(fontAwesomeDefault);
            Tooltip rightClickToolTip = new Tooltip();
            toolTipList.add(rightClickToolTip);
            rightClickInfo.setTooltip(rightClickToolTip);
            BooleanProperty rightPanelIsON = new SimpleBooleanProperty(true);
            linkAndBindText(rightClickInfo, rightPanelIsON, RIGHT_PANEL_IS_ON, RIGHT_PANEL_IS_OFF);
            bindTextToBoolean(rightClickToolTip.textProperty(), rightPanelIsON, HELPTXT_RIGHT_IS_ON,
                    HELPTXT_RIGHT_IS_OFF);

            Button toggleExtendedAlt = new Button();
            toggleExtendedAlt.setFont(fontAwesomeDefault);
            Tooltip extendedAltTooltip = new Tooltip();
            toolTipList.add(extendedAltTooltip);
            toggleExtendedAlt.setTooltip(extendedAltTooltip);
            linkAndBindText(toggleExtendedAlt, manager.extendedAltitudeIsOnProperty(), EXTEND_ALT_IS_ON, EXTEND_ALT_IS_OFF);
            bindTextToBoolean(extendedAltTooltip.textProperty(), manager.extendedAltitudeIsOnProperty(),
                    HELPTXT_EXTEND_IS_ON, HELPTXT_EXTEND_IS_OFF);

            GridPane layerOneGrid = new GridPane();
            layerOneGrid.addRow(0, toggleGUI, rightClickInfo);
            layerOneGrid.addRow(1, toggleFullscreen, toggleExtendedAlt);
            layerOneGrid.setVgap(PARAMS_GRIDGAP);
            layerOneGrid.setHgap(PARAMS_GRIDGAP);
            layerOneGrid.setAlignment(Pos.CENTER);

            Stream.of(toggleGUI, toggleFullscreen, toggleExtendedAlt, rightClickInfo)
                    .forEach(button -> button.setMaxWidth(Double.MAX_VALUE));
            // A javaFX hack to make buttons occupy all the space they can, because unlike for other nodes,
            // buttons will not do so 'automatically'.

            Separator firstSeparator = new Separator(Orientation.HORIZONTAL);
            firstSeparator.setStyle(SEPARATOR_STYLE);

            // b/ Middle sliders:
            Label sensLabel = new Label("Sensibilités :");

            Label dragSensLabel = new Label(TRANSLATION_LABEL);
            dragSensLabel.setFont(fontAwesomeDefault);
            addTooltip(dragSensLabel, HELPTXT_DRAG_SENS);

            Slider translationSlider = new Slider(TRANSSLIDER_MIN, TRANSSLIDER_MAX,
                    manager.getMouseDragSensitivity() * manager.getMouseDragFactor());
            manager.mouseDragSensitivityProperty().bind(translationSlider.valueProperty().divide(manager.getMouseDragFactor()));

            Label scrollSensLabel = new Label(SCROLL_LABEL);
            scrollSensLabel.setFont(fontAwesomeDefault);
            addTooltip(scrollSensLabel, HELPTXT_SCROLL_SENS);

            Slider scrollSlider = new Slider(SCROLLSLIDER_MIN, SCROLLSLIDER_MAX, manager.getMouseScrollSensitivity());
            manager.mouseScrollSensitivityProperty().bind(scrollSlider.valueProperty());

            Stream.of(translationSlider, scrollSlider)
                    .forEach(slider -> {
                        slider.setShowTickLabels(true);
                        slider.setMinWidth(PARAMS_SLIDER_WIDTH);
                        slider.setMaxWidth(PARAMS_SLIDER_WIDTH);
                    });

            Button resetSensButton = new Button(RESETDEFAULT_BUTTON);
            resetSensButton.setFont(fontAwesomeDefault);
            resetSensButton.setOnAction(e -> {
                translationSlider.setValue(MOUSE_DRAG_DEFAULTSENS);
                scrollSlider.setValue(MOUSE_SCROLL_DEFAULTSENS);
            });
            addTooltip(resetSensButton, HELPTXT_RESETDEF);

            VBox slidersVBox = new VBox(sensLabel, spaceLabel(), dragSensLabel, translationSlider,
                    scrollSensLabel, scrollSlider, resetSensButton);
            slidersVBox.setAlignment(Pos.CENTER);

            Separator secondSeparator = new Separator(Orientation.HORIZONTAL);
            secondSeparator.setStyle(SEPARATOR_STYLE);

            // c/ Final checkboxes and color pickers:
            Label drawLabel = new Label("Dessiner :");
            addTooltip(drawLabel, HELPTXT_OBJECTS_TO_DRAW);

            GridPane checkBoxesToDraw = new GridPane();
            ObservableList<CheckBox> drawablesList = FXCollections.observableArrayList();
            DrawableObjects[] allPossibleDrawables = DrawableObjects.values();
            int indexOfHorizon = 0, indexOfStars = 0, indexOfOrbit = 0;
            for (int i = 0; i < allPossibleDrawables.length; ++i) {
                CheckBox draw = new CheckBox(allPossibleDrawables[i].getName());
                draw.setSelected(allPossibleDrawables[i] != DrawableObjects.GRID);
                draw.selectedProperty().addListener((p, o, n) -> {
                    EnumSet<DrawableObjects> nextSet = EnumSet.copyOf(manager.getObjectsToDraw());
                    if (n) {
                        nextSet.add(DrawableObjects.getDrawableFromString(draw.getText()));
                    } else {
                        nextSet.remove(DrawableObjects.getDrawableFromString(draw.getText()));
                    }
                    manager.setObjectsToDraw(nextSet);
                });
                drawablesList.add(draw);
                if (allPossibleDrawables[i] == DrawableObjects.STARS) indexOfStars = i;
                if (allPossibleDrawables[i] == DrawableObjects.HORIZON) indexOfHorizon = i;
                if (allPossibleDrawables[i] == DrawableObjects.ORBIT) indexOfOrbit = i;
            }

            Collections.swap(drawablesList, indexOfHorizon, indexOfStars);
            CheckBox orbitDrawingCheckbox = drawablesList.get(indexOfOrbit);

            Iterator<CheckBox> checkBoxIt = drawablesList.iterator();
            for (int i = 0; i < GRID_TODRAW_WIDTH; ++i) {
                for (int j = 0; j < GRID_TODRAW_HEIGHT; ++j) {
                    checkBoxesToDraw.add(checkBoxIt.next(), i, j);
                }
            }

            checkBoxesToDraw.setVgap(PARAMS_GRIDGAP);
            checkBoxesToDraw.setHgap(PARAMS_GRIDGAP);

            GridPane colorsGrid = new GridPane();
            int numberOfColorLabels = manager.colorLabelsList().size();
            Label[] colorLabels = new Label[numberOfColorLabels];
            ColorPicker[] colorPickers = new ColorPicker[numberOfColorLabels];
            Button[] resetColorButtons = new Button[numberOfColorLabels];

            for (int i = 0; i < numberOfColorLabels; ++i) {
                colorPickers[i] = new ColorPicker();
                colorPickers[i].valueProperty().bindBidirectional(manager.colorPropertiesList().get(i));
                colorPickers[i].setStyle(COLORPICKER_STYLE);

                colorLabels[i] = new Label(manager.colorLabelsList().get(i));
                GridPane.setHalignment(colorLabels[i], HPos.RIGHT);

                resetColorButtons[i] = new Button(RESETDEFAULT_BUTTON);
                resetColorButtons[i].setFont(fontAwesomeSmall);
                int finalI = i;
                resetColorButtons[i].setOnAction(e ->
                        manager.colorPropertiesList().get(finalI).set(SkyCanvasManager.getDefaultColorsList().get(finalI)));
            }

            colorsGrid.addColumn(0, colorLabels);
            colorsGrid.addColumn(1, colorPickers);
            colorsGrid.addColumn(2, resetColorButtons);

            Label gridSpaceLabel = new Label(GRID_LABEL);
            gridSpaceLabel.setFont(fontAwesomeDefault);
            addTooltip(gridSpaceLabel, HELPTXT_GRIDSPACE);

            ComboBox<String> spacingsBox = new ComboBox<>();
            spacingsBox.setItems(FXCollections.observableArrayList(manager.suggestedGridSpacings()));
            spacingsBox.setValue(manager.getHorizCoordsGridSpacingDeg() + "°");
            spacingsBox.valueProperty().addListener((p, o, n) ->
                    manager.setHorizCoordsGridSpacingDeg(Integer.parseInt(n.substring(0, n.length() - 1))));

            HBox gridSizeHBox = new HBox(gridSpaceLabel, spacingsBox);
            gridSizeHBox.setSpacing(PARAMS_GRIDGAP);
            gridSizeHBox.setAlignment(Pos.CENTER);

            VBox drawingVbox = new VBox(drawLabel, checkBoxesToDraw, spaceLabel(), gridSizeHBox, spaceLabel(), colorsGrid);

            // d/ Finalization
            VBox parametersBox = new VBox(layerOneGrid, firstSeparator, slidersVBox, secondSeparator, drawingVbox);
            parametersBox.setStyle(BONUS_BOXES_STYLE);
            setVisibleAndManaged(parametersBox, false);

            // NEW VI/ Information on celestial object panel (right border):
            // a/ Common information:
            Text celestNameLabel = new Text();
            celestNameLabel.setStyle(CELEST_TITLE_STYLE);

            Separator firstRightSeparator = new Separator(Orientation.HORIZONTAL);
            firstRightSeparator.setStyle(SEPARATOR_STYLE);

            GridPane basicInfo = new GridPane();
            Label[] basicInfoLabels = {
                    new Label("Ascension droite (hr) : "),
                    new Label("Déclinaison (°) : "),
                    new Label("Taille angulaire (°) : "),
                    new Label("Magnitude : "),
                    new Label("Date : "),
                    new Label("Heure : "),
                    new Label("Fuseau : "),
                    new Label("Longitude (°) : "),
                    new Label("Latitude (°) : ")
            };
            addTooltips(basicInfoLabels, HELPTXT_RA, HELPTXT_DEC, HELPTXT_ANGSIZE, HELPTXT_MAG, HELPTXT_INFODATE,
                    HELPTXT_INFOHOUR, HELPTXT_INFOZONE, HELPTXT_INFOLON, HELPTXT_INFOLAT);
            Text[] basicInfoValues = emptyTextArrayOfSize(basicInfoLabels.length);
            formatInfoGridPane(basicInfo, basicInfoLabels, basicInfoValues);

            Separator secondRightSeparator = new Separator(Orientation.HORIZONTAL);
            secondRightSeparator.setStyle(SEPARATOR_STYLE);

            // b/ Star-specific info:
            Label relatedStarsText = new Label();
            relatedStarsText.setTextAlignment(TextAlignment.CENTER);

            GridPane starsInfo = new GridPane();
            Label[] starsInfoLabels = {
                    new Label("Température (K) : "),
                    new Label("Hipparcos : ")
            };
            addTooltip(starsInfoLabels[1], HELPTXT_HIPPARCOS);
            Text[] starsInfoValues = emptyTextArrayOfSize(starsInfoLabels.length);
            formatInfoGridPane(starsInfo, starsInfoLabels, starsInfoValues);

            VBox starsSpecifics = new VBox(starsInfo, relatedStarsText);
            starsSpecifics.setAlignment(Pos.CENTER);

            Separator possibleSeparator = new Separator(Orientation.HORIZONTAL);
            possibleSeparator.setStyle(SEPARATOR_STYLE);

            // c/ Moon-specific info:
            GridPane moonSpecifics = new GridPane();
            Label[] moonInfoLabels = {
                    new Label(PHASE_LABEL)
            };
            moonInfoLabels[0].setFont(fontAwesomeDefault);
            addTooltips(moonInfoLabels, HELPTXT_PHASE);
            Text[] moonInfoValues = emptyTextArrayOfSize(moonInfoLabels.length);
            formatInfoGridPane(moonSpecifics, moonInfoLabels, moonInfoValues);
            //Left as array for ease of adding (hypothetical) information later

            // d/ Sun-specific info:
            GridPane sunSpecifics = new GridPane();
            Label[] sunInfoLabels = {
                    new Label("Anomalie moyenne (°) : "),
                    new Label("Longitude écliptique (°) : ")
            };
            addTooltips(sunInfoLabels, HELPTXT_MEANANOM, HELPTXT_ECLIPLON);
            Text[] sunInfoValues = emptyTextArrayOfSize(sunInfoLabels.length);
            formatInfoGridPane(sunSpecifics, sunInfoLabels, sunInfoValues);

            // e/ Orbit drawing sliders:
            Label orbitLabel = new Label("Paramètres d'orbite :");
            addTooltip(orbitLabel, HELPTXT_ORBITPARAMS);

            Label resolutionLabel = new Label(ORBIT_RESO_LABEL);
            resolutionLabel.setFont(fontAwesomeDefault);
            addTooltip(resolutionLabel, HELPTXT_RESOLUTION);

            Label lengthLabel = new Label(ORBIT_LENGTH_LABEL);
            lengthLabel.setFont(fontAwesomeDefault);
            addTooltip(lengthLabel, HELPTXT_LENGTH);

            Slider resolutionSlider = new Slider(RESOSLIDER_MIN, RESOSLIDER_MAX, manager.getOrbitDrawingStep());
            Slider lengthSlider = new Slider(LENGTHSLIDER_MIN, LENGTHSLIDER_MAX, manager.getDrawOrbitUntil());

            resolutionSlider.valueProperty().bindBidirectional(manager.orbitDrawingStepProperty());
            lengthSlider.valueProperty().bindBidirectional(manager.drawOrbitUntilProperty());

            Stream.of(resolutionSlider, lengthSlider).forEach(slider -> {
                slider.setMinWidth(ORBIT_SLIDER_WIDTH);
                slider.setMaxWidth(ORBIT_SLIDER_WIDTH);
                slider.disableProperty().bind(orbitDrawingCheckbox.selectedProperty().not());
            });
            resolutionSlider.setLabelFormatter(new StringConverter<>() {
                @Override
                public String toString(Double object) {
                    return String.valueOf(1/object);
                }

                @Override
                public Double fromString(String string) {
                    return 1/Double.parseDouble(string);
                }
            });
            resolutionSlider.setShowTickLabels(true);
            lengthSlider.setShowTickLabels(true);
            lengthSlider.setMajorTickUnit(LENGTHSLIDER_MAX - LENGTHSLIDER_MIN);
            lengthSlider.setLabelFormatter(new StringConverter<Double>() {
                @Override
                public String toString(Double object) {
                    if (object == LENGTHSLIDER_MIN) return MIN_LENGTH_LABEL;
                    if (object == LENGTHSLIDER_MAX) return MAX_LENGTH_LABEL;
                    return String.valueOf(object);
                }
                @Override
                public Double fromString(String string) {
                    if (string.equals(MIN_LENGTH_LABEL)) return LENGTHSLIDER_MIN;
                    if (string.equals(MAX_LENGTH_LABEL)) return LENGTHSLIDER_MAX;
                    return Double.parseDouble(string);
                }
            });

            VBox orbitSliders = new VBox(orbitLabel, spaceLabel(), resolutionLabel, resolutionSlider, lengthLabel, lengthSlider);
            orbitSliders.setAlignment(Pos.CENTER);

            // f/ Finalization and update:
            VBox rightBox = new VBox(celestNameLabel, firstRightSeparator, basicInfo, secondRightSeparator, sunSpecifics,
                    moonSpecifics, starsSpecifics, possibleSeparator, orbitSliders);
            rightBox.setStyle(BONUS_BOXES_STYLE);
            rightBox.setAlignment(Pos.CENTER);

            manager.wantNewInformationPanelProperty().addListener((p, o, n) -> {

                setVisibleAndManaged(false, rightBox, starsSpecifics);
                setVisibleAndManaged(false, starsSpecifics.getChildren());
                setVisibleAndManaged(false, rightBox.getChildren());

                if (rightPanelIsON.get() && n != null) {
                    celestNameLabel.setText(" " + n.name() + " ");
                    setTexts(Arrays.copyOfRange(basicInfoValues, 0, 4), DEFAULT_NBR_DECIMALS, n.equatorialPos().raHr(),
                            n.equatorialPos().decDeg(), Angle.toDeg(n.angularSize()), n.magnitude());
                    setTexts(Arrays.copyOfRange(basicInfoValues, 4, basicInfoLabels.length),
                            dateTimeBean.getDate().toString(),
                            dateTimeBean.getTime().truncatedTo(ChronoUnit.SECONDS).toString(),
                            dateTimeBean.getZone().toString(),
                            doubleWithXdecimals(observerLocationBean.getLonDeg(), DEFAULT_NBR_DECIMALS),
                            doubleWithXdecimals(observerLocationBean.getLatDeg(), DEFAULT_NBR_DECIMALS));
                    setVisibleAndManaged(true, celestNameLabel, firstRightSeparator, basicInfo,
                            secondRightSeparator, rightBox);
                    if (n instanceof Star) {
                        setTexts(starsInfoValues, String.valueOf(((Star) n).colorTemperature()),
                                String.valueOf(((Star) n).hipparcosId()));
                        setVisibleAndManaged(true, starsSpecifics, starsInfo);

                        Optional<Set<Star>> relatedStars = catalogue.constellationOfStar((Star) n);
                        if (relatedStars.isPresent()) {
                            relatedStarsText.setText("Liée à :\n" + formatSetToString(relatedStars.get()));
                            setVisibleAndManaged(true, relatedStarsText);
                        }

                    } else {
                        if (n instanceof Sun) {
                            setTexts(sunInfoValues, DEFAULT_NBR_DECIMALS,
                                    Angle.toDeg(Angle.normalizePositive(((Sun) n).meanAnomaly())),
                                    ((Sun) n).eclipticPos().lonDeg());
                            setVisibleAndManaged(true, sunSpecifics, possibleSeparator);
                        } else if (n instanceof Moon) {
                            setTexts(moonInfoValues, doubleToPercent(((Moon) n).phase(), DEFAULT_NBR_DECIMALS));
                            setVisibleAndManaged(true, moonSpecifics, possibleSeparator);
                        }
                        setVisibleAndManaged(true, orbitSliders);
                    }
                }
            });
            setVisibleAndManaged(rightBox, false);
            setVisibleAndManaged(false, rightBox.getChildren());
            setVisibleAndManaged(false, starsSpecifics.getChildren());

            // VII/ Misc controls and styles: ------------------------------
            mainBorder = new BorderPane(canvasPane, controlBar, rightBox, informationBar, parametersBox);

            toOptionsButton.setOnAction(e -> setVisibleAndManaged(parametersBox, !parametersBox.isVisible()));
            bindTextToBoolean(paramsTip.textProperty(), parametersBox.visibleProperty(), HELPTXT_PARAMS_ON, HELPTXT_PARAMS_OFF);

            toolTipList.forEach(tooltip -> {
                tooltip.setHideDelay(TOOLTIP_HIDE_WAIT);
                tooltip.setShowDelay(TOOLTIP_SHOW_WAIT);
                tooltip.setStyle(TOOLTIP_DEFAULT_STYLE);
            });

            // VIII/ Showtime: ----------------------------------------------
            Scene mainScene = new Scene(mainBorder);
            primaryStage.setFullScreenExitHint(HELPTXT_FULLSCREEN);
            primaryStage.setScene(mainScene);
            primaryStage.setMinWidth(MIN_WIDTH);
            primaryStage.setMinHeight(MIN_HEIGHT);
            primaryStage.setTitle("Rigel");
            primaryStage.show();
            manager.canvas().requestFocus();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private InputStream resourceStream(String s) {
        return getClass().getResourceAsStream(s);
    }

    private static UnaryOperator<TextFormatter.Change> coordFilter(NumberStringConverter stringConverter, boolean isLon) {
        return (change -> {
            try {
                String newText = change.getControlNewText();
                double newCoordDeg = stringConverter.fromString(newText).doubleValue();
                return ((isLon && GeographicCoordinates.isValidLonDeg(newCoordDeg)) ||
                        (!isLon && GeographicCoordinates.isValidLatDeg(newCoordDeg))) ? change : null;
            } catch (Exception e) {
                return null;
            }
        });
    }

    private static void setVisibleAndManaged(Node node, boolean value) {
        node.setManaged(value);
        node.setVisible(value);
    }

    private static void setVisibleAndManaged(boolean value, Collection<Node> nodes) {
        for (Node node : nodes) setVisibleAndManaged(node, value);
    }

    private static void setVisibleAndManaged(boolean value, Node... nodes) {
        setVisibleAndManaged(value, Arrays.asList(nodes));
    }

    private static void bindTextToBoolean(StringProperty stringProp, ReadOnlyBooleanProperty booleanProperty,
                                          String trueValue, String falseValue) {
        stringProp.bind(Bindings.when(booleanProperty)
                .then(trueValue)
                .otherwise(falseValue));
    }

    private static void link(Button button, BooleanProperty booleanProp) {
        button.setOnAction(e -> booleanProp.set(!booleanProp.get()));
    }

    private static void linkAndBindText(Button button, BooleanProperty booleanProp, String trueValue, String falseValue) {
        link(button, booleanProp);
        bindTextToBoolean(button.textProperty(), booleanProp, trueValue, falseValue);
    }

    private static void addTooltip(Control controlNode, String helpText) {
        controlNode.setTooltip(new Tooltip(helpText));
        toolTipList.add(controlNode.getTooltip());
    }

    private static void addTooltips(Control[] controlNodes, String... helpTexts) {
        Preconditions.checkArgument(controlNodes.length == helpTexts.length);
        for(int i = 0; i < controlNodes.length; ++i) addTooltip(controlNodes[i], helpTexts[i]);
    }

    private static Label spaceLabel() {
        return new Label("\n");
    }

    private static void setGridPaneAlignment(HPos align, Node... nodes) {
        for (Node node : nodes) GridPane.setHalignment(node, align);
    }

    private static void setTexts(Text[] textsArray, int nbrOfDecimals, double... toConvert) {
        setTexts(textsArray, Arrays.stream(toConvert)
                .boxed()
                .map(nbr -> doubleWithXdecimals(nbr, nbrOfDecimals))
                .collect(Collectors.toList()));
    }

    private static void setTexts(Text[] textsArray, String... texts) {
        setTexts(textsArray, Arrays.asList(texts));
    }

    private static void setTexts(Text[] textsArray, List<String> texts) {
        Preconditions.checkArgument(textsArray.length == texts.size(), "Main.setLabelsTexts:" +
                "given number of strings =/= given number of texts.");
        for (int i = 0; i < textsArray.length; ++i) textsArray[i].setText(texts.get(i));
    }

    private static Text[] emptyTextArrayOfSize(int size) {
        Text[] textArray = new Text[size];
        for (int i = 0; i < size; ++i) textArray[i] = new Text();
        return textArray;
    }

    private static void formatInfoGridPane(GridPane grid, Label[] labels, Text[] values) {
        setGridPaneAlignment(HPos.RIGHT, labels);
        setGridPaneAlignment(HPos.LEFT, values);
        grid.addColumn(0, labels);
        grid.addColumn(1, values);
        grid.setHgap(RIGHTBOX_GRIDGAP);
        grid.setVgap(RIGHTBOX_GRIDGAP);
        grid.setAlignment(Pos.CENTER);
    }

    private static String doubleToPercent(double doubleToFormat, int numberOfDecimals) {
        return doubleWithXdecimals(doubleToFormat * 100, numberOfDecimals) + "%";
    }

    /**
     * Returns given double in String form up to given decimal
     *
     * @param doubleToFormat   (double) given double
     * @param numberOfDecimals (int) given decimal
     * @return (String) formatted double
     * @throws IllegalArgumentException if given number of decimals is (strictly) negative
     */
    private static String doubleWithXdecimals(double doubleToFormat, int numberOfDecimals) {
        Preconditions.checkArgument(numberOfDecimals >= 0, "Main.doubleWithXdecimals: given number" +
                " of decimals is < 0.");
        return String.format(Locale.ROOT, "%." + numberOfDecimals + "f", doubleToFormat);
    }

    private static String formatSetToString(Set<?> data) {
        StringBuilder buildingString = new StringBuilder();
        int i = 0;
        for (Object obj : data) {
            buildingString.append(obj).append(", ");
            if (i != 0 && i % STRINGS_PER_LINE == 0) buildingString.append("\n");
            ++i;
        }
        if (buildingString.charAt(buildingString.length() - 3) == ',')
            buildingString.deleteCharAt(buildingString.length() - 3);
        return buildingString.substring(0, buildingString.length() - 2) + ".";
    }
}
