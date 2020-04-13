package ch.epfl.rigel.math.graphs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Representation of a Tree as an implementation of a directed graph with nodes and directed links
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Tree<T> extends AbstractGraph<Node<T>, DirectedLink<Node<T>>> {

    private final Node<T> root;
    private final Set<Node<T>> leaves;

    /**
     * Main Constructor of Tree using parameter Nodes' inner hierarchy to construct the directed graph
     *
     * @param leaves (Collection<Node<T>>)
     */
    public Tree(Collection<Node<T>> leaves) {
        super(leaves.stream().map(Node::hierarchy).collect(Collectors.toCollection(ArrayList::new)));
        assert (leaves.stream().map(n -> n.hierarchy().tail()).distinct().count() == 1);

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
     * @return (AbstractGraph<Node<T>, DirectedLink<Node<T>>>) implemented as Tree
     */
    @Override
    public AbstractGraph<Node<T>, DirectedLink<Node<T>>> on(Set<Node<T>> points) {
        return new Tree<>(points);

    }

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

    //TODO: doc
    @Override
    public AbstractGraph<Node<T>, DirectedLink<Node<T>>> component(Node<T> point) {
        return this;
    }

    //TODO: doc
    @Override
    public Path<Node<T>> isConnectedTo(Node<T> node1, Node<T> node2) {
        try {
            return node1.hierarchy().subpathTo(node2);
        } catch (Exception e) {
            return node2.hierarchy().subpathTo(node1);
        }
    }

    public Set<Node<T>> getLeaves() {
        return leaves;
    }
}
