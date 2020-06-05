package ch.epfl.rigel.gui.searchtool;

import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.math.graphs.GraphNode;
import ch.epfl.rigel.math.graphs.Path;
import ch.epfl.rigel.math.graphs.Tree;
import ch.epfl.rigel.math.sets.abstraction.AbstractMathSet;
import ch.epfl.rigel.math.sets.implement.IndexedSet;
import ch.epfl.rigel.math.sets.implement.MathSet;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ch.epfl.rigel.math.sets.abstraction.AbstractMathSet.unionOf;
import static ch.epfl.rigel.math.sets.implement.MathSet.emptySet;
import static java.lang.String.valueOf;

/**
 * Search tool functionalities' implementation
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Searcher extends SearchTextField<CelestialObject> {

    private final WeakHashMap<String, CelestialObject>      resultCache;
    private final int                                       cacheCapacity;
    private final IndexedSet<Tree<Character>, Character>    treesOfCharacters;
    private final Set<Character>                            availableChars;
    private final IndexedSet<CelestialObject, String>       stringToCelest;

    private final ObjectProperty<String> lastSelectedName;

    /**
     * Main Searcher constructor
     *
     * @param cacheCapacity (int) number of suggestions to display
     * @param sky           (ObservedSky) initial observed sky
     */
    public Searcher(int cacheCapacity, ObservedSky sky) {
        super(cacheCapacity);

        this.lastSelectedName   = new SimpleObjectProperty<>();
        this.resultCache        = new WeakHashMap<>(cacheCapacity);
        this.cacheCapacity      = cacheCapacity;
        this.availableChars     = new HashSet<>();

        AbstractMathSet<String> data = sky.celestialObjMap().keySet().stream().map(CelestialObject::name).collect(MathSet.toMathSet());


        Map<Character, Tree<Character>> preDat = new HashMap<>();

        for (char i = 'A'; i <= 'Z'; ++i) {
            final char finalI = i;
            final AbstractMathSet<String> res = data.imageIf(str -> str.indexOf(finalI) == findFirstalpha(str),
                    string -> string.substring(findFirstalpha(string)));
            if (!res.isEmpty()) {
                availableChars.add(i);
                final GraphNode<Character> root = new GraphNode<>(i);
                preDat.put(i, new Tree<>(unionOf(res.image(string -> Path.fromStringWithRoot(string, root, 1))),
                        root, false));
            }
        }

        this.treesOfCharacters  = new IndexedSet<>(preDat);
        this.stringToCelest     = new IndexedSet<>(sky.celestialObjMap().keySet().stream()
                .collect(Collectors.toMap(CelestialObject::name, Function.identity(), (v1, v2) -> v1)));
    }

    /**
     * Searching method
     *
     * @param inputText   (String)
     * @param s           (char) new character in input text
     * @param initialTree (Tree<Character>) Tree to search
     * @param depth       (int) depth to search in
     * @return (Optional<Tree<Character>>) new tree to search in
     */
    public Optional<Tree<Character>> search(String inputText, char s, Tree<Character> initialTree, int depth) {

        if (depth > initialTree.getMaxDepth()) return Optional.empty();

        AbstractMathSet<GraphNode<Character>> potential = initialTree.getNodesAtDepth(Math.max(depth, 0))
                .suchThat(node -> node.getValue() == ((depth == 0) ? Character.toUpperCase(s) : s)
                        && node.hierarchy().reverse().image(g -> valueOf(g.getValue())).stream()
                        .collect(Collectors.joining()).equals(inputText));

        AbstractMathSet<GraphNode<Character>>
                unionHierarchy = unionOf(potential.image(node -> node.hierarchy().union(initialTree.subtreeAtPoint(node))));

        return potential.isEmpty() ?
                Optional.empty() : Optional.of(
                new Tree<>(unionHierarchy, false));
    }

    /**
     * Getter as a set of strings of the potentially sought after celestial objects
     *
     * @param data (Tree<Character>) current search tree
     * @return (AbstractMathSet<String>) names of the possible celestial objects
     */
    public AbstractMathSet<String> potentialSolutions(Tree<Character> data) {
        return data.getLeaves()
                .image(n -> n.hierarchy().reverse()
                        .image(GraphNode::getValue)
                        .image(String::valueOf)
                        .stream()
                        .collect(Collectors.joining()))
                .union(new MathSet<>(resultCache.keySet()));
    }

    /**
     * @return (ObjectProperty<String>) Observable: last selected search result
     */
    public ObjectProperty<String> lastSelectedNameProperty() {
        return lastSelectedName;
    }

    /**
     * Setter for observable: last selected search result
     *
     * @param value (String) new value
     */
    public void setLastSelectedName(String value) {
        lastSelectedName.set(value);
    }

    protected void prepareCache(AbstractMathSet<String> labels) {
        if (resultCache.size() == cacheCapacity) flushCache();

        labels.forEach(l -> resultCache.put(l, stringToCelest.at(l)));
    }

    protected void flushCache() {
        resultCache.clear();
    }

    @Override
    AbstractMathSet<String> process(String s) {
        int firstAlpha;
        if (!s.equals("") && (firstAlpha = findFirstalpha(s)) != -1 && availableChars.contains(s.charAt(firstAlpha))) {
            Optional<Tree<Character>> res = search(s, s.charAt(s.length() - 1),
                    treesOfCharacters.at(s.charAt(firstAlpha)), s.length() - 1);

            return res.isEmpty() ? emptySet() : potentialSolutions(res.get());
        }
        return emptySet();
    }

    @Override
    AbstractMathSet<CelestialObject> handleReturn(String str) {
        if (treesOfCharacters.isEmpty()) {
            prepareCache(MathSet.of(str));
            return MathSet.of(stringToCelest.at(str));
        } else {
            return potentialSolutions(treesOfCharacters.at(str.charAt(findFirstalpha(str))))
                    .image(stringToCelest::at);
        }
    }

    @Override
    void clickAction(String str) {
        CelestialObject potentialCelestObj = stringToCelest.at(str);
        lastSelectedName.set((potentialCelestObj == null ? stringToCelest.at("? " + str) : potentialCelestObj).name());
        clear();
    }

    private static int findFirstalpha(String str) {
        for (int i = 0; i < str.length(); ++i) {
            if (Character.isAlphabetic(str.charAt(i)))
                return i;
        }
        return -1;
    }
}
