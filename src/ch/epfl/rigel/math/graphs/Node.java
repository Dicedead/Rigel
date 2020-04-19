package ch.epfl.rigel.math.graphs;

import ch.epfl.rigel.math.sets.OrderedSet;

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
public class Node<T> {

    final private T value;
    final private Node<T> parent;
    final private int depth;

    /**
     * Root Node Constructor: creates a node with no parent node
     * Node<T> is immutable iff T is immutable
     *
     * @param value (T) value stored in the node
     */
    public Node(final T value) {
        this.value = value;
        this.parent = null;
        this.depth = 0;
    }

    /**
     * Child Node Constructor: creates a node with a non null parent node
     * Node<T> is immutable iff T is immutable
     *
     * @param value (T) value stored in the node
     * @param parent (Node<T>) non null parent node
     * @throws NullPointerException if parent is null
     */
    private Node(T value, Node<T> parent) {
        this.value = value;
        this.parent = Objects.requireNonNull(parent);
        this.depth = parent.getDepth() + 1;
    }


    /**
     * Creates a Node with this as parent and 'childValue' as value
     *
     * @param childValue (T) child node's value
     * @return (Node<T>) said node containing said value
     */
    public Node<T> createChild(final T childValue) {
        return new Node<>(childValue, this);
    }

    /**
     * @return (Path<Node<T>>) a Path of all the nodes higher than this node; i.e its parent and (recursively) the parent
     * of its parent
     */
    public Path<Node<T>> hierarchy() {
         return new Path<>(new OrderedSet<>(new ArrayList<>(hierarchyRecur(new ArrayDeque<>(Collections.singleton(this))))));
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
     * @return (int) this node's depth (number of nodes in hierarchy)
     */
    public int getDepth() { return depth; }

    /**
     * @return (boolean) whether this node has a parent or not
     */
    public boolean isRoot() {
        return parent == null;
    }

    public boolean isParentOf(Node<T> potentialChild) {
        return this.equals(potentialChild.getParent());
    }

    public static <X> boolean areRelated(Node<X> node1, Node<X> node2) {
        return node1.hierarchy().contains(node2) || node2.hierarchy().contains(node1);
    }

    public static <X> boolean areRelatedRootless(Node<X> node1, Node<X> node2) {
        final Path<Node<X>> node1Hier = node1.hierarchy();
        final Path<Node<X>> node2Hier = node2.hierarchy();
        return (node1Hier.contains(node2) && node1Hier.cardinality() > 1)
                || (node2Hier.contains(node1) && node2Hier.cardinality() > 1);
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