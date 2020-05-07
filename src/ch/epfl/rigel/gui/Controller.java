package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.gui.searchtool.Searcher;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.util.converter.NumberStringConverter;

import java.io.IOException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class Controller {

    private final SkyCanvasManager manager;
    private Searcher searcher;
    private  Stage thisStage;
    @FXML
    public TextField lon;
    @FXML
    public TextField lat;
    @FXML
    public DatePicker date;
    @FXML
    public TextField heure;
    @FXML
    public ComboBox<ZoneId> zone;
    @FXML
    public ChoiceBox<NamedTimeAccelerator> acceleration;
    @FXML
    public Button replay;
    @FXML
    public Button goStop;
    //@FXML
    //public TextField searchBar;
    @FXML
    public Canvas canvas;
    @FXML
    public BorderPane split;

    @FXML
    public AnchorPane menuPane;

    private final StarCatalogue catalogue;
    private final DateTimeBean dateTimeBean;
    private final ObserverLocationBean observer;
    private final ViewingParametersBean view;
    private final ObservedSky sky;
    private final TimeAnimator animator;

    public Controller(StarCatalogue catalogue, DateTimeBean dateTimeBean, ObserverLocationBean observer, ViewingParametersBean view, ObservedSky sky, TimeAnimator animator) throws IOException {

        this.catalogue = catalogue;
        this.dateTimeBean = dateTimeBean;
        this.observer = observer;
        this.view = view;
        this.sky = sky;
        this.animator = animator;

        this.manager = new SkyCanvasManager(catalogue, dateTimeBean, observer, view);
        searcher = new Searcher(5, sky, observer, dateTimeBean);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("maingui.fxml"));

        loader.setController(this);
        thisStage = new Stage();
        thisStage.setScene(new Scene(loader.load()));

    }


    @FXML
    public void initialize()
    {
        searcher.setLayoutX(67);
        searcher.setLayoutY(3);
        searcher.setPrefHeight(23);
        searcher.setPrefWidth(93);
        menuPane.getChildren().add(searcher);

        canvas = manager.canvas();
        split.getChildren().add(canvas);

        canvas.widthProperty().bind(split.widthProperty());
        canvas.heightProperty().bind(split.heightProperty());

        canvas.requestFocus();

        NumberStringConverter stringConverter =
                new NumberStringConverter("#0.00");

        UnaryOperator<TextFormatter.Change> lonFilter = (change -> {
            try {
                String newText =
                        change.getControlNewText();
                double newLonDeg =
                        stringConverter.fromString(newText).doubleValue();
                return GeographicCoordinates.isValidLonDeg(newLonDeg)
                        ? change
                        : null;
            } catch (Exception e) {
                return null;
            }
        });

        UnaryOperator<TextFormatter.Change> latFilter = (change -> {
            try {
                String newText =
                        change.getControlNewText();
                double newLonDeg =
                        stringConverter.fromString(newText).doubleValue();
                return GeographicCoordinates.isValidLatDeg(newLonDeg)
                        ? change
                        : null;
            } catch (Exception e) {
                return null;
            }
        });

        TextFormatter<Number> lonTextFormatter =
                new TextFormatter<>(stringConverter, 0, lonFilter);

        TextFormatter<Number> latTextFormatter =
                new TextFormatter<>(stringConverter, 0, latFilter);

        TextFormatter<Number> textFormat =
                new TextFormatter<>(stringConverter, 0);

        TextFormatter<Number> textFormatLat =
                new TextFormatter<>(stringConverter, 0);

        lon.setTextFormatter(lonTextFormatter);
        lat.setTextFormatter(latTextFormatter);

        observer.lonDegProperty().bindBidirectional(lonTextFormatter.valueProperty());
        observer.latDegProperty().bindBidirectional(latTextFormatter.valueProperty());


        lon.setTextFormatter(textFormat);
        lat.setTextFormatter(textFormatLat);

        DateTimeFormatter hmsFormatter =
                DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTimeStringConverter stringConverter2 =
                new LocalTimeStringConverter(hmsFormatter, hmsFormatter);
        TextFormatter<LocalTime> timeFormatter =
                new TextFormatter<>(stringConverter2);

        heure.setTextFormatter(timeFormatter);
        timeFormatter.setValue(dateTimeBean.getTime());
        date.setValue(dateTimeBean.getDate());
        dateTimeBean.timeProperty().bindBidirectional(timeFormatter.valueProperty());
        dateTimeBean.dateProperty().bindBidirectional(date.valueProperty());


        zone.setValue(dateTimeBean.getZone());
        zone.getItems().addAll(ZoneId.getAvailableZoneIds().stream().map(ZoneId::of).collect(Collectors.toSet()));
        zone.valueProperty().bindBidirectional(dateTimeBean.zoneProperty());


        searcher.lastSelectedCenterProperty().addListener(((observable, oldValue, newValue) -> {
                view.setCenter(newValue);
                })
        );


        acceleration.getItems().addAll(
                NamedTimeAccelerator.TIMES_1,
                NamedTimeAccelerator.TIMES_30,
                NamedTimeAccelerator.TIMES_300,
                NamedTimeAccelerator.TIMES_3000);

        acceleration.valueProperty().addListener((observable, oldValue, newValue) -> animator.setAccelerator(newValue.getAccelerator()));
        goStop.setOnAction(ae ->{
            if (!animator.isRunning()) {
                animator.start();
            }
            else {
                animator.stop();
            }
        });

        replay.setOnAction(ae -> animator.handle(0));
        

    }

    public Stage getThisStage() {
        return thisStage;
    }
}

/*
<TextField fx:id="searchBar" layoutX="67.0" layoutY="3.0" prefHeight="23.0" prefWidth="93.0" style="-fx-border-insets: 0, 0 0 0 0; -fx-background-insets: 0, 0 0 0 0;">
               <font>
                  <Font size="12.0" />
               </font>
            </TextField>
 */