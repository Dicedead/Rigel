package ch.epfl.rigel.math.graphs;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.sets.*;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Tree<T> extends PartitionSet<Node<T>> implements Graph<Node<T>, Tree<T>> {

    private final Node<T> root;

    public Tree(Collection<MathSet<Node<T>>> t, Node<T> root) {
        super(t);
        this.root = root;
    }

    public Tree(IndexedSet<MathSet<Node<T>>, Node<T>> t, Node<T> root) {
        super(t);
        this.root = root;
    }

    public Tree(MathSet<Node<T>> t) {
        super(t, (a , b) -> a.getParent().equals(b.getParent()) || a.getParent().equals(b) || b.getParent().equals(a));
        root = suchThat(Node::isRoot).stream().findFirst().orElseThrow();
    }

    @Override
    public Optional<Tree<T>> getNeighbors(Node<T> point) {
        return Optional.of(new Tree<T>(component(point).minus(point.getParent())));
    }

    @Override
    public OrderedPair<Node<T>> flow(Function<Tree<T>, Node<T>> chooser, Node<T> point) {
        return null;
    }

    @Override
    public Optional<Iterable<Node<T>>> findPathBetween(Node<T> node1, Node<T> node2) {

        return Optional.empty();
    }

    @Override
    public Graph<Node<T>, ? extends MathSet<Node<T>>> on(MathSet<Node<T>> points) {
        return null;
    }

    @Override
    public Graph<Node<T>, Tree<T>> connectedComponent(Node<T> point) {
        return null;
    }

    @Override
    public MathSet<Graph<Node<T>, Tree<T>>> connectedComponents() {
        return null;
    }

    @Override
    public MathSet<Link<Node<T>>> edgeSet() {
        return null;
    }

    @Override
    public Tree<T> vertexSet() {
        return null;
    }

    public Node<T> getRoot() {
        return root;
    }
}


