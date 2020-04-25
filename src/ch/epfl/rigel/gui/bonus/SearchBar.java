package ch.epfl.rigel.gui.bonus;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.Moon;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.Planet;
import ch.epfl.rigel.astronomy.Star;
import ch.epfl.rigel.astronomy.Sun;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.graphs.Node;
import ch.epfl.rigel.math.graphs.Tree;
import ch.epfl.rigel.math.sets.abtract.AbstractMathSet;
import ch.epfl.rigel.math.sets.concrete.MathSet;
import ch.epfl.rigel.math.sets.concrete.OrderedTuple;
import ch.epfl.rigel.math.sets.concrete.PartitionSet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A tool to search through celestial objects
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class SearchBar {

    private final ObservedSky sky;

    public SearchBar(final ObservedSky sky) {
        this.sky = sky;
    }

    public Tree<CelestialObject> createSearchTree(
            final char inputChar, final Filters filter, final SearchBy search) {

        Preconditions.checkArgument(search != SearchBy.HIPPARCOS || filter == Filters.STARS);
        final char initialChar = (Character.isLowerCase(inputChar)) ? Character.toUpperCase(inputChar) : inputChar;

        final Function<CelestialObject, String> stringFunc = search.getStringFunction();

        final PartitionSet<CelestialObject> partitionLengths = new PartitionSet<>(
                new MathSet<>(sky.celestialObjMap().keySet().stream()
                        .filter(celestObj -> filter.classList().contains(celestObj.getClass())
                                && stringFunc.apply(celestObj).charAt(0) == initialChar)
                        .collect(Collectors.toSet())),
                (celestObj1, celestObj2) -> stringFunc.apply(celestObj1).length() == stringFunc.apply(celestObj2).length());

        //char does not correspond to any initial name
        if (partitionLengths.cardinality() == 0) {
            return new Tree<>(MathSet.emptySet());
        }

        final OrderedTuple<AbstractMathSet<CelestialObject>> lengthsSet = new OrderedTuple<>(
                partitionLengths.components().stream()
                        .sorted(Comparator.comparingInt(set -> stringFunc.apply(set.getElement()).length()))
                        .collect(Collectors.toCollection(ArrayList::new)));

        final boolean existsCelestObjOfNameChar = (stringFunc.apply(lengthsSet.at(0).getElement()).length() == 1);

        final Node<CelestialObject> root = new Node<>(existsCelestObjOfNameChar ?
                lengthsSet.at(0).getElement() :
                new CelestialObject(String.valueOf(initialChar), EquatorialCoordinates.of(0, 0),
                        0, 0) {
                });

        final Set<Node<CelestialObject>> nodesSet = new HashSet<>();
        nodesSet.add(root);
        createSearchTreeRecur(nodesSet, root, existsCelestObjOfNameChar ? lengthsSet.at(1) : lengthsSet.at(0),
                stringFunc, lengthsSet, 1, root);

        return new Tree<>(new MathSet<>(nodesSet.stream().map(Node::lockNode).collect(Collectors.toSet())));
    }

    private void createSearchTreeRecur(final Set<Node<CelestialObject>> workSet, final Node<CelestialObject> currNode,
            final AbstractMathSet<CelestialObject> nextLengthSet, final Function<CelestialObject, String> stringFunc,
            final OrderedTuple<AbstractMathSet<CelestialObject>> orderedSets, final int currentDepth, final Node<CelestialObject> root) {

        nextLengthSet.stream()
            .forEach(celestObj -> {
                final Node<CelestialObject> nextNode = new Node<>(celestObj,
                        (stringFunc.apply(celestObj).contains(stringFunc.apply(currNode.getValue()).substring(0, currentDepth))) ?
                                currNode : root);
                workSet.add(nextNode);
                if (orderedSets.indexOf(nextLengthSet) < orderedSets.cardinality() - 1) {
                    createSearchTreeRecur(workSet, nextNode, orderedSets.next(nextLengthSet), stringFunc, orderedSets,
                           currentDepth + 1, root);
                }
            });
    }

    public enum Filters {
        SOLAR_SYSTEM(List.of(Moon.class, Sun.class, Planet.class)),
        STARS(List.of(Star.class)),
        ALL(List.of(Moon.class, Sun.class, Planet.class, Star.class));

        private final List<Class<? extends CelestialObject>> classList;

        private Filters(List<Class<? extends CelestialObject>> classList) {
            this.classList = classList;
        }

        public List<Class<? extends CelestialObject>> classList() {
            return classList;
        }
    }

    public enum SearchBy {
        NAME(CelestialObject::name),
        HIPPARCOS(celestObj -> (!(celestObj instanceof Star)) ?
                null : String.valueOf(((Star) celestObj).hipparcosId()));

        private final Function<CelestialObject, String> stringFunction;

        SearchBy(final Function<CelestialObject, String> stringGetter) {
            this.stringFunction = stringGetter;
        }

        private Function<CelestialObject, String> getStringFunction() {
            return stringFunction;
        }
    }
}
