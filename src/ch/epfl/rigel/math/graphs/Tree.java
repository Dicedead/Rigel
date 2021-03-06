package ch.epfl.rigel.math.graphs;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.sets.abstraction.AbstractMathSet;
import ch.epfl.rigel.math.sets.abstraction.AbstractOrderedTuple;
import ch.epfl.rigel.math.sets.implement.MathSet;
import ch.epfl.rigel.math.sets.implement.OrderedTuple;
import ch.epfl.rigel.math.sets.implement.PartitionSet;
import ch.epfl.rigel.math.sets.properties.SetFunction;
import ch.epfl.rigel.math.sets.abstraction.PointedSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Directed and connected recursive definition of a tree based on GraphNodes
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Tree<V> extends PointedSet<GraphNode<V>> implements Graph<GraphNode<V>, Tree<V>> {

    private final AbstractMathSet<GraphNode<V>> nodes;
    private final int maxDepth;

    /**
     * Constructor of Tree using parameter Nodes' inner hierarchy to construct the directed graph
     * Creates a connected tree aka not a forest (one and unique root node)
     *
     * @param t (Collection<Node<T>>)
     * @throws IllegalArgumentException if given leaves aren't all connected to the same root
     */
    public Tree(Collection<AbstractMathSet<GraphNode<V>>> t) {
        this(AbstractMathSet.unionOf(t));
    }

    /**
     * Main Constructor of Tree using parameter Nodes' inner hierarchy to construct the directed graph
     * Creates a connected tree aka not a forest (one and unique root node)
     *
     * @param nodes (Collection<Node<T>>)
     * @throws IllegalArgumentException if given leaves aren't all connected to the same root
     */
    public Tree(AbstractMathSet<GraphNode<V>> nodes) {
        this(nodes, true);
    }

    /**
     * Dangerous but efficient constructor not performing security checks if user programmer decides so.
     * This class will NOT function properly with an invalid tree, ie one that does not have exactly one root node at
     * the top of every other node's hierarchy.
     *
     * @param nodes (AbstractMathSet<GraphNode<V>>)
     * @param securityChecksActivated (boolean)
     */
    public Tree(AbstractMathSet<GraphNode<V>> nodes, boolean securityChecksActivated) {
        this(nodes, nodes.minOf(GraphNode::getDepth), securityChecksActivated);
    }

    /**
     * @see Tree#Tree(AbstractMathSet, boolean)
     * Same as the constructor mentionned above along with root specification
     *
     * @param nodes (AbstractMathSet<GraphNode<V>>)
     * @param root (GraphNode<V>)
     * @param securityChecksActivated (boolean)
     */
    public Tree(AbstractMathSet<GraphNode<V>> nodes, GraphNode<V> root, boolean securityChecksActivated) {
        this(nodes, securityChecksActivated, root, nodes.maxOf(GraphNode::getDepth).getDepth());
    }

    private Tree() {
        super(emptySet(), null);
        this.maxDepth = -1;
        this.nodes = emptySet();
    }

    private Tree(AbstractMathSet<GraphNode<V>> nodes, boolean securityChecksActivated, GraphNode<V> root, int maxDepth) {
        super(nodes, root);
        if (securityChecksActivated) {
            Preconditions.checkArgument(nodes.image(
                    node -> {
                        final Path<GraphNode<V>> pathN = node.hierarchy();
                        return pathN.at(pathN.cardinality() - 1);
                    }).cardinality() == 1, "Tree: Given set of nodes does not have a common root.");
        }

        this.maxDepth = maxDepth;
        this.nodes = nodes;
    }

    /**
     * Factory constructor of an empty tree of type V
     *
     * @param <V> type parameter
     * @return (Tree<V>)
     */
    public static <V> Tree<V> emptyTree()
    {
        return new Tree<>();
    }

    /**
     * Add a Path of nodes into this tree via set union
     *
     * @param p (Path<GraphNode<V>>) path to be added
     * @return (Tree<V>) union of this tree and given path
     */
    public Tree<V> add(Path<GraphNode<V>> p)
    {
        return new Tree<>(union(p));
    }

    /**
     * Get the given node and it's children
     *
     * @param point (T) given point
     * @return (Optional<Tree<V>>) tree of given point and its children
     */
    @Override
    public Optional<Tree<V>> getNeighbours(GraphNode<V> point) {
        return getChildren(point.getParent().orElseThrow());
    }

    /**
     * Get the given node's children
     *
     * @param point (T)
     * @return (Optional<Tree<V>>) The points for which point is their direct parent
     */
    public Optional<Tree<V>> getChildren(GraphNode<V> point) {
        AbstractMathSet<GraphNode<V>> children = getNodesAtDepth(point.getDepth() + 1).suchThat(point::isParentOf);
        return children.isEmpty() ? Optional.empty() : Optional.of(new Tree<>(children, false, point, maxDepth));
    }

    /**
     * @see Graph#flow(SetFunction, Object) 
     */
    @Override
    public OrderedTuple<GraphNode<V>> flow(SetFunction<Tree<V>, GraphNode<V>> chooser, GraphNode<V> point) {
        List<GraphNode<V>> flowList = flowRecur(chooser, chooser.apply(getChildren(point).orElse(null)), new ArrayList<>());
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
     * @param point (T)
     * @return the tree that has for root point and points the same as the current tree
     */
    public Tree<V> subtreeAtPoint(GraphNode<V> point) {
        Preconditions.checkArgument(contains(point));
        return new Tree<>(nodes.suchThat(node -> node.getDepth() >= point.getDepth() && GraphNode.areRelated(node, point)),
                false, point, maxDepth);
    }

    /**
     * Return the branch given point is in
     *
     * @param point (GraphNode<V>) given point
     * @return (Optional<AbstractOrderedTuple<GraphNode<V>>>) a list (in the sense of this custom made lib)
     */
    public Optional<AbstractOrderedTuple<GraphNode<V>>> branchAtPoint(GraphNode<V> point)
    {
        Preconditions.checkArgument(contains(point));
        var top = point
                .hierarchy()
                .getElement(n -> getNodesAtDepth(n.getDepth() + 1)
                        .suchThat(n::isParentOf)
                        .cardinality() == 1);

        return findPathBetween(point, top.orElse(point));
    }
    /**
     * Finds the shortest path between two nodes
     *
     * @param node1 (GraphNode<V>) from where the path should start
     * @param node2 (GraphNode<V>) where it should end
     * @return (Path<GraphNode<V>>) the shortest path in the graph from point a to b
     * @throws IllegalArgumentException if either of the two nodes is not in the tree as two points on a tree are always linked
     */
    public Optional<AbstractOrderedTuple<GraphNode<V>>> findPathBetween(GraphNode<V> node1, GraphNode<V> node2) {
        Preconditions.checkArgument(contains(node1) && contains(node2));

        Path<GraphNode<V>> nodeOneHierarchy = node1.hierarchy();
        Path<GraphNode<V>> nodeTwoHierarchy = node2.hierarchy();
        AbstractMathSet<GraphNode<V>> aut   = nodeOneHierarchy.intersection(nodeTwoHierarchy);

        if (aut.contains(node1) || aut.contains(node2))
        {
            return nodeTwoHierarchy.findPathBetween(node1, node2);
        } else {

            final GraphNode<V> anchor = aut.maxOf(GraphNode::getDepth);
            return  Optional.of(new Path<>(nodeOneHierarchy.findPathBetween(node1, anchor).orElseThrow())
                    .add(new Path<>(nodeTwoHierarchy.findPathBetween(anchor, node2).orElseThrow()).reverse()));
        }
    }

    /**
     * @see Graph#on(AbstractMathSet)
     */
    @Override
    public Graph<GraphNode<V>, ? extends AbstractMathSet<GraphNode<V>>> on(AbstractMathSet<GraphNode<V>> points) {
        return new ConcreteGraph<>(new PartitionSet<>(intersection(points),
                (a, b) -> points.containsSet(new OrderedTuple<>(findPathBetween(a,b).orElseThrow()))),
                edgeSet().suchThat(points::containsSet));
    }

    /**
     * By definition of this class, all components are connected. This method will return this.
     *
     * @param point (GraphNode<V> point) might as well be null
     * @return this
     */
    @Override
    public Graph<GraphNode<V>, Tree<V>> connectedComponent(GraphNode<V> point) {
        return this;
    }

    /**
     * By definition of this class, all components are connected. This method will return a singleton of this.
     *
     * @return this
     */
    @Override
    public AbstractMathSet<Graph<GraphNode<V>, Tree<V>>> connectedComponents() {
        return new MathSet<>(Collections.singleton(this));
    }

    /**
     * Computes the partition defined in GraphNode.areRelatedRootless
     * @see GraphNode#areRelatedRootless(GraphNode, GraphNode)
     *
     * @return (PartitionSet<GraphNode<V>>)
     */
    public PartitionSet<GraphNode<V>> partition() {
        return new PartitionSet<>(new MathSet<>(getRawData()), GraphNode::areRelatedRootless);
    }

    /**
     * @see Graph#edgeSet()
     */
    @Override
    public AbstractMathSet<Link<GraphNode<V>>> edgeSet() {
        final AbstractMathSet<GraphNode<V>> nonRoots = nodes.suchThat(node -> !node.equals(getElementOrThrow()));
        return (nonRoots.cardinality() <= 1) ? emptySet() : nonRoots.image(n -> new Link<>(n, n.getParent().orElse(null)));
    }

    /**
     * @see Graph#vertexSet()
     * By recursive definition of a tree, the set of vertices is... itself!
     */
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
        return getElementOrThrow();
    }

    /**
     * Depth of this tree, ie depth of its root - depth of its deepest children
     *
     * @return (int)
     */
    public int getTotalDepth() {
        return getMaxDepth() - getMinDepth();
    }

    /**
     * Depth of (one of) the deepest nodes in its node hierarchy
     *
     * @return (int) said depth
     */
    public int getMaxDepth() {
        return maxDepth;
    }

    /**
     * Depth of the root of this tree in its node hierarchy
     *
     * @return (int)
     */
    public int getMinDepth() {
        return getRoot().getDepth();
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return "Tree: "+ partition().components().toString();
    }
}
