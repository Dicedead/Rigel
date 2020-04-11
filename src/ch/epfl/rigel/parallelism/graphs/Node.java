package ch.epfl.rigel.parallelism.graphs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Node<T> {

    final private T t;
    final private Node<T> parent;
    private final List<Node<T>> children;
    private final Set<Node<T>> graph;

    public Set<Node<T>> getGraph() {
        return graph;
    }

    public void link(final List<T> v)
    {
        v.forEach(value -> children.add(new Node<T>(value, this)));
    }

    public Node<T> link(final T value)
    {
        var a = new Node<T>(value, this);
        children.add(a);
        return a;
    }

    public Node(final T t)
    {
        this.t = t;
        this.parent = null;
        this.children = new ArrayList<>();
        graph = new HashSet<>();
        graph.add(this);
    }

    public Node(T t, Node<T> parents)
    {
        this.t = t;
        this.parent = parents;
        this.children = new ArrayList<>();

        parent.getGraph().add(this);
        graph = parent.getGraph();
    }

    public boolean isLeaf(){return children.isEmpty();}

    public T getValue(){return t;}

    public Node<T> getParents() {return parent;}

    public List<Node<T>> getChildren() {
        return children;
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() == Node.class && ((Node) o).getValue().equals(this.getValue())) {
            return ((Node) o).getChildren().equals(getChildren()) && ((Node) o).getParents() == getParents();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }
}