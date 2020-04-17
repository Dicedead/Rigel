package ch.epfl.rigel.math.graphs;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.sets.IndexedSet;
import ch.epfl.rigel.math.sets.MathSet;
import ch.epfl.rigel.math.sets.OrderedSet;
import ch.epfl.rigel.math.sets.PartitionSet;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;

public final class Tree<V> extends PartitionSet<Node<V>> implements Graph<Node<V>, Tree<V>> {

    private final MathSet<Node<V>> leaves;
    private final Node<V> root;
    private final int depth;

    public Tree(Collection<MathSet<Node<V>>> t) {
        this(unionOf(t));
    }

    public Tree(MathSet<Node<V>> nodes) {
        super(nodes, Node::areRelated);
        Preconditions.checkArgument(nodes.stream().map(n -> {
            Path<Node<V>> pathN = n.hierarchy();
            return pathN.at(pathN.size());})
                .distinct().count() == 1);

        this.root = suchThat(Node::isRoot).stream().findFirst().orElseThrow();
        this.depth = nodes.stream().max(Comparator.comparingInt(Node::getDepth)).get().getDepth();
        this.leaves = suchThat(node -> node.getDepth() == depth);
    }

    @Override
    public Optional<Tree<V>> getNeighbors(Node<V> point) {
        return Optional.of(new Tree<>(component(point).minus(point.getParent())));
    }

    @Override
    public OrderedSet<Node<V>> flow(Function<Tree<V>, Node<V>> chooser, Node<V> point) {
        return null;
    }

    @Override
    public Optional<Iterable<Node<V>>> findPathBetween(Node<V> node1, Node<V> node2) {
        Preconditions.checkArgument(contains(node1) && contains(node2));

        final Path<Node<V>> nodeOneHierarchy = node1.hierarchy();
        final Path<Node<V>> nodeTwoHierarchy = node2.hierarchy();
        final var aut = nodeOneHierarchy.intersection(nodeTwoHierarchy);

        if (aut.contains(node1) || aut.contains(node2))
        {
            return nodeTwoHierarchy.findPathBetween(node1, node2);
        } else {

            final Node<V> anchor = aut.stream().findFirst().orElseThrow();
            return  Optional.of(new Path<>(nodeOneHierarchy.findPathBetween(node1, anchor).orElseThrow())
                    .add(new Path<>(nodeTwoHierarchy.findPathBetween(anchor, node2).orElseThrow()).reverse()));
        }
    }

    @Override
    public Graph<Node<V>, ? extends MathSet<Node<V>>> on(MathSet<Node<V>> points) {
        return new ConcreteGraph<>(new PartitionSet<>(intersection(points),
                (a, b) -> points.containsSet(new OrderedSet<>(findPathBetween(a,b).orElseThrow()))),
                edgeSet().suchThat(points::containsSet),
                root);
    }

    @Override
    public Graph<Node<V>, Tree<V>> connectedComponent(Node<V> point) {
        return this;
    }

    @Override
    public MathSet<Graph<Node<V>, Tree<V>>> connectedComponents() {
        return new MathSet<>(Collections.singleton(this));
    }

    @Override
    public MathSet<Link<Node<V>>> edgeSet() {
        return null;
    }

    @Override
    public Tree<V> vertexSet() {
        return this;
    }

    /**
     * @return the points that have a parent but no children
     */
    public MathSet<Node<V>> getLeaves() {
        return leaves;
    }
}
