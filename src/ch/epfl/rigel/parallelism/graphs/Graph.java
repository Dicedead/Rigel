package ch.epfl.rigel.parallelism.graphs;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
abstract class Graph<T> {

    private final Node<T> root;

    public Graph(Node<T> root) {
        this.root = root;
    }

    public Graph(T root, List<List<List<T>>> levels) {
        this.root = new Node<T>(root);
        recursiveInit(levels, List.of(this.root), 1);
    }

    private List<Node<T>> recursiveInit(final List<List<List<T>>> childs, final List<Node<T>> parents, final int n)
    {
        if (n == childs.size() )
            return parents;
        IntStream.range(0, parents.size()).forEachOrdered(i -> childs.get(n).get(i).forEach(parents.get(i)::link));
        return recursiveInit(childs, parents.stream().map(Node::getChildren).flatMap(List::stream).collect(Collectors.toList()),1+n);
    }

    public Node<T> getRoot() {
        return root;
    }

    public List<Node<T>> getNeighbors(final Node<T> node)
    {
        return node.getParents().getChildren();
    }

    protected static <T> List<Node<T>> recursList(final Function<List<Node<T>>, Node<T>> chooser, Node<T> root)
    {
        final Node<T> selected = chooser.apply(root.getChildren());

        if (selected.isLeaf())
            return List.of(selected);

        List<Node<T>> prev = recursList(chooser, selected);
        prev.add(selected);
        return prev;
    }

    public Stream<Node<T>> flow(final Function<List<Node<T>>, Node<T>> chooser)
    {
        return recursList(chooser, getRoot()).stream();
    }

    /*protected List<Node<T>> recursList(final Function<List<Node<T>>, Node<T>> chooser)
    {
        final Node<T> selected = chooser.apply(root.getChildren());

        if (selected.isLeaf())
            return List.of(selected);

        List<Node<T>> prev = new Graph(selected).recursList(chooser);
        prev.add(selected);
        return prev;
    }*/
}
