package ch.epfl.rigel.math.graphs;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.sets.abtract.AbstractMathSet;
import ch.epfl.rigel.math.sets.concrete.MathSet;
import ch.epfl.rigel.math.sets.concrete.OrderedTuple;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of hierarchized nodes to be used in directed graphs
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Node<T> {

    final private T value;
    final private Node<T> parent;
    final private int depth;
    final private Path<Node<T>> hierarchy;
    private int nmbrOfChildren;
    private boolean lockNode;

    /**
     * Root Node Constructor: creates a node with no parent node
     * Node<T> is immutable after applying lockNode() iff T is immutable
     *
     * @param value (T) value stored in the node
     */
    public Node(final T value) {
        this(value, null);
    }

    /**
     * Child Node Constructor: creates a node with a non null parent node
     * Node<T> is immutable after applying lockNode() iff T is immutable
     *
     * @param value  (T) value stored in the node
     * @param parent (Node<T>) non null parent node
     */
    public Node(T value, Node<T> parent) {
        if (parent != null) {
            Preconditions.checkArgument(!parent.lockNode);
            ++parent.nmbrOfChildren;
        }
        this.value = value;
        this.parent = parent;
        this.depth = (parent == null) ? 0 : parent.getDepth() + 1;
        this.hierarchy = new Path<>(new OrderedTuple<>(
                new ArrayList<>(hierarchyRecur(new ArrayDeque<>(Collections.singleton(this))))));
    }

    /**
     * Creates a Node with this as parent and 'childValue' as value
     *
     * @param childValue (T) child node's value
     * @return (Node < T >) said node containing said value
     */
    public Node<T> createChild(final T childValue) {
        return new Node<>(childValue, this);
    }

    /**
     * @return (Path < Node < T > >) a Path of all the nodes higher than this node; i.e its parent and (recursively) the parent
     * of its parent
     */
    public Path<Node<T>> hierarchy() {
        return hierarchy;
    }

    /**
     * @return (T) value stored in this node
     * Node<T> is immutable iff T is immutable
     */
    public T getValue() {
        return value;
    }

    /**
     * @return (Optional < Node < T > >) this node's parent, Optional.empty if null
     */
    public Optional<Node<T>> getParent() {
        return parent == null ? Optional.empty() : Optional.of(parent);
    }

    /**
     * @return (int) this node's depth (number of nodes in hierarchy)
     */
    public int getDepth() {
        return depth;
    }

    public Node<T> lockNode() {
        lockNode = true;
        return this;
    }

    @SafeVarargs
    public static <X> AbstractMathSet<Node<X>> bunk(Node<X>... nodes) {
        return MathSet.of(nodes).image(Node::lockNode);
    }

    /**
     * @return (boolean) whether this node has a parent or not
     */
    public boolean isRoot() {
        return parent == null;
    }

    public boolean isParentOf(Node<T> potentialChild) {
        return this.equals(potentialChild.getParent().orElse(null));
    }

    /**
     * Checks whether the two given nodes are in the same branch.
     * Note that any Node is always related to all nodes created through createChild applied upon it and its
     * descendants.
     *
     * @param node1 starting node
     * @param node2 finishing node
     * @param <X> underlying type
     * @return whether one is the parent of the other
     */
    public static <X> boolean areRelated(Node<X> node1, Node<X> node2) {
        return node1.hierarchy.contains(node2) || node2.hierarchy.contains(node1);
    }

    public static <X> boolean areRelatedRootless(Node<X> node1, Node<X> node2) {
        if (node1.equals(node2)) return true;
        if (!areRelated(node1, node2)) return false;

        final Path<Node<X>> chosenHier = (node1.hierarchy.cardinality() > node2.hierarchy.cardinality()) ?
                node1.hierarchy : node2.hierarchy;
        return chosenHier.stream().takeWhile(node -> node.nmbrOfChildren <= 1)
                .collect(Collectors.toSet()).containsAll(Set.of(node1, node2));

    }

    private Deque<Node<T>> hierarchyRecur(final Deque<Node<T>> nodeDeque) {
        if (nodeDeque.getLast().isRoot())
            return nodeDeque;
        else {
            nodeDeque.addLast(nodeDeque.getLast().parent);
            return hierarchyRecur(nodeDeque);
        }
    }

    public String toString() {
        return getValue().toString();
    }
}