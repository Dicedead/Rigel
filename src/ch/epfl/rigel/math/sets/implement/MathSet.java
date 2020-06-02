package ch.epfl.rigel.math.sets.implement;

import ch.epfl.rigel.math.sets.abstraction.AbstractMathSet;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Main custom Set class
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public class MathSet<T> implements AbstractMathSet<T> {

    private final Collection<T> data;

    /**
     * Constructor from collection
     * @param t the data to copy
     */
    public MathSet(Collection<T> t) {
        data = t;
    }

    /**
     * Copy constructor
     * @param t the MathSet to copy
     */
    public MathSet(AbstractMathSet<T> t) {
        this(t.getRawData());
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
        return AbstractMathSet.powerSet(this.getRawData()).stream().map(MathSet::new).collect(toMathSet());
    }

    /**
     * Set theoretical union
     *
     * @param others the Sets to union with
     * @return A MathSet containing all elements that lies in one of the sets
     */
    @Override
    public final AbstractMathSet<T> union(Collection<AbstractMathSet<T>> others) {
        return Stream.concat(this.stream(), others.stream().flatMap(s -> s.getRawData().stream())).collect(toMathSet());
    }

    /**
     * Makes a new set, adding given element
     *
     * @param elem (T) element to add
     * @return (AbstractMathSet<T>)
     */
    public final AbstractMathSet<T> plus(T elem) {
        Collection<T> newData = getRawData();
        newData.add(elem);
        return new MathSet<>(newData);
    }

    /**
     * Allows to select elements according to predicates
     *
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
    public final Collection<T> getRawData() {
        return data;
    }

    /**
     * This Set view is less useful than one would think, hence the O(n) call rather than an immediate O(n) conversion
     * in constructor. This getter is O(1) if the data is indeed a Set though.
     *
     * @return the data wrapped by the set in its set form
     */
    @Override
    public Set<T> getSetData() {
        return (data instanceof Set) ? (Set<T>) data : Set.copyOf(data);
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

    /**
     * @param o (Object) other object
     * @return (boolean) is true iff o is a mathset and its data equals this set's data
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MathSet)) return false;
        return Objects.equals(data, ((MathSet<?>) o).data);
    }

    /**
     * @return (int) wrapped data's hashcode
     */
    @Override
    public int hashCode() {
        return data.hashCode();
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
