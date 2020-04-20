package ch.epfl.rigel.math.sets;

import javafx.util.Pair;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MathSet<T> implements Iterable<T> {

    private final Set<T> data;

    public MathSet(Collection<T> t) {
        data = Set.copyOf(t);
    }

    public MathSet(MathSet<T> t) {
        data = t.getData();
    }

    @SafeVarargs
    public static <T> MathSet<T> of(T... t) {
        return new MathSet<>(Set.of(t));
    }

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
        final Collection<Set<T>> subPowerSet = powerSet(new MathSet<T>(subset)).image(MathSet::getData).getData();
        Set<Set<T>> powerSet = new HashSet<>();
        for (Set<T> s : subPowerSet) {
            Set<T> s1 = new HashSet<>(s);
            s1.add(firstElement);
            powerSet.add(s);
            powerSet.add(s1);
        }

        return powerSet.stream().map(MathSet::new).collect(toMathSet());
    }

    public final MathSet<T> intersection(final MathSet<T> others) {
        return intersection(Collections.singleton(others));
    }

    public final <U> MathSet<Pair<T, U>> product(final MathSet<U> other) {
        return product(Collections.singleton(other));
    }

    public final MathSet<T> union(final MathSet<T> others) {
        return union(Collections.singleton(others));
    }

    public final MathSet<T> intersection(final Collection<MathSet<T>> others) {
        return suchThat(others.stream().map(MathSet::predicateContains).collect(Collectors.toSet()));
    }

    public final <U> MathSet<Pair<T, U>> product(final Collection<MathSet<U>> other) {
        return unionOf(image(t -> unionOf(other).image(u -> new Pair<>(t, u))));
    }

    public final <U> MathSet<Maybe<T, U>> directSum(final MathSet<U> other) {
        return image(t -> new Maybe<T, U>(t, null)).union(other.image(v -> new Maybe<T, U>(null, v)));
    }

    public final <U> MathSet<Maybe<T, U>> directSum(final Collection<MathSet<U>> other) {
        return image(t -> new Maybe<T, U>(t, null)).union(other.stream().map(s -> s.image(u -> new Maybe<T, U>(null, u)))
                .collect(Collectors.toList()));
    }

    public final MathSet<T> union(final Collection<MathSet<T>> others) {
        return Stream.concat(this.stream(), others.stream().flatMap(s -> s.getData().stream())).collect(toMathSet());
    }

    public final boolean containsSet(final MathSet<T> other) {
        return data.containsAll(other.getData());
    }

    public final boolean contains(final T t) {
        return data.contains(t);
    }

    public final MathSet<T> without(final MathSet<T> other) {
        return suchThat(Predicate.not(other::contains));
    }

    public final MathSet<T> suchThat(final Predicate<T> t) {
        return stream().filter(t).collect(toMathSet());
    }

    public final MathSet<T> suchThat(final Collection<Predicate<T>> t) {
        return stream().filter(l -> t.stream().allMatch(r -> r.test(l))).collect(toMathSet());
    }

    public final MathSet<T> without(final T other) {
        return suchThat(p -> !p.equals(other));
    }

    public final MathSet<T> minusSet(final MathSet<T> other) {
        return intersection(without(other));
    }

    public final MathSet<T> minus(final T other) {
        return intersection(without(of(other)));
    }

    public final int cardinality() {
        return data.size();
    }

    public final boolean isEmpty() {
        return data.size() == 0;
    }

    public final Set<T> getData() {
        return data;
    }

    public final Predicate<T> predicateContains() {
        return this::contains;
    }

    public <U> MathSet<U> image(SetFunction<T, U> f) {
        return f.apply(this);
    }

    public final <U> Function<T, U> lift(SetFunction<T, U> f) {
        return f;
    }

    static public <T> Collector<T, ?, MathSet<T>> toMathSet() {
        return Collectors.collectingAndThen(Collectors.toSet(), MathSet::new);
    }

    public static <T> MathSet<T> unionOf(final MathSet<MathSet<T>> sets) {
        if (sets.cardinality() <= 1) {
            final Optional<MathSet<T>> potentialSet = sets.stream().findAny();
            return (potentialSet.isEmpty()) ? emptySet() : new MathSet<>(potentialSet.get());
        }
        return sets.stream().flatMap(MathSet::stream).collect(toMathSet());
    }

    public static <T> MathSet<T> unionOf(final Collection<MathSet<T>> sets) {
        return unionOf(new MathSet<>(sets));
    }

    public static <T> MathSet<T> intersectionOf(final Collection<MathSet<T>> sets) {
        if (sets.size() <= 1) {
            final Optional<MathSet<T>> potentialSet = sets.stream().findAny();
            return (potentialSet.isEmpty()) ? emptySet() : new MathSet<>(potentialSet.get());
        }
        return sets.iterator().next().intersection(sets);
    }


    public static <T> MathSet<T> emptySet() {
        return new MathSet<>(Set.of());
    }

    public static <T> MathSet<T> suchThat(final Predicate<T> t, final MathSet<T> set) {
        return set.suchThat(t);
    }

    public static <T> MathSet<T> suchThat(final Collection<Predicate<T>> t, final MathSet<T> set) {
        return set.suchThat(t);
    }

    public static <T> MathSet<T> suchThat(final Collection<Predicate<T>> t, final Collection<MathSet<T>> set) {
        return unionOf(set).suchThat(t);
    }

    public static <T> MathSet<T> suchThat(final Predicate<T> t, final Collection<MathSet<T>> set) {
        return unionOf(set).suchThat(t);
    }

    public final MathSet<MathSet<T>> suchThatSet(final Predicate<MathSet<T>> t) {
        return powerSet().suchThat(t);
    }

    public MathSet<MathSet<T>> suchThatSet(final Collection<Predicate<MathSet<T>>> t) {
        return powerSet().suchThat(t);
    }

    public T getElement() {
        return stream().findFirst().orElseThrow(
                () -> new NoSuchElementException("Tried to get element from empty set."));
    }

    public final T minOf(ToIntFunction<T> f) {
        return stream().min(Comparator.comparingInt(f)).orElseThrow();
    }

    public final T maxOf(ToIntFunction<T> f) {
        return stream().max(Comparator.comparingInt(f)).orElseThrow();
    }

    public T getElement(final Predicate<T> t) {
        return suchThat(t).getElement();
    }

    public Stream<T> stream() {
        return data.stream();
    }

    public Stream<T> parallelStream() {
        return data.parallelStream();
    }

    @Override
    public final Iterator<T> iterator() {
        return data.iterator();
    }

    @Override
    public final Spliterator<T> spliterator() {
        return data.spliterator();
    }

    @Override
    public final void forEach(Consumer<? super T> action) {
        data.forEach(action);
    }

    public final Iterator<MathSet<T>> setIterator() {
        return powerSet().iterator();
    }

    public void forEachSet(Consumer<? super MathSet<T>> action) {
        powerSet().forEach(action);
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
