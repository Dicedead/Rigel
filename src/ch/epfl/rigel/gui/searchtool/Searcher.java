package ch.epfl.rigel.gui.searchtool;

import ch.epfl.rigel.Preconditions;
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

import static ch.epfl.rigel.math.sets.abstraction.AbstractMathSet.unionOf;
import static ch.epfl.rigel.math.sets.concrete.MathSet.emptySet;
import static ch.epfl.rigel.math.sets.concrete.MathSet.of;

public final class Searcher extends AutoCompleter<CelestialObject> {

    private final WeakHashMap<String, CelestialObject> resultCache;
    private final AbstractMathSet<String> data;
    private final int cacheCapacity;
    private final Predicate<CelestialObject> filter;
    private final IndexedSet<Tree<Character>, Character> unfinishedData;
    private final IndexedSet<CelestialObject, String> starCatalogue;

    public Searcher(int cacheCapacity, Predicate<CelestialObject> p, StarCatalogue sky) {
        super(2 * cacheCapacity);
        this.data = sky.stars().stream().map(CelestialObject::name).collect(MathSet.toMathSet());
        this.resultCache = new WeakHashMap<>(cacheCapacity);
        this.cacheCapacity = cacheCapacity;
        this.filter = p;

        Map<Character, Tree<Character>> preDat = new HashMap<>();
        for (char i = 'a'; i <= 'z'; ++i)
        {
            final char finalI = i;
            final var res = data.suchThat(str -> str.toLowerCase().indexOf(finalI) == findFirstalpha(str));
            if (!res.isEmpty())
                preDat.put(i, new Tree<>(unionOf(res.image(s -> Path.fromString(s.toLowerCase()))), false));
        }

        this.unfinishedData = new IndexedSet<>(preDat);
        this.starCatalogue = new IndexedSet<CelestialObject, String>(sky.stars().stream().collect(Collectors.toMap(CelestialObject::name,
                Function.identity(), (v1, v2) ->  v1)));
    }

    private static int findFirstalpha(String str)
    {
        for (int i = 0; i < str.length(); i++) {
            if(Character.isAlphabetic(str.charAt(i)))
                return i;
        }
        return -1;
    }
    public Optional<Tree<Character>> search(char inputChar, Tree<Character> unfinished, int n)
    {
        Preconditions.checkArgument(Character.isAlphabetic(inputChar));
        final char s = Character.toLowerCase(inputChar);
        Optional<GraphNode<Character>> potential = unfinished.getNodesAtDepth(Math.max(n, 0)).getElement(p -> p.getValue() == s);
        return potential.isEmpty() ? Optional.empty() : Optional.of(unfinished.subtreeAtPoint(potential.get()));
    }

    public AbstractMathSet<String> potentialSolutions(final Tree<Character> data, final String builder)
    {
        return data.getLeaves()
                .image(n -> builder + n.hierarchy().reverse()
                        .image(l -> l.getValue().toString()).stream()
                        .collect(Collectors.joining()))
                .union(new MathSet<>(resultCache.keySet()));
    }

    protected void prepareCache(final AbstractMathSet<String> labels)
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
    AbstractMathSet<String> process(final String s, final String t) {

        if (!s.equals("")) {
            final var res = search(getText().charAt(getText().length() - 1), unfinishedData.at(getText().charAt(0)), getText().length());
            return res.isEmpty() ? of(t) : potentialSolutions(res.get(), t);
        }
        return emptySet();
    }

    @Override
    AbstractMathSet<CelestialObject> handleReturn(final String t) {

        if (unfinishedData.isEmpty())
        {
            prepareCache(of(t));
            return of(starCatalogue.at(t)).suchThat(filter);
        }
        else
            return potentialSolutions(unfinishedData.at(t.charAt(0)), t)
                    .image(starCatalogue::at)
                    .suchThat(filter);
    }
}
