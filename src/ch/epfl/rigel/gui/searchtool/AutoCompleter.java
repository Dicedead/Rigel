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

import static ch.epfl.rigel.math.sets.concrete.MathSet.emptySet;

public abstract class AutoCompleter<T> extends TextField {

    private final ObjectProperty<AbstractMathSet<T>> results;
    private final ContextMenu entriesGUI;
    private final int numberOfEntry;

    public AutoCompleter(final int numberOfEntry) {
        super();
        this.entriesGUI = new ContextMenu();
        this.numberOfEntry= numberOfEntry;
        results = new SimpleObjectProperty<>(emptySet());
        makeLinks();
    }

    public AbstractMathSet<T> getResults() {
        return results.get();
    }

    public ObjectProperty<AbstractMathSet<T>> resultsProperty() {
        return results;
    }

    protected void populate(final AbstractMathSet<String> toPopulate)
    {
        AbstractMathSet<MenuItem> menu = new MathSet<>(entriesGUI.getItems());
        menu = toPopulate.image(e ->
        {
            var res = (MenuItem) (new CustomMenuItem(new Label(e), true));
            res.setGraphic(buildTextFlow(e, getText()));
            return res;
        }).minusSet(menu);

        if (menu.cardinality() - numberOfEntry > 0)
            entriesGUI.getItems().remove(0, entriesGUI.getItems().size() - numberOfEntry);


        entriesGUI.getItems().addAll();
        entriesGUI.getItems().retainAll((new HashSet<>(entriesGUI.getItems())));


    }
    abstract AbstractMathSet<String> process(String s, String t);
    abstract AbstractMathSet<T> handleReturn(String t);

    public void makeLinks()
    {
        textProperty().addListener((observable, oldValue, newValue) -> {

            if (getText() == null ) {
                entriesGUI.hide();
            } else {
                populate(process(newValue, oldValue));

                if (!entriesGUI.isShowing()) { //optional
                    entriesGUI.show(this, Side.BOTTOM, 0, 0); //position of popup
                }}});

        focusedProperty().addListener((observableValue, oldValue, newValue) -> entriesGUI.hide());

        setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER){
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
