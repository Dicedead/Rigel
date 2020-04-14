package ch.epfl.rigel.math.graphs;

import ch.epfl.rigel.Preconditions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Representation of a Tree as an implementation of a directed graph with nodes and directed links
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Tree<T> extends Graph<Node<T>, DirectedLink<Node<T>>> {

    private final Node<T> root;
    private final Set<Node<T>> leaves;

    /**
     * Main Constructor of Tree using parameter Nodes' inner hierarchy to construct the directed graph
     * Supposes a connected tree AKA not a forest
     * @param leaves (Collection<Node<T>>)
     */
    public Tree(Collection<Node<T>> leaves) {
        super(leaves.stream().map(Node::hierarchy).collect(Collectors.toCollection(ArrayList::new)));
        Preconditions.checkArgument(leaves.stream().map(n -> n.hierarchy().tail()).distinct().count() == 1);

        root = super.getPoint().hierarchy().tail();
        this.leaves = Set.copyOf(leaves);
    }


    /**
     * Alternate Tree Constructor: set of vertices + set of directed links
     *
     * @param childs (Set<Node<T>>) set of vertices, nodes for trees
     * @param collect (Set<DirectedLink<Node<T>>>) set of directed edges
     */
    public Tree(Set<Node<T>> childs, Set<DirectedLink<Node<T>>> collect) {
        super(childs, collect);
        root = super.getPoint().hierarchy().tail();
        leaves = childs;

    }

    /**
     * Constructs a Tree ON given set of points, returns it as supertype
     *
     * @param points (Set<T>) said set
     * @return (Graph<Node<T>, DirectedLink<Node<T>>>) implemented as Tree
     */
    @Override
    public Graph<Node<T>, DirectedLink<Node<T>>> on(Set<Node<T>> points) {
        return new Tree<>(points);

    }
    /**
     * @return (Node<T>) gets the first object of the tree
     */
    @Override
    public Node<T> getPoint() {
        return getRoot();
    }

    /**
     * @return (Node<T>) gets the first object of the tree
     */
    public Node<T> getRoot() {
        return root;
    }

    /**
     * @return the points that have a parent but no children
     */
    public Set<Node<T>> getLeaves() {
        return leaves;
    }

    /**
     * The component in which a point lies, in case of a tree, the tree itself
     *
     * @param point (Node<T>) the point to test
     * @return (Graph<Node<T>, DirectedLink<Node<T>>>) its connected component
     */
    @Override
    public Graph<Node<T>, DirectedLink<Node<T>>> component(Node<T> point) {
        return this;
    }

    /**
     * Finds the shortest path between two nodes
     *
     * @param node1 (Node<T>) from where the path should start
     * @param node2 (Node<T>) where it should end
     * @return (Path<Node<T>>) the shortest path in the graph from point a to b
     * @throws IllegalArgumentException if the two nodes are not in the same hierarchical branch.
     */
    @Override
    public Path<Node<T>> findPathBetween(Node<T> node1, Node<T> node2) {
        Preconditions.checkArgument(getPointSet().contains(node1) && getPointSet().contains(node2));

        final Path<Node<T>> nodeOneHierarchy = node1.hierarchy();
        final Path<Node<T>> nodeTwoHierarchy = node2.hierarchy();
        final var aut = nodeOneHierarchy.intersection(nodeTwoHierarchy);

        if (aut.contains(node1) || aut.contains(node2))
        {
            if (nodeTwoHierarchy.contains(node1)) {
                return nodeTwoHierarchy.subpathTo(node1);
            }

            return nodeOneHierarchy.subpathTo(node2);

        } else {

            final Node<T> anchor = aut.getPoint();
            return  nodeOneHierarchy.subpathTo(anchor).add(nodeTwoHierarchy.subpathTo(anchor).inverse());
        }
    }

    /**
     * @see Graph#intersection(Graph)
     */
    @Override
    public Graph<Node<T>, DirectedLink<Node<T>>> intersection(Graph<Node<T>, DirectedLink<Node<T>>> otherGraph) {
        final Set<Node<T>> interNodes = otherGraph.getPointSet();
        interNodes.retainAll(this.getPointSet());
        final Set<DirectedLink<Node<T>>> interLinks = otherGraph.getEdgeSet();
        return new Tree<T>(interNodes, interLinks);
    }
}
