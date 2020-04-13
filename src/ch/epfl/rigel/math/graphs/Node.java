package ch.epfl.rigel.math.graphs;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Objects;

/**
 * Implementation of hierarchized nodes to be used in directed graphs
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Node<T> {

    final private T value;
    final private Node<T> parent;

    /**
     * Root Node Constructor: creates a node with no parent node
     * Node<T> is immutable iff T is immutable
     *
     * @param value (T) value stored in the node
     */
    public Node(final T value) {
        this.value = value;
        this.parent = null;
    }

    /**
     * Child Node Constructor: creates a node with a non null parent node
     * Node<T> is immutable iff T is immutable
     *
     * @param value (T) value stored in the node
     * @param parent (Node<T>) non null parent node
     * @throws NullPointerException if parent is null
     */
    public Node(T value, Node<T> parent) {
        this.value = value;
        this.parent = Objects.requireNonNull(parent);
    }

    /**
     * Creates a Node with this as parent and 'childValue' as value
     *
     * @param childValue (T) child node's value
     * @return (Node<T>) said node containing said value
     */
    public Node<T> createChild(final T childValue) {
        return new Node<T>(childValue, this);
    }

    /**
     * @return (Path<Node<T>>) a Path of all the nodes higher than this node; ie its parent and (recursively) the parent
     * of its parent
     */
    public Path<Node<T>> hierarchy() {
        return new Path<>(new ArrayList<>(hierarchyRecur(new ArrayDeque<>(Collections.singleton(this)))));
    }

    /**
     * @return (T) value stored in this node
     * Node<T> is immutable iff T is immutable
     */
    public T getValue() {
        return value;
    }

    /**
     * @return (Node<T>) this node's parent
     */
    public Node<T> getParent() {
        return parent;
    }

    /**
     * @return (boolean) whether this node has a parent
     */
    public boolean isRoot() {
        return parent == null;
    }

    private Deque<Node<T>> hierarchyRecur(final Deque<Node<T>> nodeDeque) {
        if (nodeDeque.getLast().isRoot())
            return nodeDeque;
        else {
            nodeDeque.addLast(nodeDeque.getLast().parent);
            return hierarchyRecur(nodeDeque);
        }
    }
}