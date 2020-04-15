package ch.epfl.rigel.math.sets;

import javafx.util.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MathSet<T> {

    private final Set<T> data;

    public MathSet(Collection<T> t)
    {
        data = Set.copyOf(t);
    }
    public MathSet<T> intersection (final MathSet<T> others)
    {
        return intersection(Collections.singleton(others));
    }
    public <U> MathSet<Pair<T, U>> product (final MathSet<U> other)
    {
        return product(Collections.singleton(other));
    }
    public MathSet<T> union(final MathSet<T> others)
    {
        return union(Collections.singleton(others));
    }
    public MathSet<T> intersection (final Collection<MathSet<T>> others)
    {
        return suchThat(others.stream().map(MathSet::isIn).collect(Collectors.toSet()));
    }

    public <U> MathSet<Pair<T, U>> product (final Collection<MathSet<U>> other)
    {
        return stream().map(t -> unionOf(other).image(u -> new Pair<>(t, u))).collect(MathSet.union());
    }

    public MathSet<T> union(final Collection<MathSet<T>> others)
    {
        return others.stream().flatMap(s -> s.getData().stream()).collect(MathSet.toSet());
    }
    public boolean in(final T t)
    {
        return data.contains(t);
    }
    public boolean contains(final MathSet<T> other)
    {
        return data.containsAll(other.getData());
    }
    public MathSet<T> complement(final MathSet<T> other)
    {
        return suchThat(Predicate.not(other::in));
    }
    public MathSet<T> suchThat(final Predicate<T> t)
    {
        return stream().filter(t).collect(MathSet.toSet());
    }
    public MathSet<T> suchThat(final Collection<Predicate<T>> t)
    {
        return stream().filter(l -> t.stream().allMatch(r -> r.test(l))).collect(MathSet.toSet());
    }
    public MathSet<T> minus(final MathSet<T> other)
    {
        return intersection(Collections.singleton(complement(other)));
    }

    public Stream<T> stream(){return data.stream();}
    public int cardinality(){return getData().size();}
    public Set<T> getData(){return data;}

    public Predicate<T> isIn()
    {
        return this::in;
    }
    public <U> MathSet<U> image (Function<T, U> f) {
        return new SetFunction<>(f).apply(this);
    }

    public <U> MathSet<U> image (SetFunction<T, U> f) {
        return f.apply(this);
    }
    public <U> Function<T, U> lift (SetFunction<T, U> f) {
        return f::applyOn;
    }
    static public <T> Collector<T, ?, MathSet<T>> toSet()
    {
        return Collector.of(
                () -> new MathSet<>(new HashSet<>()),
                (res, t) -> res.union(new MathSet<>(Collections.singleton(t))),
                MathSet::union,
                Collector.Characteristics.CONCURRENT,
                Collector.Characteristics.IDENTITY_FINISH,
                Collector.Characteristics.UNORDERED);
    }

    static public <T> Collector<MathSet<T>, ?, MathSet<T>> union()
    {
        return Collector.of(
                () -> new MathSet<>(new HashSet<>()),
                MathSet::union,
                MathSet::union,
                Collector.Characteristics.CONCURRENT,
                Collector.Characteristics.IDENTITY_FINISH,
                Collector.Characteristics.UNORDERED);
    }

    static public <T> Collector<MathSet<T>, ?, MathSet<T>> intersection()
    {
        return Collector.of(
                () -> new MathSet<>(new HashSet<>()),
                MathSet::intersection,
                MathSet::intersection,
                Collector.Characteristics.CONCURRENT,
                Collector.Characteristics.IDENTITY_FINISH,
                Collector.Characteristics.UNORDERED);
    }



    public static <T> MathSet<T> unionOf (final Collection<MathSet<T>> sets)
    {
        return sets.iterator().next().union(sets);
    }

    public static <T> MathSet<T> intersectionOf (final Collection<MathSet<T>> sets)
    {
        return sets.iterator().next().intersection(sets);
    }


    public static <T> MathSet<T> suchThat(final Predicate<T> t, final MathSet<T> set)
    {
        return set.suchThat(t);
    }

    public static <T> MathSet<T> suchThat(final Collection<Predicate<T>> t, final MathSet<T> set)
    {
        return set.suchThat(t);
    }

    public static <T> MathSet<T> suchThat(final Collection<Predicate<T>> t, final Collection<MathSet<T>> set)
    {
        return unionOf(set).suchThat(t);
    }

    public static <T> MathSet<T> suchThat(final Predicate<T> t, final Collection<MathSet<T>> set)
    {
        return unionOf(set).suchThat(t);
    }

}
