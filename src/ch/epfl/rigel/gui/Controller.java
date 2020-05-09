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
import javafx.scene.layout.BorderPane;
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
    private TextField lon, lat, heure;

    @FXML
    private DatePicker date;

    @FXML
    private ComboBox<ZoneId> zone;

    @FXML
    private ComboBox<NamedTimeAccelerator> acceleration;
    //we thought a combobox looked slightly better than a ChoiceBox here

    @FXML
    private Button replay, goStop;

    @FXML
    private Canvas canvas;

    @FXML
    private HBox searchSection;

    @FXML
    private Pane canvasPane;

    @FXML
    private Text objectUnderMouseTxt, fovText, mousePositionText;

    private static final String PLAY_BUTTON_TEXT = "\uf04b";
    private static final String PAUSE_BUTTON_TEXT = "\uf04c";

    private final SkyCanvasManager manager;
    private final Searcher searcher;
    private final FXMLLoader loader;
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

        loader = new FXMLLoader(getClass().getResource("maingui.fxml"));

        this.canvas = manager.canvas();

        loader.setController(this);
    }


    @FXML
    public void initialize() {

        searchSection.getChildren().add(searcher);
        canvasPane.getChildren().add(canvas);
        canvas.widthProperty().bind(canvasPane.widthProperty());
        canvas.heightProperty().bind(canvasPane.heightProperty());

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
        timeFormatter.valueProperty().bindBidirectional(dateTimeBean.timeProperty());
        date.valueProperty().bindBidirectional(dateTimeBean.dateProperty());

        zone.setValue(dateTimeBean.getZone());
        zone.getItems().addAll(ZoneId.getAvailableZoneIds()
                .stream()
                .sorted()
                .map(ZoneId::of)
                .collect(Collectors.toList()));
        zone.valueProperty().bindBidirectional(dateTimeBean.zoneProperty());

        acceleration.setItems(FXCollections.observableArrayList(NamedTimeAccelerator.values()));
        acceleration.valueProperty().addListener((p, o, n) -> animator.setAccelerator(n.getAccelerator()));
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

        replay.setOnAction(click -> dateTimeBean.setZonedDateTime(ZonedDateTime.now()));

        heure.disableProperty().bind(animator.runningProperty());
        date.disableProperty().bind(animator.runningProperty());
        zone.disableProperty().bind(animator.runningProperty());
        acceleration.disableProperty().bind(animator.runningProperty());
        replay.disableProperty().bind(animator.runningProperty());

        replay.setFont(specialFont);
        goStop.setFont(specialFont);

        manager.objectUnderMouseProperty().addListener((p, o, n) ->
                n.ifPresentOrElse(celestialObject -> objectUnderMouseTxt.setText(celestialObject.info()),
                        () -> objectUnderMouseTxt.setText("")));

        fovText.textProperty().bind(Bindings.format(Locale.ROOT,"Champ de vue : %.1f°",view.fieldOfViewDegProperty()));

        mousePositionText.textProperty().bind(Bindings.format(Locale.ROOT,"Azimut : %.2f°, hauteur : %.2f°",
                manager.mouseAzDegProperty(), manager.mouseAltDegProperty()));
    }

    public FXMLLoader getLoader() { return loader; }

    public void canvasRequestFocus() { canvas.requestFocus(); }

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