package ch.epfl.rigel.math.graphs;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.sets.abstraction.AbstractMathSet;
import ch.epfl.rigel.math.sets.implement.MathSet;
import ch.epfl.rigel.math.sets.implement.OrderedTuple;

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
public final class GraphNode<T> {

    final private T value;
    final private GraphNode<T> parent;
    final private int depth;
    final private Path<GraphNode<T>> hierarchy;
    private int nmbrOfChildren;
    private boolean lockNode;

    /**
     * Root Node Constructor: creates a node with no parent node
     * Node<T> is immutable after applying lockNode() iff T is immutable
     *
     * @param value (T) value stored in the node
     */
    public GraphNode(T value) {
        this(value, null);
    }

    /**
     * Child Node Constructor: creates a node with a non null parent node
     * Node<T> is immutable after applying lockNode() iff T is immutable
     *
     * @param value  (T) value stored in the node
     * @param parent (GraphNode<T>) non null parent node
     */
    public GraphNode(T value, GraphNode<T> parent) {
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
     * @return (GraphNode<T>) said node containing said value
     */
    public GraphNode<T> createChild(T childValue) {
        return new GraphNode<>(childValue, this);
    }

    /**
     * @return (Path<GraphNode<T>>) a Path of all the nodes higher than this node; i.e its parent and (recursively) the parent
     * of its parent until reaching the root of the hierarchy
     */
    public Path<GraphNode<T>> hierarchy() {
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
     * @return (Optional<GraphNode<T>>) this node's parent, Optional.empty if null
     */
    public Optional<GraphNode<T>> getParent() {
        return parent == null ? Optional.empty() : Optional.of(parent);
    }

    /**
     * @return (int) this node's depth (number of nodes in hierarchy)
     */
    public int getDepth() {
        return depth;
    }

    public GraphNode<T> lockNode() {
        lockNode = true;
        return this;
    }

    @SafeVarargs
    public static <X> AbstractMathSet<GraphNode<X>> bunk(GraphNode<X>... nodes) {
        return MathSet.of(nodes).image(GraphNode::lockNode);
    }

    /**
     * @return (boolean) whether this node has a parent or not
     */
    public boolean isRoot() {
        return parent == null;
    }

    public boolean isParentOf(GraphNode<T> potentialChild) {
        return this.equals(potentialChild.getParent().orElse(null));
    }

    public boolean isParent() { return nmbrOfChildren != 0; }

    /**
     * Checks whether the two given nodes are in the same branch - NOT an equivalence relation
     * Note that any Node is always related to all nodes created through createChild applied upon it and its
     * descendants. This particularly means that the root of all current nodes is related to all nodes - useful
     * for family-checking.
     *
     * @param node1 starting node
     * @param node2 finishing node
     * @param <X> underlying type
     * @return whether one is the (possibly distant) parent of the other
     */
    public static <X> boolean areRelated(GraphNode<X> node1, GraphNode<X> node2) {
        return node1.hierarchy.contains(node2) || node2.hierarchy.contains(node1);
    }

    /**
     * Defines an equivalence relation on nodes.
     * Example: consider the following structural tree (java source code format):
     *       1
     *      / \
     *     2   3
     *    / \   \
     *   4   5   6
     *   where the root is 1, its two children are 2 and 3, then 3 has a unique child, 6, and 2 has two children, 4 and 5.
     *   The set {1, 2, 3, 4, 5, 6} is partitioned into 5 sets: {4}, {5}, {3, 6}, {2}, {1}.
     *   Generally speaking, two nodes a and b are related iff they are inside the same branch of nodes with exactly 1
     *   child each OR they're the last node in the branch (ie, the first one to not have exactly 1 child node).
     *
     * @param node1 (GraphNode<X>)
     * @param node2 (GraphNode<X>)
     * @param <X> type of value stored inside the node
     * @return (boolean) boolean value of: node1 ~ node2
     */
    public static <X> boolean areRelatedRootless(GraphNode<X> node1, GraphNode<X> node2) {
        if (node1.equals(node2)) return true;
        if (!areRelated(node1, node2)) return false;

        final Path<GraphNode<X>> chosenHier = (node1.hierarchy.cardinality() > node2.hierarchy.cardinality()) ?
                node1.hierarchy : node2.hierarchy;
        return chosenHier.stream().takeWhile(node -> node.nmbrOfChildren <= 1)
                .collect(Collectors.toSet()).containsAll(Set.of(node1, node2));
    }

    private Deque<GraphNode<T>> hierarchyRecur(Deque<GraphNode<T>> nodeDeque) {
        if (nodeDeque.getLast().isRoot())
            return nodeDeque;
        else {
            nodeDeque.addLast(nodeDeque.getLast().parent);
            return hierarchyRecur(nodeDeque);
        }
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        return getValue().toString();
    }
}