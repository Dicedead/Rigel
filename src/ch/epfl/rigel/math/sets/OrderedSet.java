package ch.epfl.rigel.math.sets;

import ch.epfl.rigel.Preconditions;

public final class OrderedSet<T> extends MathSet<T> {
    private final Relation.Order<T> comparator;

    public OrderedSet(MathSet<T> m,Relation.Order<T> comparator) {
        super(m);
        this.comparator = comparator;
    }

    public Relation.COMP compare(T t, T u)
    {
        Preconditions.checkArgument(contains(t) && contains(u));
        return comparator.compare(t, u);
    }

    public MathSet<T> min()
    {
        return suchThat(p -> comparator.partialApply(p).apply(this).equals(of(Relation.COMP.LESS)));
    }

    public MathSet<T> max()
    {
        return suchThat(p -> comparator.partialApply(p).apply(this).equals(of(Relation.COMP.GREATER)));
    }

    public MathSet<T> moreThan(T t)
    {
        return suchThat(p -> comparator.partialApply(t).apply(p) == (Relation.COMP.LESS));
    }

    public MathSet<T> lessThan(T t)
    {
        return suchThat(p -> comparator.partialApply(t).apply(p) == (Relation.COMP.GREATER));
    }

    public MathSet<T> equalsTo(T t)
    {
        return suchThat(p -> comparator.partialApply(t).apply(p) == (Relation.COMP.EQUAL));
    }

}
