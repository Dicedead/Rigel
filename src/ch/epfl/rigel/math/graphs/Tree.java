package ch.epfl.rigel.math.graphs;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.sets.MathSet;
import ch.epfl.rigel.math.sets.OrderedSet;
import ch.epfl.rigel.math.sets.PartitionSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Tree<V> extends PartitionSet<Node<V>> implements Graph<Node<V>, Tree<V>> {

    private final MathSet<Node<V>> leaves;
    private final Node<V> root;
    private final int maxDepth;

    public Tree(Collection<MathSet<Node<V>>> t) {
        this(unionOf(t));
    }

    public Tree(MathSet<Node<V>> nodes) {
        super(nodes, Node::areRelatedRootless);
        Preconditions.checkArgument(nodes.image(
                node -> { final Path<Node<V>> pathN = node.hierarchy();
                    return pathN.at(pathN.cardinality() - 1);})
                .cardinality() == 1);

        this.maxDepth = nodes.maxOf(Node::getDepth).getDepth();
        this.root = nodes.minOf(Node::getDepth);
        this.leaves = suchThat(node -> node.getDepth() == maxDepth);
    }

    @Override
    public Optional<Tree<V>> getNeighbours(Node<V> point) {
        return Optional.of(new Tree<>(component(point).minus(point.getParent())));
    }

    public Optional<Tree<V>> getChildren(Node<V> point) {
        final MathSet<Node<V>> children = getNodesAtDepth(point.getDepth() + 1).suchThat(point::isParentOf);
        return children.isEmpty() ? Optional.empty() : Optional.of(new Tree<>(children));
    }

    @Override
    public OrderedSet<Node<V>> flow(Function<Tree<V>, Node<V>> chooser, Node<V> point) {
        final List<Node<V>> flowList = flowRecur(chooser, chooser.apply(getChildren(point).get()), new ArrayList<>());
        flowList.add(point);
        Collections.reverse(flowList);
        return new OrderedSet<>(flowList);
    }

    private List<Node<V>> flowRecur(Function<Tree<V>, Node<V>> chooser, Node<V> point, List<Node<V>> workList) {
        if (getChildren(point).isEmpty()) {
            workList.add(point);
            return workList;
        }
        flowRecur(chooser, chooser.apply(getChildren(point).get()), workList);
        workList.add(point);
        return workList;
    }

    public Optional<Iterable<Node<V>>> findPathBetween(Node<V> node1, Node<V> node2) {
        Preconditions.checkArgument(contains(node1) && contains(node2));

        final Path<Node<V>> nodeOneHierarchy = node1.hierarchy();
        final Path<Node<V>> nodeTwoHierarchy = node2.hierarchy();
        final var aut = nodeOneHierarchy.intersection(nodeTwoHierarchy);

        if (aut.contains(node1) || aut.contains(node2))
        {
            return nodeTwoHierarchy.findPathBetween(node1, node2);
        } else {

            final Node<V> anchor = aut.minOf(Node::getDepth);
            return  Optional.of(new Path<>(nodeOneHierarchy.findPathBetween(node1, anchor).orElseThrow())
                    .add(new Path<>(nodeTwoHierarchy.findPathBetween(anchor, node2).orElseThrow()).reverse()));
        }
    }

    @Override
    public Graph<Node<V>, ? extends MathSet<Node<V>>> on(MathSet<Node<V>> points) {
        return new ConcreteGraph<>(new PartitionSet<>(intersection(points),
                (a, b) -> points.containsSet(new OrderedSet<>(findPathBetween(a,b).orElseThrow()))), edgeSet().suchThat(points::containsSet));
    }

    @Override
    public Graph<Node<V>, Tree<V>> connectedComponent(Node<V> point) {
        return this;
    }

    @Override
    public MathSet<Graph<Node<V>, Tree<V>>> connectedComponents() {
        return MathSet.of(this);
    }

    @Override
    public MathSet<Link<Node<V>>> edgeSet() {
        return image(n -> new Link<>(n.getParent(), n));
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

    public MathSet<Node<V>> getNodesAtDepth(int targetDepth) {
        return suchThat(node -> node.getDepth() == targetDepth);
    }

    public Node<V> getRoot() {
        return root;
    }

    public int getTotalDepth() {
        return maxDepth - root.getDepth();
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public int getMinDepth() {
        return root.getDepth();
    }
}
