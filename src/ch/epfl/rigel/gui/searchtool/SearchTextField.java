package ch.epfl.rigel.gui.searchtool;

import ch.epfl.rigel.math.sets.abstraction.AbstractMathSet;
import ch.epfl.rigel.math.sets.concrete.MathSet;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.HashSet;
import static ch.epfl.rigel.math.sets.implement.MathSet.emptySet;

/**
 * Search tool GUI's implementation and functionalities abstraction
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public abstract class SearchTextField<T> extends TextField {

    private final ObjectProperty<AbstractMathSet<T>> results;
    private final ContextMenu entriesGUI;
    private final int numberOfEntry;

    public SearchTextField(final int numberOfEntry) {
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
        for (String str : toPopulate) {
            CustomMenuItem menuItem = new CustomMenuItem(new Label(str), true);
            menuItem.getContent().setOnMouseClicked(mouse -> clickAction(str));
            entriesGUI.getItems().add(menuItem);
        }

        if (entriesGUI.getItems().size() - numberOfEntry > 0)
            entriesGUI.getItems().remove(0, entriesGUI.getItems().size() - numberOfEntry);

    abstract AbstractMathSet<String> process(String s);

    abstract AbstractMathSet<T> handleReturn(String t);

    abstract void clickAction(String str);

    public void makeLinks() {
        textProperty().addListener((observable, oldValue, newValue) -> {
            String enteredText = getText();
            if (enteredText == null) {
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

    /**
     * Build TextFlow with selected text. Return "case" dependent.
     *
     * @param text - string with text
     * @param filter - string to select in text
     * @return - TextFlow
     */
    public static TextFlow buildTextFlow(String text, String filter) {

        final int filterIndex   = text.toLowerCase().indexOf(filter.toLowerCase());
        final Text textBefore   = new Text(text.substring(0, filterIndex));
        final Text textAfter    = new Text(text.substring(filterIndex + filter.length()));
        final Text textFilter   = new Text(text.substring(filterIndex,  filterIndex + filter.length())); //instead of "filter" to keep all "case sensitive"

        textFilter.setFill(Color.ORANGE);
        textFilter.setFont(Font.font("Helvetica", FontWeight.BOLD, 12));

        return new TextFlow(textBefore, textFilter, textAfter);
    }
}
