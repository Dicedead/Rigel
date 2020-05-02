package ch.epfl.rigel.gui.searchtool;

import ch.epfl.rigel.math.sets.abstraction.AbstractMathSet;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.util.HashSet;

import static ch.epfl.rigel.math.sets.implement.MathSet.emptySet;

public abstract class SearchTextField<T> extends TextField {

    private final ObjectProperty<AbstractMathSet<T>> results;
    private final ContextMenu entriesGUI;
    private final int numberOfEntry;

    public SearchTextField(int numberOfEntry) {
        super();
        this.entriesGUI = new ContextMenu();
        this.numberOfEntry = numberOfEntry;
        results = new SimpleObjectProperty<>(emptySet());
        makeLinks();
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

    protected void populate(final AbstractMathSet<String> toPopulate) {
        entriesGUI.getItems().clear();
        entriesGUI.getItems().addAll(new HashSet<>(toPopulate.image(e -> new CustomMenuItem(new Label(e), true))
                .getData()));
        if (entriesGUI.getItems().size() - numberOfEntry > 0)
            entriesGUI.getItems().remove(0, entriesGUI.getItems().size() - numberOfEntry);
    }

    abstract AbstractMathSet<String> process(String s, String t);

    abstract AbstractMathSet<T> handleReturn(String t);

    public void makeLinks() {
        textProperty().addListener((observable, oldValue, newValue) -> {
            String enteredText = getText();
            if (enteredText == null) {
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
            if (event.getCode() == KeyCode.ENTER) {
                results.setValue(handleReturn(getText()));
            }
        });
    }

}
