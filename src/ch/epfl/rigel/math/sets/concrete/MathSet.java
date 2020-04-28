package ch.epfl.rigel.math.sets.concrete;

import ch.epfl.rigel.math.sets.abstraction.AbstractMathSet;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public class MathSet<T> implements AbstractMathSet<T> {

    private final Set<T> data;

    /**
     * Constructor from collection
     * @param t the data to copy
     */
    public MathSet(Collection<T> t) {
        data = Set.copyOf(t);
    }

    /**
     * Copy constructor
     * @param t the MathSet to copy
     */
    public MathSet(AbstractMathSet<T> t) {
        data = t.getData();
    }

    /**
     * Construct a MathSet from an array of elements
     * @param t the elements to store
     * @param <T> the type of those eleents
     * @return A MathSet containing those elements
     */
    @SafeVarargs
    public static <T> MathSet<T> of(T... t) {
        return new MathSet<>(Set.of(t));
    }

    /**
     * The powerSet is the set of all subsets of a set, it allows to navigate through subsets
     * @return The powerset of the current MathSet
     */
    @Override
    public final AbstractMathSet<AbstractMathSet<T>> powerSet()
    {
        return AbstractMathSet.powerSet(this.getData()).stream().map(MathSet::new).collect(toMathSet());
    }

    /**
     * Set theoretical union
     * @param others the Sets to union with
     * @return A MathSet containing all elements that lies in one of the sets
     */
    @Override
    public final AbstractMathSet<T> union(final Collection<AbstractMathSet<T>> others) {
        return Stream.concat(this.stream(), others.stream().flatMap(s -> s.getData().stream())).collect(toMathSet());
    }

    /**
     * Allows to select elements according to predicates
     * @param t the predicates that each element will have to respect
     * @return the set of all elements in this set that complies to all t
     */
    @Override
    public final AbstractMathSet<T> suchThat(final Collection<Predicate<T>> t) {
        return stream().filter(l -> t.stream().allMatch(r -> r.test(l))).collect(toMathSet());
    }

    /**
     * @return the data wrapped by the set in its raw form
     */
    @Override
    public final Set<T> getData() {
        return data;
    }

    /**
     * A collector allowing to collect elements into a set
     * @param <T> the type of the future MathSet
     * @return The collector allowing to gather values as a set
     */
    static public <T> Collector<T, ?, MathSet<T>> toMathSet() {
        return Collectors.collectingAndThen(Collectors.toSet(), MathSet::new);
    }

    /**
     *
     * @param <T> Any type that is needed
     * @return a reference to the set with 0 elements
     */
    public static <T> AbstractMathSet<T> emptySet() {
        return of();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MathSet)) return false;
        MathSet<?> mathSet = (MathSet<?>) o;
        return Objects.equals(data, mathSet.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
