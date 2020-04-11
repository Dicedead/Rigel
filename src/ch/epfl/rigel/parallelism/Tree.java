package ch.epfl.rigel.parallelism;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Tree<T> {

    private final Node<T> root;

    public Tree(Node<T> root) {
        this.root = root;
    }

    public boolean contains(Node<T> other)
    {
        return this.root.getGraph().contains(other);
    }

    public boolean contains(Tree<T> other)
    {
        return this.root.getGraph().containsAll(other.root.getGraph());
    }

    public Tree(T root, List<List<List<T>>> levels) {
        this.root = new Node<T>(root);
        recursiveInit(levels, List.of(this.root), 1);

    }

    private List<Node<T>> recursiveInit(final List<List<List<T>>> childs, final List<Node<T>> parents, final int n)
    {
        if (n == childs.size() )
            return parents;
        IntStream.of(0, parents.size()).forEachOrdered(i -> childs.get(n).get(i).forEach(parents.get(i)::link));
        return recursiveInit(childs, parents.stream().map(Node::getChildren).flatMap(List::stream).collect(Collectors.toList()),1+n);
    }

    public Node<T> getRoot() {
        return root;
    }

    public List<Node<T>> getNeighbors(final Node<T> node)
    {
        return node.getParents().getChildren();
    }

    protected List<Node<T>> recursList(final Function<List<Node<T>>, Node<T>> chooser)
    {
        final Node<T> selected = chooser.apply(root.getChildren());

        if (selected.isLeaf())
            return List.of(selected);

        List<Node<T>> prev = new Tree<T>(selected).recursList(chooser);
        prev.add(selected);
        return prev;
    }

    public Path<T> getPath(final Function<List<Node<T>>, Node<T>> chooser)
    {
        return new Path<T>( recursList(chooser), root);
    }

    public Stream<Node<T>> flow(final Function<List<Node<T>>, Node<T>> chooser)
    {
        return recursList(chooser).stream();
    }

    public static class Node<T> {

        final private T t;
        final private Node<T> parent;
        private final List<Node<T>> children;
        private final Set<Node<T>> graph;

        public Set<Node<T>> getGraph() {
            return graph;
        }

        public void link(final List<T> v)
        {
            v.forEach(value -> children.add(new Node<T>(value, this)));
        }



        public Node<T> link(final T value)
        {
            var a = new Node<T>(value, this);
            children.add(a);
            return a;
        }

        public Node(final T t)
        {
            this.t = t;
            this.parent = null;
            this.children = new ArrayList<>();
            graph = new HashSet<>();
            graph.add(this);
        }

        public Node(T t, Node<T> parents)
        {
            this.t = t;
            this.parent = parents;
            this.children = new ArrayList<>();

            parent.getGraph().add(this);
            graph = parent.getGraph();
        }

        public boolean isLeaf(){return children.isEmpty();}

        public T getValue(){return t;}

        public Node<T> getParents() {return parent;}

        public List<Node<T>> getChildren() {
            return children;
        }

        @Override
        public final boolean equals(Object o) {
            if (o instanceof Node)
                return ((Node) o).getChildren().equals(getChildren()) && ((Node) o).getParents() == getParents();
            else return false;
        }

    }

}
