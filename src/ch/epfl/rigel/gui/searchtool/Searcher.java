package ch.epfl.rigel.gui.bonus;

import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.math.graphs.GraphNode;
import ch.epfl.rigel.math.graphs.Path;
import ch.epfl.rigel.math.graphs.Tree;
import ch.epfl.rigel.math.sets.abtract.AbstractMathSet;
import ch.epfl.rigel.math.sets.concrete.IndexedSet;
import ch.epfl.rigel.math.sets.concrete.MathSet;
import javafx.beans.Observable;
import javafx.beans.binding.Binding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.nio.CharBuffer;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ch.epfl.rigel.math.sets.abtract.AbstractMathSet.unionOf;
import static ch.epfl.rigel.math.sets.concrete.MathSet.emptySet;
import static ch.epfl.rigel.math.sets.concrete.MathSet.of;

public final class Searcher<T> extends AutoCompleter<T>{

    private final WeakHashMap<String, T> resultCache;
    private final AbstractMathSet<String> data;
    private final int cacheCapacity;
    private final Predicate<T> filter;
    private final AbstractMathSet<Tree<Character>> unfinishedData;

    protected Searcher(AbstractMathSet<String> names, int cacheCapacity, Predicate<T> p) {
        super(cacheCapacity*2);
        this.data = names;
        this.resultCache = new WeakHashMap<>(cacheCapacity);
        this.cacheCapacity = cacheCapacity;
        this.filter = p;
        this.unfinishedData = IntStream.rangeClosed('a', 'z')
                .mapToObj(s ->
                        new Tree<>(unionOf(data.suchThat(str -> str.charAt(0) == s)
                                .image(Path::fromString)), false))
                .collect(MathSet.toMathSet());
    }

    public Optional<Tree<Character>> search(char s, Tree<Character> unfinished, int n)
    {
        Optional<GraphNode<Character>> potential = unfinished.getNodesAtDepth(n).getElement(p -> p.getValue() == s);
        return potential.isEmpty() ? Optional.empty() : Optional.of(unfinished.subtreeAtPoint(potential.get()));
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

    @Override
    AbstractMathSet<String> process(String s, String t) {

        var res = search(t.charAt(t.length() - 1), unfinishedData.getElement(tr -> tr.getRoot().getValue() == t.charAt(0)).orElseThrow(), t.length());
        return res.isEmpty() ? of(t) : potentialSolutions(res.get(), t);
    }

    @Override
    AbstractMathSet<T> handleReturn(String t) {

        if (unfinishedData.isEmpty())
        {
            var res = data.at(t);
            prepareCache(of(t));
            return of(res).suchThat(filter);
        }
        else
            return potentialSolutions(unfinishedData, t)
                    .image(data::at)
                    .suchThat(filter);
    }


}
