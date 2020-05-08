package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.gui.searchtool.Searcher;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.util.converter.NumberStringConverter;

import java.io.IOException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public final class Controller {

    @FXML
    public TextField lon, lat, heure;

    @FXML
    public DatePicker date;

    @FXML
    public ComboBox<ZoneId> zone;

    @FXML
    public ComboBox<NamedTimeAccelerator> acceleration;
    //we thought a combobox looked slightly better than a ChoiceBox here

    @FXML
    public Button replay, goStop;

    @FXML
    public Canvas canvas;

    @FXML
    public HBox searchSection;

    @FXML
    public Text objectUnderMouseTxt, fovText, mousePositionText;

    @FXML
    public Pane canvasBorder;

    private static final int MIN_WIDTH = 800;
    private static final int MIN_HEIGHT = 600;
    private static final String PLAY_BUTTON_TEXT = "\uf04b";
    private static final String PAUSE_BUTTON_TEXT = "\uf04c";

    private final SkyCanvasManager manager;
    private final Searcher searcher;
    private final Stage thisStage;
    private final DateTimeBean dateTimeBean;
    private final ObserverLocationBean observerLoc;
    private final ViewingParametersBean view;
    private final TimeAnimator animator;
    private final Font specialFont;

    public Controller(StarCatalogue catalogue, DateTimeBean dateTimeBean, ObserverLocationBean observer,
                      ViewingParametersBean view, Font specialFont) throws IOException {

        this.dateTimeBean = dateTimeBean;
        this.observerLoc = observer;
        this.view = view;
        this.animator = new TimeAnimator(dateTimeBean);
        this.specialFont = specialFont;

        this.manager = new SkyCanvasManager(catalogue, dateTimeBean, observer, view);
        this.searcher = manager.searcher();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("maingui.fxml"));

        this.canvas = manager.canvas();

        loader.setController(this);
        thisStage = new Stage();

        thisStage.setMinWidth(MIN_WIDTH);
        thisStage.setMinHeight(MIN_HEIGHT);
        thisStage.setTitle("Rigel");
        thisStage.setScene(new Scene(loader.load()));
    }


    @FXML
    public void initialize() {

        searchSection.getChildren().add(searcher);
        canvasBorder.getChildren().add(canvas);
        canvas.widthProperty().bind(canvasBorder.widthProperty());
        canvas.heightProperty().bind(canvasBorder.heightProperty());

        NumberStringConverter stringConverter = new NumberStringConverter("#0.00");

        UnaryOperator<TextFormatter.Change> lonFilter = coordFilter(stringConverter, true);
        UnaryOperator<TextFormatter.Change> latFilter = coordFilter(stringConverter, false);

        TextFormatter<Number> lonTextFormatter = new TextFormatter<>(stringConverter, 0, lonFilter);
        TextFormatter<Number> latTextFormatter = new TextFormatter<>(stringConverter, 0, latFilter);

        lon.setTextFormatter(lonTextFormatter);
        lat.setTextFormatter(latTextFormatter);

        lonTextFormatter.valueProperty().bindBidirectional(observerLoc.lonDegProperty());
        latTextFormatter.valueProperty().bindBidirectional(observerLoc.latDegProperty());

        DateTimeFormatter hmsFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTimeStringConverter stringConverter2 = new LocalTimeStringConverter(hmsFormatter, hmsFormatter);
        TextFormatter<LocalTime> timeFormatter = new TextFormatter<>(stringConverter2);

        heure.setTextFormatter(timeFormatter);
        timeFormatter.setValue(dateTimeBean.getTime());
        date.setValue(dateTimeBean.getDate());
        dateTimeBean.timeProperty().bindBidirectional(timeFormatter.valueProperty());
        dateTimeBean.dateProperty().bindBidirectional(date.valueProperty());

        zone.setValue(dateTimeBean.getZone());
        zone.getItems().addAll(ZoneId.getAvailableZoneIds()
                .stream()
                .map(ZoneId::of)
                .collect(Collectors.toSet()));

        zone.valueProperty().bindBidirectional(dateTimeBean.zoneProperty());

        acceleration.setItems(FXCollections.observableArrayList(NamedTimeAccelerator.values()));

        acceleration.valueProperty().addListener((p, o, newValue) -> {
            if (animator.isRunning()) {
                animator.stop();
                animator.setAccelerator(newValue.getAccelerator());
                animator.start();
            } else {
                animator.setAccelerator(newValue.getAccelerator());
            }
        });

        acceleration.valueProperty().set(NamedTimeAccelerator.TIMES_300);

        goStop.setOnAction(ae -> {
            if (!animator.isRunning()) {
                animator.start();
                goStop.setText(PAUSE_BUTTON_TEXT);
            } else {
                animator.stop();
                goStop.setText(PLAY_BUTTON_TEXT);
            }
        });

        replay.setOnAction(click -> {
            boolean rerun = animator.isRunning();
            animator.stop();
            dateTimeBean.setZonedDateTime(ZonedDateTime.now());
            if (rerun) animator.start();
        });

        lon.disableProperty().bind(animator.runningProperty());
        lat.disableProperty().bind(animator.runningProperty());
        heure.disableProperty().bind(animator.runningProperty());
        date.disableProperty().bind(animator.runningProperty());

        replay.setFont(specialFont);
        goStop.setFont(specialFont);

        manager.objectUnderMouseProperty().addListener((p, o, n) ->
                n.ifPresentOrElse(celestialObject -> objectUnderMouseTxt.setText(celestialObject.info()),
                        () -> objectUnderMouseTxt.setText("")));

        fovText.textProperty().bind(Bindings.format(Locale.ROOT,"Champ de vue : %.1f°",view.fieldOfViewDegProperty()));
        mousePositionText.textProperty().bind(
                Bindings.format(Locale.ROOT,"Azimut : %.2f°, hauteur : %.2f°", manager.mouseAzDegProperty(),
                        manager.mouseAltDegProperty()));
    }

    public Stage getThisStage() {
        return thisStage;
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
}