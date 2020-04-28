package ch.epfl.rigel.math.sets.abstraction;

import ch.epfl.rigel.math.sets.*;
import ch.epfl.rigel.math.sets.concrete.MathSet;
import ch.epfl.rigel.math.sets.properties.Equation;
import javafx.util.Pair;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public interface AbstractMathSet<T> extends Iterable<T> {

    /**
     * The image of this set by a function
     *
     * @param f   the function to apply
     * @param <U> the codomain type
     * @return the MathSet containing all the lements produced by f when applied on this set
     */
    default <U> AbstractMathSet<U> image(SetFunction<T, U> f) {
        return f.apply(this);
    }

    default boolean contains(T t) {
        return getData().contains(t);
    }

    default boolean containsSet(AbstractMathSet<T> other) {
        return getData().containsAll(other.getData());
    }

    /**
     * @return a useful predicate checking wether an element is in the set
     */
    default Equation<T> predicateContains() {
        return this::contains;
    }

    /**
     * Allows to select elements according to a predicate
     *
     * @param equation the predicate that each element will have to respect
     * @return the set of all elements in this set that complies to t
     */
    default AbstractMathSet<T> suchThat(Predicate<T> equation) {
        return suchThat(Collections.singletonList(equation));
    }

    /**
     * Allows to select elements according to predicates
     *
     * @param t the predicates that each element will have to respect
     * @return the set of all elements in this set that complies to all t
     */

    AbstractMathSet<T> suchThat(Collection<Predicate<T>> t);

    /**
     * @return the data wrapped by the set in its raw form
     */
    Collection<T> getData();

    /**
     * @return an element from the current set
     */
    default Optional<T> getElement() {
        return stream().findFirst();
    }


    default T getElementOrThrow() {
        return stream().findFirst().orElseThrow(
                () -> new NoSuchElementException("Tried to get element from empty set."));
    }


    /**
     * @param equation the property to respect
     * @return an element from the current set respecting equation
     */
    default Optional<T> getElement(Predicate<T> equation) {
        return suchThat(equation).getElement();
    }


    /**
     * Set theoristic intersection
     *
     * @param others the collection of Set to intersect with
     * @return A MathSet containing only those elements that lies in all sets
     */
    default AbstractMathSet<T> intersection(AbstractMathSet<T> others) {
        return intersection(Collections.singleton(others));
    }

    /**
     * Set theoristic intersection
     *
     * @param others the collection of Set to intersect with
     * @return A MathSet containing only those elements that lies in all sets
     */
    default AbstractMathSet<T> intersection(Collection<AbstractMathSet<T>> others) {
        return suchThat(others.stream().map(AbstractMathSet::predicateContains).collect(Collectors.toSet()));
    }

    /**
     * Set theoristic union
     *
     * @param others the Sets to union with
     * @return A MathSet containing all elements that lies in one of the sets
     */
    default AbstractMathSet<T> union(AbstractMathSet<T> others) {
        return union(Collections.singleton(others));
    }

    /**
     * Set theoretical union
     *
     * @param others the Sets to union with
     * @return A MathSet containing all elements that lies in one of the sets
     */
    AbstractMathSet<T> union(Collection<AbstractMathSet<T>> others);

    /**
     * The directsum construct a space containing a copy of all sets
     *
     * @param others the other sets in the directsum
     * @param <U>    the type of the other Sets
     * @return a set containing this set and others as copies inside him
     */
    default <U> AbstractMathSet<OptionalPair<T, U>> directSum(AbstractMathSet<U> others) {
        return directSum(Collections.singleton(others));
    }

    /**
     * The direct sum construct a space containing a copy of all sets
     *
     * @param other the other sets in the directsum
     * @param <U>   the type of the other Sets
     * @return a set containing this set and others as copies inside him
     */
    default <U> AbstractMathSet<OptionalPair<T, U>> directSum(Collection<AbstractMathSet<U>> other) {
        return image(t -> new OptionalPair<T, U>(t, null)).union(other.stream().map(s -> s.image(u -> new OptionalPair<T, U>(null, u)))
                .collect(Collectors.toList()));
    }

    /**
     * Set theoretical union followed by a cartesian product
     *
     * @param others the Collection of Sets to "multiply" with
     * @return A MathSet containing all possible pairs of elements from other and all other MathSets in other
     */
    default <U> AbstractMathSet<Pair<T, U>> product(AbstractMathSet<U> others) {
        return product(Collections.singleton(others));
    }

    /**
     * Set theoristic union followed by a cartesian product
     *
     * @param other the Collection of Sets to "multiply" with
     * @return A MathSet containing all possible pairs of elements from other and all other MathSets in other
     */
    default <U> AbstractMathSet<Pair<T, U>> product(Collection<AbstractMathSet<U>> other) {
        return unionOf(image(t -> unionOf(other).image(u -> new Pair<>(t, u))));
    }

    /**
     * Set theoric substraction
     *
     * @param other the set to substract
     * @return the set containing all elements of this set except those lying in other
     */
    default AbstractMathSet<T> minusSet(AbstractMathSet<T> other) {
        return suchThat(Predicate.not(other::contains));
    }

    /**
     * Set theoric substraction
     *
     * @param other the element to substract
     * @return the set containing all elements of this set except other
     */
    default AbstractMathSet<T> minus(T other) {
        return suchThat(p -> !p.equals(other));
    }

    /**
     * The powerSet is the set of all subsets of a set, it allows to navigate through subsets
     *
     * @return The powerset of the current MathSet
     */
    AbstractMathSet<AbstractMathSet<T>> powerSet();

    /**
     * Powerset computation implementation, adapted from the JASS exercise
     *
     * @param set (Set<T>) set which's powerset will be computed
     * @param <T> type
     * @return (Set < Set < T > >) powerset of input set
     */
    static <T> Set<Set<T>> powerSet(Collection<T> set) {
        if (set.isEmpty())
            return Set.of();

        final T firstElement = set.iterator().next();
        final Set<T> subset = new HashSet<>(set);
        subset.remove(firstElement);
        final Collection<Set<T>> subPowerSet = powerSet(subset);
        Set<Set<T>> powerSet = new HashSet<>();
        for (Set<T> s : subPowerSet) {
            Set<T> s1 = new HashSet<>(s);
            s1.add(firstElement);
            powerSet.add(s);
            powerSet.add(s1);
        }

        return powerSet;
    }


    /**
     * @return the size of the current set
     */
    default int cardinality() {
        return getData().size();
    }

    /**
     * @return wether the set contains at least one element or not
     */
    default boolean isEmpty() {
        return getData().size() == 0;
    }

    /**
     * @param f the function needed to map to a comparable number
     * @return the minimal element according to f
     */
    default T minOf(SetFunction<T, Number> f) {
        return stream().min(Comparator.comparingDouble(t -> f.apply(t).doubleValue())).orElseThrow();
    }

    /**
     * @param f the function needed to map to a comparable number
     * @return the maximal element according to f
     */
    default T maxOf(SetFunction<T, Number> f) {
        return stream().max(Comparator.comparingDouble(t -> f.apply(t).doubleValue())).orElseThrow();
    }

    /**
     * @return allows to traverse the set as a stream
     */
    default Stream<T> stream() {
        return getData().stream();
    }

    /**
     * @return a stream capable of beeing parallelised
     */
    default Stream<T> parallelStream() {
        return getData().parallelStream();
    }

    /**
     * Set theoric union
     *
     * @param sets the set to combine
     * @param <T>  the type of those sets
     * @return A sett containing all elements from each sets
     */
    static <T> AbstractMathSet<T> unionOf(AbstractMathSet<AbstractMathSet<T>> sets) {
        return sets.getElementOrThrow().union(sets.getData());
    }

    /**
     * Set theoric union
     *
     * @param sets the set to combine
     * @param <T>  the type of those sets
     * @return A sett containing all elements from each sets
     */
    static <T> AbstractMathSet<T> unionOf(Collection<AbstractMathSet<T>> sets) {
        return unionOf(new MathSet<>(sets));
    }

    /**
     * @return An iterator on subsets of this set
     */
    default Iterator<AbstractMathSet<T>> setIterator() {
        return powerSet().iterator();
    }

    @Override
    default Iterator<T> iterator() {
        return getData().iterator();
    }

    @Override
    default void forEach(Consumer<? super T> action) {
        stream().forEach(action);
    }

    @Override
    default Spliterator<T> spliterator() {
        return getData().spliterator();
    }

}
