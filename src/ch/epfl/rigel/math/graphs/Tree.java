package ch.epfl.rigel.math.graphs;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.sets.abstraction.AbstractMathSet;
import ch.epfl.rigel.math.sets.abstraction.AbstractOrderedTuple;
import ch.epfl.rigel.math.sets.concrete.MathSet;
import ch.epfl.rigel.math.sets.concrete.OrderedTuple;
import ch.epfl.rigel.math.sets.concrete.PartitionSet;
import ch.epfl.rigel.math.sets.abstraction.SetFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Tree<V> extends PartitionSet<GraphNode<V>> implements Graph<GraphNode<V>, Tree<V>> {

    private final AbstractMathSet<GraphNode<V>> nodes;
    private final GraphNode<V> root;
    private final int maxDepth;

    /**
     * Constructor of Tree using parameter Nodes' inner hierarchy to construct the directed graph
     * Supposes a connected tree AKA not a forest
     *
     * @param t (Collection<Node<T>>)
     * @throws IllegalArgumentException if given leaves aren't all connected to the same root
     */
    public Tree(Collection<AbstractMathSet<GraphNode<V>>> t) {
        this(AbstractMathSet.unionOf(t));
    }

    /**
     * Main Constructor of Tree using parameter Nodes' inner hierarchy to construct the directed graph
     * Supposes a connected tree AKA not a forest
     *
     * @param nodes (Collection<Node<T>>)
     * @throws IllegalArgumentException if given leaves aren't all connected to the same root
     */
    public Tree(AbstractMathSet<GraphNode<V>> nodes) {
        this(nodes, !nodes.isEmpty(), (nodes.isEmpty()) ? new GraphNode<>(null) : nodes.minOf(GraphNode::getDepth),
                (nodes.isEmpty()) ? -1 : nodes.maxOf(GraphNode::getDepth).getDepth());
    }

    private Tree(AbstractMathSet<GraphNode<V>> nodes, boolean securityChecksActivated, GraphNode<V> root, int maxDepth) {
        super(nodes, GraphNode::areRelatedRootless);

        if (securityChecksActivated) {
            Preconditions.checkArgument(nodes.image(
                    node -> {
                        final Path<GraphNode<V>> pathN = node.hierarchy();
                        return pathN.at(pathN.cardinality() - 1);
                    })
                    .cardinality() == 1);
        }

        this.maxDepth = maxDepth;
        this.root = root;
        this.nodes = nodes;
    }

    @Override
    public Optional<Tree<V>> getNeighbours(GraphNode<V> point) {
        return getChildren(point.getParent().orElseThrow());
    }

    /**
     *
     * @param point (T)
     * @return The points for which point is their parent
     */
    public Optional<Tree<V>> getChildren(GraphNode<V> point) {
        final AbstractMathSet<GraphNode<V>> children = getNodesAtDepth(point.getDepth() + 1).suchThat(point::isParentOf);
        return children.isEmpty() ? Optional.empty() : Optional.of(new Tree<>(children, false, point, maxDepth));
    }

    @Override
    public OrderedTuple<GraphNode<V>> flow(SetFunction<Tree<V>, GraphNode<V>> chooser, GraphNode<V> point) {
        final List<GraphNode<V>> flowList = flowRecur(chooser, chooser.apply(getChildren(point).orElse(null)), new ArrayList<>());
        flowList.add(point);
        Collections.reverse(flowList);
        return new OrderedTuple<>(flowList);
    }

    private List<GraphNode<V>> flowRecur(Function<Tree<V>, GraphNode<V>> chooser, GraphNode<V> point, List<GraphNode<V>> workList) {
        if (getChildren(point).isEmpty()) {
            workList.add(point);
            return workList;
        }
        flowRecur(chooser, chooser.apply(getChildren(point).get()), workList);
        workList.add(point);
        return workList;
    }

    /**
     *
     * @param point (T)
     * @return the tree that has for root point and points the same as the current tree
     */
    public Tree<V> subtreeAtPoint(final GraphNode<V> point) {
        Preconditions.checkArgument(contains(point));
        return new Tree<>(nodes.suchThat(node -> node.getDepth() >= point.getDepth() && GraphNode.areRelated(node, point)),
                false, point, maxDepth);
    }
    /**
     * Finds the shortest path between two nodes
     *
     * @param node1 (GraphNode<V>) from where the path should start
     * @param node2 (GraphNode<V>) where it should end
     * @return (Path<GraphNode<V>>) the shortest path in the graph from point a to b
     * @throws IllegalArgumentException if either of the two nodes is not in the tree
     */
    public Optional<AbstractOrderedTuple<GraphNode<V>>> findPathBetween(GraphNode<V> node1, GraphNode<V> node2) {
        Preconditions.checkArgument(contains(node1) && contains(node2));

        final Path<GraphNode<V>> nodeOneHierarchy = node1.hierarchy();
        final Path<GraphNode<V>> nodeTwoHierarchy = node2.hierarchy();
        final AbstractMathSet<GraphNode<V>> aut = nodeOneHierarchy.intersection(nodeTwoHierarchy);

        if (aut.contains(node1) || aut.contains(node2))
        {
            return nodeTwoHierarchy.findPathBetween(node1, node2);
        } else {

            final GraphNode<V> anchor = aut.maxOf(GraphNode::getDepth);
            return  Optional.of(new Path<>(nodeOneHierarchy.findPathBetween(node1, anchor).orElseThrow())
                    .add(new Path<>(nodeTwoHierarchy.findPathBetween(anchor, node2).orElseThrow()).reverse()));
        }
    }

    @Override
    public Graph<GraphNode<V>, ? extends AbstractMathSet<GraphNode<V>>> on(AbstractMathSet<GraphNode<V>> points) {
        return new ConcreteGraph<>(new PartitionSet<>(intersection(points),
                (a, b) -> points.containsSet(new OrderedTuple<>(findPathBetween(a,b).orElseThrow()))),
                edgeSet().suchThat(points::containsSet));
    }

    @Override
    public Graph<GraphNode<V>, Tree<V>> connectedComponent(GraphNode<V> point) {
        return this;
    }

    @Override
    public AbstractMathSet<Graph<GraphNode<V>, Tree<V>>> connectedComponents() {
        return new MathSet<>(Collections.singleton(this));
    }

    @Override
    public AbstractMathSet<Link<GraphNode<V>>> edgeSet() {
        final AbstractMathSet<GraphNode<V>> nonRoots = nodes.suchThat(node -> !node.equals(root));
        return (nonRoots.cardinality() <= 1) ? emptySet() : nonRoots.image(n -> new Link<>(n, n.getParent().orElse(null)));
    }

    @Override
    public Tree<V> vertexSet() {
        return this;
    }

    /**
     * @return the points that have a parent but no children - assumes that, for each node, all its children are in the
     *         tree (ie assuming that this tree contains all nodes alpha verifying Node.areRelated(this.root, alpha)).
     */
    public AbstractMathSet<GraphNode<V>> getLeaves() {
        return nodes.suchThat(node -> !node.isParent());
    }

    /**
     * @param targetDepth the wanted depth of nodes
     * @return all nodes sharing this depth
     */
    public AbstractMathSet<GraphNode<V>> getNodesAtDepth(int targetDepth) {
        return suchThat(node -> node.getDepth() == targetDepth);
    }

    /**
     * @return the root node to which every node should be linked
     */
    public GraphNode<V> getRoot() {
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
