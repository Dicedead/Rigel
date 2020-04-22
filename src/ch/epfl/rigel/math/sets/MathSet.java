package ch.epfl.rigel.math.sets;

import javafx.util.Pair;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public class MathSet<T> implements Iterable<T> {

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
    public MathSet(MathSet<T> t) {
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
    public final MathSet<MathSet<T>> powerSet() {
        return powerSet(this);
    }

    private MathSet<MathSet<T>> powerSet(MathSet<T> set) {
        Set<T> Cset = set.getData();
        if (Cset.isEmpty())
            return new MathSet<>(Set.of());

        final T firstElement = Cset.iterator().next();
        Set<T> subset = new HashSet<>(Cset);
        subset.remove(firstElement);
        final Collection<Set<T>> subPowerSet = powerSet(new MathSet<>(subset)).image(MathSet::getData).getData();
        Set<Set<T>> powerSet = new HashSet<>();
        for (Set<T> s : subPowerSet) {
            Set<T> s1 = new HashSet<>(s);
            s1.add(firstElement);
            powerSet.add(s);
            powerSet.add(s1);
        }

        return powerSet.stream().map(MathSet::new).collect(toMathSet());
    }

    /**
     * Set theoristic intersection
     * @param others the Set to intersect with
     * @return A MathSet containing only those elements that lies in both sets
     */
    public final MathSet<T> intersection(final MathSet<T> others) {
        return intersection(Collections.singleton(others));
    }

    /**
     * Set theoristic cartesian product
     * @param other the Set to "multiply" with
     * @return A MathSet containing all possible pairs of elements from other ant this MathSet
     */
    public final <U> MathSet<Pair<T, U>> product(final MathSet<U> other) {
        return product(Collections.singleton(other));
    }
    /**
     * Set theoristic union
     * @param others the Set to union with
     * @return A MathSet containing all elements that lies in one of the sets
     */
    public final MathSet<T> union(final MathSet<T> others) {
        return union(Collections.singleton(others));
    }
    /**
     * Set theoristic intersection
     * @param others the collection of Set to intersect with
     * @return A MathSet containing only those elements that lies in all sets
     */
    public final MathSet<T> intersection(final Collection<MathSet<T>> others) {
        return suchThat(others.stream().map(MathSet::predicateContains).collect(Collectors.toSet()));
    }
    /**
     * Set theoristic union followed by a cartesian product
     * @param other the Collection of Sets to "multiply" with
     * @return A MathSet containing all possible pairs of elements from other and all other MathSets in other
     */
    public final <U> MathSet<Pair<T, U>> product(final Collection<MathSet<U>> other) {
        return unionOf(image(t -> unionOf(other).image(u -> new Pair<>(t, u))));
    }

    /**
     * The directsum construct a space containing a copy of both sets
     * @param other the other set in the directsum
     * @param <U> the type of the other Set
     * @return a set containing this set and other as copys inside him
     */
    public final <U> MathSet<Maybe<T, U>> directSum(final MathSet<U> other) {
        return image(t -> new Maybe<T, U>(t, null)).union(other.image(v -> new Maybe<>(null, v)));
    }
    /**
     * The directsum construct a space containing a copy of all sets
     * @param other the other sets in the directsum
     * @param <U> the type of the other Sets
     * @return a set containing this set and others as copies inside him
     */
    public final <U> MathSet<Maybe<T, U>> directSum(final Collection<MathSet<U>> other) {
        return image(t -> new Maybe<T, U>(t, null)).union(other.stream().map(s -> s.image(u -> new Maybe<T, U>(null, u)))
                .collect(Collectors.toList()));
    }
    /**
     * Set theoristic union
     * @param others the Sets to union with
     * @return A MathSet containing all elements that lies in one of the sets
     */
    public final MathSet<T> union(final Collection<MathSet<T>> others) {
        return Stream.concat(this.stream(), others.stream().flatMap(s -> s.getData().stream())).collect(toMathSet());
    }

    /**
     * Checks if a given set is a subset of the current instance
     * @param other the potential subset
     * @return wether all elements of other are in this set
     */
    public final boolean containsSet(final MathSet<T> other) {
        return data.containsAll(other.getData());
    }

    /**
     * Checks if an element is part of this set
     * @param t the element to test
     * @return wether the element t is in this set
     */
    public final boolean contains(final T t) {
        return data.contains(t);
    }

    /**
     * Set theoric substraction
     * @param other the set to substract
     * @return the set containing all elements of this set except those lying in other
     */
    public final MathSet<T> minusSet(final MathSet<T> other) {
        return suchThat(Predicate.not(other::contains));
    }
    /**
     * Set theoric substraction
     * @param other the element to substract
     * @return the set containing all elements of this set except other
     */
    public final MathSet<T> minus(final T other) {
        return suchThat(p -> !p.equals(other));
    }

    /**
     * Allows to select elements according to a predicate
     * @param t the predicate that each element will have to respect
     * @return the set of all elements in this set that complies to t
     */
    public final MathSet<T> suchThat(final Predicate<T> t) {
        return stream().filter(t).collect(toMathSet());
    }

    /**
     * Allows to select elements according to predicates
     * @param t the predicates that each element will have to respect
     * @return the set of all elements in this set that complies to all t
     */
    public final MathSet<T> suchThat(final Collection<Predicate<T>> t) {
        return stream().filter(l -> t.stream().allMatch(r -> r.test(l))).collect(toMathSet());
    }


    /**
     *
     * @return allows to traverse the set as a stream
     */

    public Stream<T> stream() {
        return data.stream();
    }

    /**
     *
     * @return the size of the current set
     */
    public final int cardinality() {
        return data.size();
    }

    /**
     *
     * @return wether the set contains at least one element or not
     */
    public final boolean isEmpty() {
        return data.size() == 0;
    }

    /**
     * @return the data wrapped by the set in its raw form
     */
    public final Set<T> getData() {
        return data;
    }

    /**
     *
     * @return a useful predicate checking wether an element is in the set
     */
    public final Equation<T> predicateContains() {
        return this::contains;
    }

    /**
     * The image of this set by a function
     * @param f the function to apply
     * @param <U> the codomain type
     * @return the MathSet containing all the lements produced by f when applied on this set
     */
    public <U> MathSet<U> image(SetFunction<T, U> f) {
        return f.apply(this);
    }

    /**
     * Returns the function underlying a setFunction
     * @param f the function to de promote
     * @param <U> the codomain type
     * @return the underlying Function of f
     */
    public final <U> Function<T, U> lift(SetFunction<T, U> f) {
        return f;
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
     * Set theoric union
     * @param sets the set to combine
     * @param <T> the type of those sets
     * @return A sett containing all elements from each sets
     */
    public static <T> MathSet<T> unionOf(final MathSet<MathSet<T>> sets) {
        if (sets.cardinality() <= 1) {
            final Optional<MathSet<T>> potentialSet = sets.stream().findAny();
            return (potentialSet.isEmpty()) ? emptySet() : new MathSet<>(potentialSet.get());
        }
        return sets.stream().flatMap(MathSet::stream).collect(toMathSet());
    }

    /**
     * Set theoric union
     * @param sets the set to combine
     * @param <T> the type of those sets
     * @return A sett containing all elements from each sets
     */
    public static <T> MathSet<T> unionOf(final Collection<MathSet<T>> sets) {
        return unionOf(new MathSet<>(sets));
    }

    /**
     * Set theoric intersection
     * @param sets the set to combine
     * @param <T> the type of those sets
     * @return A sett containing only elements lyinig in each sets
     */
    public static <T> MathSet<T> intersectionOf(final Collection<MathSet<T>> sets) {
        if (sets.size() <= 1) {
            final Optional<MathSet<T>> potentialSet = sets.stream().findAny();
            return (potentialSet.isEmpty()) ? emptySet() : new MathSet<>(potentialSet.get());
        }
        return sets.iterator().next().intersection(sets);
    }

    /**
     *
     * @param <T> Any type that is needed
     * @return a reference to the set with 0 elements
     */
    public static <T> MathSet<T> emptySet() {
        return new MathSet<>(Set.of());
    }

    /**
     * Allows to select elements according to predicates
     * @param t the predicates that each element will have to respect
     * @param set the set to apply the predicates on
     * @return the set of all elements in this set that complies to all t
     */
    public static <T> MathSet<T> suchThat(final Predicate<T> t, final MathSet<T> set) {
        return set.suchThat(t);
    }
    /**
     * Allows to select elements according to predicates
     * @param t the predicates that each element will have to respect
     * @param set the set to apply the predicates on
     * @return the set of all elements in this set that complies to all t
     */
    public static <T> MathSet<T> suchThat(final Collection<Predicate<T>> t, final MathSet<T> set) {
        return set.suchThat(t);
    }
    /**
     * Allows to select elements according to predicates
     * @param t the predicates that each element will have to respect
     * @param set the set to apply the predicates on
     * @return the set of all elements in this set that complies to all t
     */
    public static <T> MathSet<T> suchThat(final Collection<Predicate<T>> t, final Collection<MathSet<T>> set) {
        return unionOf(set).suchThat(t);
    }
    /**
     * Allows to select elements according to predicates
     * @param t the predicates that each element will have to respect
     * @param set the set to apply the predicates on
     * @return the set of all elements in this set that complies to all t
     */
    public static <T> MathSet<T> suchThat(final Predicate<T> t, final Collection<MathSet<T>> set) {
        return unionOf(set).suchThat(t);
    }

    /**
     *
     * @return an element from the current set
     */
    public T getElement() {
        return stream().findFirst().orElseThrow(
                () -> new NoSuchElementException("Tried to get element from empty set."));
    }

    /**
     * @param f the function needed to map to a comparable number
     * @return the minimal element according to f
     */
    public final T minOf(SetFunction<T, Number> f) {
        return stream().min(Comparator.comparingDouble(t -> f.apply(t).doubleValue())).orElseThrow();
    }
    /**
     * @param f the function needed to map to a comparable number
     * @return the maximal element according to f
     */
    public final T maxOf(SetFunction<T, Number> f) {
        return stream().max(Comparator.comparingDouble(t -> f.apply(t).doubleValue())).orElseThrow();
    }
    /**
     *
     * @return an element from the current set complying with t
     */
    public T getElement(final Predicate<T> t) {
        return suchThat(t).getElement();
    }

    /**
     *
     * @return a stream capable of beeing parallelised
     */
    public Stream<T> parallelStream() {
        return data.parallelStream();
    }

    @Override
    public Iterator<T> iterator() {
        return data.iterator();
    }

    @Override
    public Spliterator<T> spliterator() {
        return data.spliterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        data.forEach(action);
    }

    /**
     *
     * @return An iterator on subsets of this set
     */
    public final Iterator<MathSet<T>> setIterator() {
        return powerSet().iterator();
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
