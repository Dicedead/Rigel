package ch.epfl.rigel.gui.searchtool;

import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.math.graphs.GraphNode;
import ch.epfl.rigel.math.graphs.Path;
import ch.epfl.rigel.math.graphs.Tree;
import ch.epfl.rigel.math.sets.abstraction.AbstractMathSet;
import ch.epfl.rigel.math.sets.concrete.IndexedSet;
import ch.epfl.rigel.math.sets.concrete.MathSet;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ch.epfl.rigel.math.sets.abstraction.AbstractMathSet.unionOf;
import static ch.epfl.rigel.math.sets.concrete.MathSet.of;

public final class Searcher extends AutoCompleter<CelestialObject> {

    private final WeakHashMap<String, CelestialObject> resultCache;
    private final AbstractMathSet<String> data;
    private final int cacheCapacity;
    private final Predicate<CelestialObject> filter;
    private final IndexedSet<Tree<Character>, Character> unfinishedData;
    private final IndexedSet<CelestialObject, String> starCatalogue;
    protected Searcher(int cacheCapacity, Predicate<CelestialObject> p, StarCatalogue sky) {
        super(cacheCapacity*2);
        this.data = sky.stars().stream().map(CelestialObject::name).collect(MathSet.toMathSet());
        this.resultCache = new WeakHashMap<>(cacheCapacity);
        this.cacheCapacity = cacheCapacity;
        this.filter = p;
        this.unfinishedData = new IndexedSet<>(IntStream.rangeClosed('a', 'z')
                .mapToObj(s ->
                        new Tree<>(unionOf(data.suchThat(str -> str.charAt(0) == s)
                                .image(Path::fromString)), false))
                .collect(Collectors.toMap(t -> t.getRoot().getValue(), Function.identity())));
        this.starCatalogue = new IndexedSet<>(sky.stars().stream().collect(Collectors.toMap(CelestialObject::name, Function.identity())));
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

        labels.forEach(l -> resultCache.put(l , starCatalogue.at(l)));
    }

    protected void flushCache()
    {
        resultCache.clear();
    }

    @Override
    AbstractMathSet<String> process(String s, String t) {

        var res = search(t.charAt(t.length() - 1), unfinishedData.at(t.charAt(0)), t.length());
        return res.isEmpty() ? of(t) : potentialSolutions(res.get(), t);
    }

    @Override
    AbstractMathSet<CelestialObject> handleReturn(String t) {

        if (unfinishedData.isEmpty())
        {
            var res = starCatalogue.at(t);
            prepareCache(of(t));
            return of(res).suchThat(filter);
        }
        else
            return potentialSolutions(unfinishedData.at(t.charAt(0)), t)
                    .image(starCatalogue::at)
                    .suchThat(filter);
    }


}
