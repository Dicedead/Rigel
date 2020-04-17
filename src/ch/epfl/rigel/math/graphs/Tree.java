package ch.epfl.rigel.math.graphs;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.sets.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
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
    public OrderedSet<Node<T>> flow(Function<Tree<T>, Node<T>> chooser, Node<T> point) {
        return null;
    }

    @Override
    public Optional<Iterable<Node<T>>> findPathBetween(Node<T> node1, Node<T> node2) {
        Preconditions.checkArgument(contains(node1) && contains(node2));

        final Path<Node<T>> nodeOneHierarchy = node1.hierarchy();
        final Path<Node<T>> nodeTwoHierarchy = node2.hierarchy();
        final var aut = nodeOneHierarchy.intersection(nodeTwoHierarchy);

        if (aut.contains(node1) || aut.contains(node2))
        {
            return nodeTwoHierarchy.findPathBetween(node1, node2);
        } else {

            final Node<T> anchor = aut.stream().findFirst().orElseThrow();
            return  Optional.of(new Path<>(nodeOneHierarchy.findPathBetween(node1, anchor).orElseThrow())
                    .add(new Path<>(nodeTwoHierarchy.findPathBetween(anchor, node2).orElseThrow()).reverse()));
        }
    }

    @Override
    public Graph<Node<T>, ? extends MathSet<Node<T>>> on(MathSet<Node<T>> points) {
        return new ConcreteGraph<>(new PartitionSet<>(intersection(points),
                        (a, b) -> points.containsSet(new OrderedSet<>(findPathBetween(a,b).orElseThrow()))),
                edgeSet().suchThat(points::containsSet),
                root);
    }

    @Override
    public Graph<Node<T>, Tree<T>> connectedComponent(Node<T> point) {
        return this;
    }

    @Override
    public MathSet<Graph<Node<T>, Tree<T>>> connectedComponents() {
        return new MathSet<>(Collections.singleton(this));
    }

    @Override
    public MathSet<Link<Node<T>>> edgeSet() {
        return null;
    }

    @Override
    public Tree<T> vertexSet() {
        return this;
    }


    public Node<T> getRoot() {
        return root;
    }
}


