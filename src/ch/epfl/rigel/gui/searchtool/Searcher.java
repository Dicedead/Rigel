package ch.epfl.rigel.gui.searchtool;

import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.Star;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.graphs.GraphNode;
import ch.epfl.rigel.math.graphs.Path;
import ch.epfl.rigel.math.graphs.Tree;
import ch.epfl.rigel.math.sets.abstraction.AbstractMathSet;
import ch.epfl.rigel.math.sets.implement.IndexedSet;
import ch.epfl.rigel.math.sets.implement.MathSet;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ch.epfl.rigel.math.sets.abstraction.AbstractMathSet.unionOf;
import static ch.epfl.rigel.math.sets.implement.MathSet.emptySet;

public final class Searcher extends AutoCompleter<CelestialObject> {

    private final WeakHashMap<String, CelestialObject> resultCache;
    private final AbstractMathSet<String> data;
    private final int cacheCapacity;
    private final Predicate<CelestialObject> filter;
    private final IndexedSet<Tree<Character>, Character> treesOfCharacters;
    private final Set<Character> availableChars;
    private final IndexedSet<CelestialObject, String> starToString;

    public Searcher(int cacheCapacity, Predicate<CelestialObject> p, StarCatalogue sky) {
        super(2 * cacheCapacity);

        /*List<Star> stars = List.of(new Star(1, "Sa", EquatorialCoordinates.of(0,0), 5f,5f),
                new Star(1, "Se", EquatorialCoordinates.of(0,0), 5f,5f),
                new Star(1, "Sia", EquatorialCoordinates.of(0,0), 5f,5f),
                new Star(1, "Sip", EquatorialCoordinates.of(0,0), 5f,5f)) ;*/

        this.data = sky.stars().stream().map(CelestialObject::name).collect(MathSet.toMathSet());
        this.resultCache = new WeakHashMap<>(cacheCapacity);
        this.cacheCapacity = cacheCapacity;
        this.filter = p;
        this.availableChars = new HashSet<>();

        Map<Character, Tree<Character>> preDat = new HashMap<>();
        for (char i = 'A'; i <= 'Z'; ++i) {
            final char finalI = i;
            final AbstractMathSet<String> res = data.imageIf(str -> str.indexOf(finalI) == findFirstalpha(str),
                    string -> string.substring(findFirstalpha(string)));
            if (!res.isEmpty()) {
                availableChars.add(i);
                final GraphNode<Character> root = new GraphNode<>(i);
                preDat.put(i, new Tree<>(unionOf(res.image(string -> Path.fromStringWithRoot(string, root,1))), root, false));
            }
        }

        this.treesOfCharacters = new IndexedSet<>(preDat);
        this.starToString = new IndexedSet<CelestialObject, String>(sky.stars().stream()
                .collect(Collectors.toMap(CelestialObject::name, Function.identity(), (v1, v2) -> v1)));
    }

    private static int findFirstalpha(String str) {
        for (int i = 0; i < str.length(); ++i) {
            if (Character.isAlphabetic(str.charAt(i)))
                return i;
        }
        return -1;
    }

    public Optional<Tree<Character>> search(char s, Tree<Character> unfinished, int depth) {
        //TODO need to REMOVE from a Tree being worked on the nodes that are of no interest with depth
        if (depth > unfinished.getMaxDepth()) return Optional.empty();
        AbstractMathSet<GraphNode<Character>> potential = unfinished.getNodesAtDepth(Math.max(depth, 0))
                .suchThat(node -> node.getValue() == ((depth == 0) ? Character.toUpperCase(s) : s));
        return potential.isEmpty() ? Optional.empty() : Optional.of(unfinished.subtreeAtPoint(potential.minOf(GraphNode::getDepth)));
    }

    public AbstractMathSet<String> potentialSolutions(Tree<Character> data) {

        return data.getLeaves()
                .image(n -> n.hierarchy().reverse()
                        .image(l -> l.getValue().toString())
                        .stream()
                        .collect(Collectors.joining()))
                .union(new MathSet<>(resultCache.keySet()));
    }

    protected void prepareCache(AbstractMathSet<String> labels) {
        if (resultCache.size() == cacheCapacity)
            flushCache();

        labels.forEach(l -> resultCache.put(l, starToString.at(l)));
    }

    protected void flushCache() {
        resultCache.clear();
    }

    @Override
    AbstractMathSet<String> process(String s, String t) {

        int firstAlpha;
        if (!s.equals("") && availableChars.contains(s.charAt(firstAlpha = findFirstalpha(s)))) {
            Optional<Tree<Character>> res = search(s.charAt(s.length() - 1),
                    treesOfCharacters.at(s.charAt(firstAlpha)), s.length() - 1);
            return res.isEmpty() ? MathSet.of(t) : potentialSolutions(res.get());
        }
        return emptySet();
    }

    @Override
    AbstractMathSet<CelestialObject> handleReturn(String str) {

        if (treesOfCharacters.isEmpty()) {
            prepareCache(MathSet.of(str));
            return MathSet.of(starToString.at(str)).suchThat(filter);
        } else {
            return potentialSolutions(treesOfCharacters.at(str.charAt(findFirstalpha(str))))
                    .image(starToString::at)
                    .suchThat(filter);
        }
    }
}
