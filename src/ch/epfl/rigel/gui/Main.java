package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
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
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.util.converter.NumberStringConverter;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
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
    private static final HorizontalCoordinates INITIAL_CENTER = HorizontalCoordinates.ofDeg(180.000000000001, 15);
    private static final NamedTimeAccelerator INITIAL_ACCELERATOR = NamedTimeAccelerator.TIMES_300;
    private static final double INITIAL_FOV = 100;
    private static final int MIN_WIDTH = 800;
    private static final int MIN_HEIGHT = 600;
    private static final double MOUSE_DRAG_DEFAULTSENS = 1;
    private static final double MOUSE_SCROLL_DEFAULTSENNS = 0.75;

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
    private static final String SETTINGS_BOX_STYLE = "-fx-spacing: 10; -fx-padding: 4; -fx-background-color: #c3c3c3;";
    private static final String SEPARATOR_STYLE = "-fx-background-color: #4f4f4f";

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

    private static final Color CONTROLBAR_TEXT_COLOR = Color.color(0.225, 0.225, 0.225);
    private static final double PARAMS_GRIDGAP = 4d;
    private static final double SLIDER_WIDTH = 125d;

    private static BorderPane mainBorder;

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
        try (InputStream hs = resourceStream("/hygdata_v3.csv");
             InputStream ast = resourceStream("/asterisms.txt");
             InputStream fs = resourceStream("/Font Awesome 5 Free-Solid-900.otf")) {

            // I/ Initialization -------------------------------------------
            Font fontAwesome = Font.loadFont(fs, 15);
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

            primaryStage.setMinWidth(MIN_WIDTH);
            primaryStage.setMinHeight(MIN_HEIGHT);
            primaryStage.setTitle("Rigel");

            List<Tooltip> toolTipList = new ArrayList<>();
            List<Label> labelList = new ArrayList<>();

            // II/ Top bar: ------------------------------------------------
            // NEW a/ Search and options:
            Label searchLabel = new Label(SEARCH_LABEL);
            searchLabel.setFont(fontAwesome);
            labelList.add(searchLabel);
            Button toOptionsButton = new Button(SETTINGS_BUTTON_TXT);
            toOptionsButton.setFont(fontAwesome);
            Tooltip paramsTip = new Tooltip();
            toOptionsButton.setTooltip(paramsTip);
            toolTipList.add(paramsTip);
            Tooltip searchToolTip = new Tooltip(HELPTXT_SEARCH);
            toolTipList.add(searchToolTip);
            searchLabel.setTooltip(searchToolTip);
            manager.searcher().setTooltip(searchToolTip);

            HBox searchHBox = new HBox(searchLabel, manager.searcher(), toOptionsButton);
            searchHBox.setStyle(SEARCH_HBOX_STYLE);

            // b/ Position:
            Label longLabel = new Label("Longitude (°) :");
            Label latLabel  = new Label("Latitude (°) :");
            labelList.add(longLabel); labelList.add(latLabel);
            TextField longTextField = new TextField();
            TextField latTextField = new TextField();
            longTextField.setStyle(POSITION_TXTFIELDS_STYLE);
            latTextField.setStyle(POSITION_TXTFIELDS_STYLE);

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

            Label hourLabel = new Label("Heure :");
            labelList.add(hourLabel);
            TextField hourTextField = new TextField();
            hourTextField.setStyle(TIME_TXTFIELD_STYLE);

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

            HBox timeHBox = new HBox(dateLabel, datePicker, hourLabel, hourTextField, zoneList);
            timeHBox.setStyle(TIME_HBOX_STYLE);

            // d/ Acceleration control:
            ComboBox<NamedTimeAccelerator> acceleratorList =
                    new ComboBox<>(FXCollections.observableArrayList(NamedTimeAccelerator.values()));
            acceleratorList.valueProperty().addListener((p, o, n) -> animator.setAccelerator(n.getAccelerator()));
            /* This does the same job as the recommended Bindings.select way but seems more concise. */
            /* We also thought a comboBox looks better here... */
            acceleratorList.setValue(INITIAL_ACCELERATOR);

            Button resetButton = new Button(RESET_BUTTON_TEXT);
            Button startPauseButton = new Button();
            resetButton.setFont(fontAwesome);
            startPauseButton.setFont(fontAwesome);
            bindTextToBoolean(startPauseButton.textProperty(),
                    animator.runningProperty(), PAUSE_BUTTON_TEXT, PLAY_BUTTON_TEXT);

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
            fovText.textProperty().bind(Bindings.format(Locale.ROOT,"Champ de vue : %.1f°",
                    viewingParametersBean.fieldOfViewDegProperty()));

            Text objectUnderMouseText = new Text();
            manager.objectUnderMouseProperty().addListener((p, o, n) ->
                    n.ifPresentOrElse(celestialObject -> objectUnderMouseText.setText(celestialObject.info()),
                            () -> objectUnderMouseText.setText("")));

            Text mousePosText = new Text();
            mousePosText.textProperty().bind(Bindings.format(Locale.ROOT,"Azimut : %.2f°, hauteur : %.2f°",
                    manager.mouseAzDegProperty(), manager.mouseAltDegProperty()));

            BorderPane informationBar = new BorderPane(objectUnderMouseText, null, mousePosText, null, fovText);
            informationBar.setStyle(INFO_BAR_STYLE);

            // NEW V/ Parameters menu (left border): -----------------------
            // a/ Top check buttons:
               /* Using buttons instead of checkboxes for graphical malleability */
            Button toggleGUI = new Button(TOGGLE_GUI);
            toggleGUI.setFont(fontAwesome);
            Tooltip toggleGUItoolTip = new Tooltip(HELPTXT_TOGGLEGUI);
            toolTipList.add(toggleGUItoolTip);
            toggleGUI.setTooltip(toggleGUItoolTip);

            BooleanProperty guiIsON = new SimpleBooleanProperty(true);

            toggleGUI.setOnAction(e -> {
                manager.setNonFunctionalKeyPressed(false);
                guiIsON.set(false);
                manager.canvas().requestFocus();
            });

            guiIsON.addListener((p, o, newBoolean) ->
                    Stream.of(mainBorder.getTop(), mainBorder.getRight(), mainBorder.getLeft(), mainBorder.getBottom())
                            .forEach(e -> {e.setVisible(newBoolean); e.setManaged(newBoolean);}));

            manager.nonFunctionalKeyPressedProperty().addListener((p, o, n) -> {
                if (!guiIsON.get() && n) {
                    guiIsON.set(true);
                }
            });

            Button toggleFullscreen = new Button();
            Tooltip toggleFullscreenTooltip = new Tooltip();
            toolTipList.add(toggleFullscreenTooltip);
            toggleFullscreen.setFont(fontAwesome);
            toggleFullscreen.setTooltip(toggleFullscreenTooltip);
            toggleFullscreen.setOnAction(e -> primaryStage.setFullScreen(!primaryStage.isFullScreen()));
                /* FullScreenProperty is readonly in Stage, not allowing us to use the nifty link method below. */
            bindTextToBoolean(toggleFullscreenTooltip.textProperty(), primaryStage.fullScreenProperty(),
                    HELPTXT_FULLSCREEN_ON, HELPTXT_FULLSCREEN_OFF);
            bindTextToBoolean(toggleFullscreen.textProperty(),
                    primaryStage.fullScreenProperty(), FULLSCREEN_SET_OFF, FULLSCREEN_SET_ON);

            //TODO rightPanelIsON: add listener to activate/deactive information right panel
            Button rightClickInfo = new Button();
            rightClickInfo.setFont(fontAwesome);
            Tooltip rightClickToolTip = new Tooltip();
            toolTipList.add(rightClickToolTip);
            rightClickInfo.setTooltip(rightClickToolTip);
            BooleanProperty rightPanelIsON = new SimpleBooleanProperty(true);
            linkAndBindText(rightClickInfo, rightPanelIsON, RIGHT_PANEL_IS_ON, RIGHT_PANEL_IS_OFF);
            bindTextToBoolean(rightClickToolTip.textProperty(), rightPanelIsON, HELPTXT_RIGHT_IS_ON,
                    HELPTXT_RIGHT_IS_OFF);

            Button toggleExtendedAlt = new Button();
            toggleExtendedAlt.setFont(fontAwesome);
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
            dragSensLabel.setFont(fontAwesome);
            Tooltip dragSensTooltip = new Tooltip(HELPTXT_DRAG_SENS);
            toolTipList.add(dragSensTooltip);
            dragSensLabel.setTooltip(dragSensTooltip);

            Slider translationSlider = new Slider(0.25, 6, manager.getMouseDragSensitivity()
                    * manager.getMouseDragFactor());
            manager.mouseDragSensitivityProperty().bind(translationSlider.valueProperty().divide(manager.getMouseDragFactor()));

            Label scrollSensLabel = new Label(SCROLL_LABEL);
            scrollSensLabel.setFont(fontAwesome);
            Tooltip scrollSensToolTip = new Tooltip(HELPTXT_SCROLL_SENS);
            toolTipList.add(scrollSensToolTip);
            scrollSensLabel.setTooltip(scrollSensToolTip);

            Slider scrollSlider = new Slider(0.5, 1.5, manager.getMouseScrollSensitivity());
            manager.mouseScrollSensitivityProperty().bind(scrollSlider.valueProperty());

            Stream.of(translationSlider, scrollSlider)
                    .forEach(slider -> {
                        slider.setShowTickLabels(true);
                        slider.setMinWidth(SLIDER_WIDTH);
                        slider.setMaxWidth(SLIDER_WIDTH);
                    });

            Button resetDef = new Button(RESETDEFAULT_BUTTON);
            resetDef.setFont(fontAwesome);
            resetDef.setOnAction(e -> {
                translationSlider.setValue(MOUSE_DRAG_DEFAULTSENS);
                scrollSlider.setValue(MOUSE_SCROLL_DEFAULTSENNS);
            });
            Tooltip resetDefaultToolTip = new Tooltip(HELPTXT_RESETDEF);
            toolTipList.add(resetDefaultToolTip);
            resetDef.setTooltip(resetDefaultToolTip);

            VBox slidersVBox = new VBox(sensLabel, spaceLabel(), dragSensLabel, translationSlider, spaceLabel(),
                    scrollSensLabel, scrollSlider, resetDef);
            slidersVBox.setAlignment(Pos.CENTER);

            Separator secondSeparator = new Separator(Orientation.HORIZONTAL);
            secondSeparator.setStyle(SEPARATOR_STYLE);

            // c/ Final checkboxes:
            Label drawLabel = new Label("Dessiner :");
            Tooltip drawLabelToolTip = new Tooltip(HELPTXT_OBJECTS_TO_DRAW);
            drawLabel.setTooltip(drawLabelToolTip);
            toolTipList.add(drawLabelToolTip);
            drawLabel.setAlignment(Pos.CENTER);

            GridPane checkBoxesToDraw = new GridPane();
            ObservableList<CheckBox> drawablesList = FXCollections.observableArrayList();
            for (DrawableObjects drawable : DrawableObjects.values()) {
                CheckBox draw = new CheckBox(drawable.getName());
                draw.setSelected(true);
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
            }
            CheckBox index7 = drawablesList.get(7);
            drawablesList.set(7, drawablesList.get(3));
            drawablesList.set(3, index7);

            Iterator<CheckBox> checkBoxIt = drawablesList.iterator();
            for (int i = 0; i < 2; ++i) {
                for (int j = 0; j < 4; ++j) {
                    checkBoxesToDraw.add(checkBoxIt.next(), i, j);
                }
            }

            checkBoxesToDraw.setVgap(PARAMS_GRIDGAP);
            checkBoxesToDraw.setHgap(PARAMS_GRIDGAP);

            // d/ Finalization
            VBox parametersBox = new VBox(layerOneGrid, firstSeparator, slidersVBox, secondSeparator, drawLabel, checkBoxesToDraw);
            parametersBox.setStyle(SETTINGS_BOX_STYLE);
            setVisibleAndManaged(parametersBox, false);

            // NEW VI/ Information on celestial object panel (right border):
            VBox rightBox = new VBox();

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
            primaryStage.setScene(mainScene);
            primaryStage.show();
            manager.canvas().requestFocus();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private InputStream resourceStream(String s) {
        return getClass().getResourceAsStream(s);
    }

    private UnaryOperator<TextFormatter.Change> coordFilter(NumberStringConverter stringConverter, boolean isLon) {
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

    private static Label spaceLabel() {
        return new Label("\n");
    }
}
