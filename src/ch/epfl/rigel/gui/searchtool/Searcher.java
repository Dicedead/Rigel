package ch.epfl.rigel.gui.searchtool;

import ch.epfl.rigel.math.graphs.GraphNode;
import ch.epfl.rigel.math.graphs.Tree;
import ch.epfl.rigel.math.sets.abstraction.AbstractMathSet;
import ch.epfl.rigel.math.sets.concrete.IndexedSet;
import ch.epfl.rigel.math.sets.concrete.MathSet;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ch.epfl.rigel.math.sets.concrete.MathSet.emptySet;
import static ch.epfl.rigel.math.sets.concrete.MathSet.of;

public abstract class Searcher<T> extends TextField {

    private final WeakHashMap<String, T> resultCache;
    private final IndexedSet<T, String> data;
    private final int cacheCapacity;
    private final ContextMenu entriesGUI;
    private Tree<Character> searchTree;
    private final Predicate<T> filter;
    private final ObjectProperty<AbstractMathSet<T>> results = new SimpleObjectProperty<>(emptySet());

    protected Searcher(IndexedSet<T, String> data, int cacheCapacity, Predicate<T> p) {
        super();
        this.data = data;
        this.resultCache = new WeakHashMap<>(cacheCapacity);
        this.cacheCapacity = cacheCapacity;
        this.entriesGUI = new ContextMenu();
        this.filter = p;
        init();
    }

    public ObjectProperty<AbstractMathSet<T>> getResults()
    {
        return results;
    }

    private void init()
    {
        textProperty().addListener((observable, oldValue, newValue) -> {
            String enteredText = getText();
            if (enteredText == null ) {
                entriesGUI.hide();
            } else {
                //some suggestions are found

                    //build popup - list of "CustomMenuItem"
                    searchTree = search(newValue.charAt(newValue.length() - 1), searchTree).orElse(searchTree);
                    populateWith(potentialSolutions(searchTree,newValue));

                    if (!entriesGUI.isShowing()) { //optional
                        entriesGUI.show(this, Side.BOTTOM, 0, 0); //position of popup
                    }
            }

        });

        //Hide always by focus-in (optional) and out
        focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            entriesGUI.hide();
        });

        setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER){
                results.setValue(handleEnd(searchTree, getText(), filter));
            }
        });
    }


    public Optional<Tree<Character>> search(char s, Tree<Character> data)
    {
        Optional<GraphNode<Character>> potential = data.getNodesAtDepth(1).getElement(p -> p.getValue() == s);
        return potential.isEmpty() ? Optional.empty() : Optional.of(data.subtreeAtPoint(potential.get()));
    }

    public AbstractMathSet<String> potentialSolutions(Tree<Character> data, String builder)
    {

        return data.getLeaves()
                .image(n -> n.hierarchy().reverse())
                .image(p -> p.image(GraphNode::getValue))
                .image(l -> builder + l.image(Object::toString).stream().collect(Collectors.joining()))
                .union(new MathSet<>(resultCache.keySet()));
    }

    protected void prepareCache(AbstractMathSet<String> labels)
    {
        if(resultCache.size() == cacheCapacity)
            flushCache();
        labels.forEach(l -> resultCache.put(l , data.at(l)));
    }

    protected void flushCache()
    {
        resultCache.clear();
    }

    public AbstractMathSet<T> handleEnd(Tree<Character> unfinished, String c, Predicate<T> filter)
    {
        if (unfinished.isEmpty())
        {
            var res = data.at(c);
            prepareCache(of(c));
            return of(res).suchThat(filter);
        }
        else
            return potentialSolutions(unfinished, c)
                    .image(data::at)
                    .suchThat(filter);
    }
    
    private void populateWith(AbstractMathSet<String> strings)
    {
        entriesGUI.getItems()
                .addAll(strings.image(e -> new CustomMenuItem(new Label(e), true))
                                .getData()
                                .stream()
                                .limit(10)
                                .collect(Collectors.toSet()));
    }
    private void populateWithCache()
    {
        entriesGUI.getItems()
                .addAll(resultCache.keySet().stream()
                        .map(e -> new CustomMenuItem(new Label(e), true))
                        .collect(Collectors.toSet()));
    }

}
