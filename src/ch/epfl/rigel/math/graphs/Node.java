package ch.epfl.rigel.math.graphs;

import java.util.*;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Node<T> {

    final private T t;
    final private Node<T> parent;

    public Node<T> link(final T value)
    {
        return new Node<T>(value, this);
    }

    public Node(final T t)
    {
        this.t = t;
        this.parent = null;
    }
    private Deque<Node<T>> hierarchyR(Deque<Node<T>> n)
    {
        if(n.getLast().isRoot())
            return n;
        else {
            n.addLast(n.getLast().parent);
            return hierarchyR(n);
        }
    }

    public Path<Node<T>> hierarchy()
    {
        return new Path<>(new ArrayList<>(hierarchyR(new ArrayDeque<>(Collections.singleton(this)))));
    }
    public boolean isRoot()
    {
        return parent == null;
    }
    public Node(T t, Node<T> parents)
    {
        this.t = t;
        this.parent = parents;
    }

    public T getValue(){return t;}

    public Node<T> getParents() {return parent;}


    @Override
    public boolean equals(Object o) {
        if (o.getClass() == Node.class && ((Node) o).getValue().equals(this.getValue())) {
            return ((Node) o).getValue().equals(getValue()) && ((Node) o).getParents() == getParents();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }
}