package ch.epfl.rigel.gui.bonus;

import ch.epfl.rigel.math.graphs.Tree;
import ch.epfl.rigel.math.sets.abtract.AbstractMathSet;
import ch.epfl.rigel.math.sets.concrete.MathSet;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static ch.epfl.rigel.math.sets.concrete.MathSet.emptySet;

public abstract class AutoCompleter<T> extends TextField {

    private final ObjectProperty<AbstractMathSet<T>> results;
    private final ContextMenu entriesGUI;
    private final int numberOfEntry;

    protected AutoCompleter(int numberOfEntry) {
        this.entriesGUI = new ContextMenu();
        this.numberOfEntry= numberOfEntry;
        results = new SimpleObjectProperty<>(emptySet());
        init();
    }

    public AbstractMathSet<T> getResults() {
        return results.get();
    }

    public ObjectProperty<AbstractMathSet<T>> resultsProperty() {
        return results;
    }

    protected void setResults(AbstractMathSet<T> results) {
        this.results.set(results);
    }

    protected void populate(AbstractMathSet<String> toPopulate)
    {
        entriesGUI.getItems().addAll(toPopulate.image(e -> new CustomMenuItem(new Label(e), true))
                .getData()
                .stream()
                .limit(numberOfEntry)
                .collect(Collectors.toSet()));
    }

    abstract AbstractMathSet<String> process(String s, String t);
    abstract AbstractMathSet<T> handleReturn(String t);

    public void init()
    {
        textProperty().addListener((observable, oldValue, newValue) -> {
            String enteredText = getText();
            if (enteredText == null ) {
                entriesGUI.hide();
            } else {
                populate(process(newValue, oldValue));
                if (!entriesGUI.isShowing()) { //optional
                    entriesGUI.show(this, Side.BOTTOM, 0, 0); //position of popup
                }
            }

        });

        focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            entriesGUI.hide();
        });

        setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER){
                results.setValue(handleReturn(getText()));
            }
        });
    }

}
