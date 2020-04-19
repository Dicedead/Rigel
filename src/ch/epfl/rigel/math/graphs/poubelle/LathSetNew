package ch.epfl.rigel.math.sets;

import javafx.util.Pair;

import java.util.*;
import java.util.function.*;
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
    public MathSet<T> intersection(final Collection<MathSet<T>> others) {
        return suchThat(others.stream().map(MathSet::predicateContains).collect(Collectors.toSet()));
    }

    public <U> MathSet<Pair<T, U>> product(final MathSet<U> other) {
        return product(Collections.singleton(other));
    }
    public <U> MathSet<Pair<T, U>> product(final Collection<MathSet<U>> other) {
        return unionOf(image(t -> unionOf(other).image(u -> new Pair<>(t, u))));
    }

    public MathSet<T> union(final MathSet<T> others) {
        return union(Collections.singleton(others));
    }
    public MathSet<T> union(final Collection<MathSet<T>> others) {
        return others.stream().flatMap(s -> s.getData().stream()).collect(MathSet.toSet());
    }


    public <U> MathSet<Maybe<T, U>> directSum(final MathSet<U> other) {
         return image(t -> new Maybe<T, U>(t, null)).union(other.image(v -> new Maybe<T, U>(null, v)));
    }
    public <U> MathSet<Maybe<T, U>> directSum(final Collection<MathSet<U>> other) {
        return image(t -> new Maybe<T, U>(t, null)).union(other.stream().map(s -> s.image(u -> new Maybe<T, U>(null, u))).collect(Collectors.toList()));
    }


    public boolean containsSet(final MathSet<T> other) {
        return data.containsAll(other.getData());
    }
    public boolean contains(final T t) { return data.contains(t); }
    public Predicate<T> predicateContains() {
        return this::contains;
    }
    public boolean in (T t)
    {
        return data.contains(t);
    }

    public MathSet<T> without(final MathSet<T> other) {
        return suchThat(Predicate.not(other::contains));
    }
    public MathSet<T> without(final T other)
    {
        return suchThat(p -> !p.equals(other));
    }
    public MathSet<T> minusSet(final MathSet<T> other)
    {
        return intersection(without(other));
    }
    public MathSet<T> minus(final T other)
    {
        return intersection(without(of(other)));
    }

    public MathSet<T> suchThat(final Predicate<T> t) {
        return new MathSet<T>(stream().filter(t).collect(Collectors.toSet()));
    }
    public MathSet<T> suchThat(final Collection<Predicate<T>> t) {
        return stream().filter(l -> t.stream().allMatch(r -> r.test(l))).collect(MathSet.toSet());
    }

    public T minOf(ToIntFunction<T> f)
    {
        return stream().min(Comparator.comparingInt(f)).orElseThrow();
    }
    public T maxOf(ToIntFunction<T> f)
    {
        return stream().max(Comparator.comparingInt(f)).orElseThrow();
    }


    public Stream<T> stream() {
        return data.stream();
    }

    public int cardinality() {
        return data.size();
    }

    public static <T> MathSet<T> emptySet (){return  new MathSet<>(Set.of());}
    public boolean isEmpty() {
        return data.size() == 0;
    }

    public Set<T> getData() {
        return data;
    }
    public T getElement()
    {
        return stream().findFirst().orElseThrow();
    }
    public T getElement(final Predicate<T> t)
    {
        return suchThat(t).getElement();
    }



    public <U> MathSet<U> image(SetFunction<T, U> f) {
        return f.apply(this);
    }
    public <U> Function<T, U> lift(SetFunction<T, U> f) {
        return f;
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


    public static <T> MathSet<T> unionOf(final MathSet<MathSet<T>> sets) {
        if (sets.cardinality() <= 1) {
            final Optional<MathSet<T>> potentialSet = sets.stream().findAny();
            return (potentialSet.isEmpty()) ? emptySet() : new MathSet<>(potentialSet.get());
        }
        return unionOf(sets.getData());
    }

    public static <T> MathSet<T> unionOf(final Collection<MathSet<T>> sets) {
        return unionOf(new MathSet<MathSet<T>>(sets));
    }

    public static <T> MathSet<T> intersectionOf(final Collection<MathSet<T>> sets) {
        if (sets.size() <= 1) {
            final Optional<MathSet<T>> potentialSet = sets.stream().findAny();
            return (potentialSet.isEmpty()) ? emptySet() : new MathSet<>(potentialSet.get());
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
