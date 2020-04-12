package ch.epfl.rigel.math.graphs;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public final class Tree<T> extends AbstractGraph<Node<T>, DirectedLink<Node<T>>> {

    private final Node<T> root;

    public Tree(Collection<Node<T>> leaves)
    {
        super(leaves.stream().map(Node::hierarchy).collect(Collectors.toList()));
        root = super.getPoint().hierarchy().tail();
    }
    public Tree(Set<Node<T>> childs, Set<DirectedLink<Node<T>>> collect) {
        super(childs, collect);
        root = super.getPoint().hierarchy().tail();

    }

    @Override
    public AbstractGraph<Node<T>, DirectedLink<Node<T>>> on(Set<Node<T>> points) {
        return new Tree<>(points);

    }

    @Override
    public Node<T> getPoint() {
        return root;
    }

    @Override
    public AbstractGraph<Node<T>, DirectedLink<Node<T>>> component(Node<T> point) {
        return this;
    }

    @Override
    public Path<Node<T>> isConnectedTo(Node<T> tNode, Node<T> u) {
        try {
            return tNode.hierarchy().getO(u);
        } catch (Exception e) {
            return u.hierarchy().getO(tNode);
        }
    }

}
