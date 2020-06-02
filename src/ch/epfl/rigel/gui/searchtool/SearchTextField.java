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
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import static ch.epfl.rigel.math.sets.implement.MathSet.emptySet;

/**
 * Search tool GUI's implementation and functionalities abstraction
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public abstract class SearchTextField<T> extends TextField {

    private static final int INITIAL_CACHE_SIZE = 15;
    private static final int TEXT_HEIGHT = 5;
    private static final Color FILTER_TEXT_COLOR = Color.ORANGE;

    private final ObjectProperty<AbstractMathSet<T>> results;
    private final ContextMenu entriesGUI;
    private final int numberOfEntry;

    /**
     * Default SearchTextField constructor with default cache size
     */
    public SearchTextField()
    {
        this(INITIAL_CACHE_SIZE);
    }

    /**
     * Main SearchTextField constructor with input number of possibilities
     *
     * @param numberOfEntry (int) number of entries
     */
    public SearchTextField(int numberOfEntry) {
        super();
        this.entriesGUI = new ContextMenu();
        this.numberOfEntry = numberOfEntry;
        results = new SimpleObjectProperty<>(emptySet());
        makeLinks();
    }

    /**
     * Build TextFlow with selected text. Return "case" dependent.
     * Concretely, this method 'colors' the beginning of the possibilities and styles them
     *
     * @param text (String) input string
     * @param filter (String) string to select in text
     * @return (TextFlow)
     */
    public static TextFlow buildTextFlow(String text, String filter) {

        int filterIndex   = text.toLowerCase().indexOf(filter.toLowerCase());
        Text textFilter   = new Text(text.substring(filterIndex,  filterIndex + filter.length()));
        //instead of "filter" to keep all "case sensitive"

        textFilter.setFill(FILTER_TEXT_COLOR);
        TextFlow res = new TextFlow(
                textFilter, new Text(text.substring(filterIndex + filter.length())));
        res.setPrefHeight(TEXT_HEIGHT);
        return res;
    }

    protected void populate(final AbstractMathSet<String> toPopulate) {
        entriesGUI.getItems().clear();
        for (String str : toPopulate) {
            Label entry = new Label();
            entry.setGraphic(buildTextFlow(str, getText()));
            entry.setOnMouseClicked(mouse -> clickAction(str));

            entriesGUI.getItems().add(new CustomMenuItem(entry, true));
        }

        if (entriesGUI.getItems().size() - numberOfEntry > 0)
            entriesGUI.getItems().remove(0, entriesGUI.getItems().size() - numberOfEntry);
    }

    abstract AbstractMathSet<String> process(String s);

    abstract AbstractMathSet<T> handleReturn(String t);

    abstract void clickAction(String str);

    private void makeLinks() {
        textProperty().addListener((observable, oldValue, newValue) -> {
            if (getText().equals("")) {
                entriesGUI.hide();
            } else {
                populate(process(newValue));
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
