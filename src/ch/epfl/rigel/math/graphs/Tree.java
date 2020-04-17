package ch.epfl.rigel.math.graphs;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.sets.MathSet;
import ch.epfl.rigel.math.sets.OrderedSet;
import ch.epfl.rigel.math.sets.PartitionSet;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;

public final class Tree<V> extends PartitionSet<Node<V>> implements Graph<Node<V>, Tree<V>> {

    private final MathSet<Node<V>> leaves;
    private final Node<V> root;
    private final int depth;

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
    public OrderedSet<Node<V>> flow(Function<Tree<V>, Node<V>> chooser, Node<V> point) {
        return null;
    }

    @Override
    public Optional<Iterable<Node<V>>> findPathBetween(Node<V> v1, Node<V> v2) {
        return Optional.empty();
    }

    @Override
    public Graph<Node<V>, ? extends MathSet<Node<V>>> on(MathSet<Node<V>> points) {
        return null;
    }

    @Override
    public Graph<Node<V>, Tree<V>> connectedComponent(Node<V> point) {
        return null;
    }

    @Override
    public MathSet<Graph<Node<V>, Tree<V>>> connectedComponents() {
        return null;
    }

    @Override
    public MathSet<Link<Node<V>>> edgeSet() {
        return null;
    }

    @Override
    public Tree<V> vertexSet() {
        return null;
    }

    /**
     * @return the points that have a parent but no children
     */
    public MathSet<Node<V>> getLeaves() {
        return leaves;
    }

    @Override
    public Optional<Tree<V>> getNeighbors(Node<V> point) {
        return Optional.empty();
    }

    public MathSet<Node<V>> getNodesAtDepth(int targetDepth) {
        return suchThat(node -> node.getDepth() == targetDepth);
    }

    public int getDepth() { return depth; }
}
