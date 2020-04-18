package ch.epfl.rigel.math.sets;

import javafx.util.Pair;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public class MathSet<T> {

    private final Set<T> data;

    public MathSet(Collection<T> t) {
        data = Set.copyOf(t);
    }

    @SafeVarargs
    public MathSet(T... ts) { data = Set.of(ts); }

    public MathSet(MathSet<T> t) {
        data = t.getData();
    }

    public MathSet<T> intersection(final MathSet<T> others) {
        return intersection(Collections.singleton(others));
    }

    public <U> MathSet<Pair<T, U>> product(final MathSet<U> other) {
        return product(Collections.singleton(other));
    }

    public MathSet<T> union(final MathSet<T> others) {
        return union(Collections.singleton(others));
    }

    public MathSet<T> intersection(final Collection<MathSet<T>> others) {
        return suchThat(others.stream().map(MathSet::predicateContains).collect(Collectors.toSet()));
    }

    public <U> MathSet<Pair<T, U>> product(final Collection<MathSet<U>> other) {
        return stream().map(t -> unionOf(other).image(u -> new Pair<>(t, u))).collect(MathSet.union());
    }

    public <U> MathSet<Pair<T, U>> directSum(final MathSet<U> other, T tP, U uP) {
        return unionOf(Set.of(image(t -> new Pair<>(t, uP)),
                other.image(u -> new Pair<>(tP, u))));
    }

    public <U> MathSet<Pair<T, U>> directSum(final Collection<MathSet<U>> other, T tP, U uP) {
        return unionOf(Set.of(image(t -> new Pair<>(t, uP)),
                other.stream().map(s -> s.image(u -> new Pair<T, U>(tP, u))).collect(MathSet.union())));
    }

    public MathSet<T> union(final Collection<MathSet<T>> others) {
        return new MathSet<>(others.stream().flatMap(s -> s.getData().stream()).collect(Collectors.toSet()));
    }

    public boolean containsSet(final MathSet<T> other) {
        return data.containsAll(other.getData());
    }

    public boolean contains(final T t) { return data.contains(t); }

    public MathSet<T> without(final MathSet<T> other) {
        return suchThat(Predicate.not(other::contains));
    }

    public MathSet<T> suchThat(final Predicate<T> t) {
        return new MathSet<T>(stream().filter(t).collect(Collectors.toSet()));
    }

    public MathSet<T> suchThat(final Collection<Predicate<T>> t) {
        return stream().filter(l -> t.stream().allMatch(r -> r.test(l))).collect(MathSet.toSet());
    }
    public MathSet<T> without(final T other)
    {
        return suchThat(p -> !p.equals(other));
    }

    public MathSet<T> minusSet(final MathSet<T> other)
    {
        return intersection(Collections.singleton(without(other)));
    }

    public MathSet<T> minus(final T other)
    {
        return intersection(Collections.singleton(without(other)));
    }

    public Stream<T> stream() {
        return data.stream();
    }

    public int cardinality() {
        return data.size();
    }

    public boolean isEmpty() {
        return data.size() == 0;
    }

    public Set<T> getData() {
        return data;
    }

    public Predicate<T> predicateContains() {
        return this::contains;
    }

    public <U> MathSet<U> image(Function<T, U> f) {
        return new SetFunction<>(f).apply(this);
    }

    public <U> MathSet<U> image(SetFunction<T, U> f) {
        return f.apply(this);
    }

    public <U> Function<T, U> lift(SetFunction<T, U> f) {
        return f::applyOn;
    }

    static public <T> Collector<T, ?, MathSet<T>> toSet() {
        return Collector.of(
                () -> new MathSet<>(new HashSet<>()),
                (res, t) -> res.union(new MathSet<>(Collections.singleton(t))),
                MathSet::union,
                Collector.Characteristics.CONCURRENT,
                Collector.Characteristics.IDENTITY_FINISH,
                Collector.Characteristics.UNORDERED);
    }

    static public <T> Collector<MathSet<T>, ?, MathSet<T>> union() {
        return Collector.of(
                () -> new MathSet<>(new HashSet<>()),
                MathSet::union,
                MathSet::union,
                Collector.Characteristics.IDENTITY_FINISH,
                Collector.Characteristics.CONCURRENT,
                Collector.Characteristics.UNORDERED);
    }

    static public <T> Collector<MathSet<T>, ?, MathSet<T>> intersection() {
        return Collector.of(
                () -> new MathSet<>(new HashSet<>()),
                MathSet::intersection,
                MathSet::intersection,
                Collector.Characteristics.IDENTITY_FINISH,
                Collector.Characteristics.CONCURRENT,
                Collector.Characteristics.UNORDERED);
    }


    public static <T> MathSet<T> unionOf(final Collection<MathSet<T>> sets) {
        if (sets.size() <= 1) {
            final Optional<MathSet<T>> potentialSet = sets.stream().findAny();
            return (potentialSet.isEmpty()) ? new MathSet<>() : new MathSet<>(potentialSet.get());
        }
        return sets.iterator().next().union(sets);
    }

    public static <T> MathSet<T> intersectionOf(final Collection<MathSet<T>> sets) {
        if (sets.size() <= 1) {
            final Optional<MathSet<T>> potentialSet = sets.stream().findAny();
            return (potentialSet.isEmpty()) ? new MathSet<>() : new MathSet<>(potentialSet.get());
        }
        return sets.iterator().next().intersection(sets);
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

    @Override
    public String toString() {
        return data.toString();
    }
}
