package ch.epfl.rigel.math.sets;

import javafx.util.Pair;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MathSet<T> {

    private final Set<T> data;

    public MathSet(Collection<T> t) {
        data = Set.copyOf(t);
    }
    public MathSet(MathSet<T> t) {
        data = t.getData();
    }
    @SafeVarargs
    public static <T> MathSet<T> of (T... t)
    {
        return new MathSet<T>(Set.of(t));
    }
    public MathSet<MathSet<T>> powerSet()
    {
        return powerSet(this);
    }
    private MathSet<MathSet<T>> powerSet(MathSet<T> set)
    {
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

        return new MathSet<>(powerSet.stream().map(MathSet::new).collect(Collectors.toSet()));
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
        return suchThat(others.stream().map(MathSet::isIn).collect(Collectors.toSet()));
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
                other.stream().map(s -> s.image(u -> new Pair<>(tP, u))).collect(MathSet.union())));
    }

    public MathSet<T> union(final Collection<MathSet<T>> others) {
        return others.stream().flatMap(s -> s.getData().stream()).collect(MathSet.toSet());
    }

    public boolean in(final T t) {
        return data.contains(t);
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
                Collector.Characteristics.CONCURRENT,
                Collector.Characteristics.IDENTITY_FINISH,
                Collector.Characteristics.UNORDERED);
    }

    static public <T> Collector<MathSet<T>, ?, MathSet<T>> intersection() {
        return Collector.of(
                () -> new MathSet<>(new HashSet<>()),
                MathSet::intersection,
                MathSet::intersection,
                Collector.Characteristics.CONCURRENT,
                Collector.Characteristics.IDENTITY_FINISH,
                Collector.Characteristics.UNORDERED);
    }


    public static <T> MathSet<T> unionOf(final Collection<MathSet<T>> sets) {
        if (sets.size() <= 1) {
            final Optional<MathSet<T>> potentialSet = sets.stream().findAny();
            return (potentialSet.isEmpty()) ? emptySet() : new MathSet<>(potentialSet.get());
        }
        return sets.iterator().next().union(sets);
    }

    public static <T> MathSet<T> intersectionOf(final Collection<MathSet<T>> sets) {
        if (sets.size() <= 1) {
            final Optional<MathSet<T>> potentialSet = sets.stream().findAny();
            return (potentialSet.isEmpty()) ? emptySet() : new MathSet<>(potentialSet.get());
        }
        return sets.iterator().next().intersection(sets);
    }


    public static <T> MathSet<T> emptySet (){return  new MathSet<>(Set.of());};
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

    public Iterator<MathSet<T>> setIterator() {
        return powerSet().getData().iterator();
    }
    public void forEachSet(Consumer<? super MathSet<T>> action) {
        powerSet().getData().forEach(action);
    }

    public MathSet<MathSet<T>> suchThatSet(final Predicate<MathSet<T>> t)
    {
        return powerSet().suchThat(t);
    }

    public MathSet<MathSet<T>> suchThatSet(final Collection<Predicate<MathSet<T>>> t)
    {
        return powerSet().suchThat(t);
    }

    public T getElement()
    {
        return stream().findFirst().orElseThrow();
    }

    public T getElement(final Predicate<T> t)
    {
        return stream().filter(t).findFirst().orElseThrow();
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


}
