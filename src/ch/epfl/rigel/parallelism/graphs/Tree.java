package ch.epfl.rigel.parallelism.graphs;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public final class Tree<T> extends Graph<T> {

    public Tree(Node<T> root) {
        super(root);
    }

    public Tree(T root, List<List<List<T>>> levels) {
        super(root, levels);
    }

    public boolean contains(Node<T> other)
    {
        return this.getRoot().getGraph().contains(other);
    }

    public boolean contains(Tree<T> other)
    {
        return this.getRoot().getGraph().containsAll(other.getRoot().getGraph());
    }

    public Path<T> getPath(final Function<List<Node<T>>, Node<T>> chooser)
    {
        return new Path<T>(recursList(chooser, getRoot()), getRoot());
    }

    public static class Path<E> extends Graph<E> {

        private final Function<List<Node<E>>, Node<E>> chooser = l -> l.get(0);
        private final List<Node<E>> path;

        public Path(List<E> points)
        {
            super(new Node<E>(points.get(0)));
            link(points, 1, getRoot());
            path = recursList(chooser, getRoot());
        }

        public Path(List<Node<E>> path, Node<E> root)
        {
            super(root);
            this.path = path;
        }

        public Node<E> head()
        {
            return getRoot();
        }
        public Node<E> tail()
        {
            return getN(path.size() - 1);
        }
        public List<Node<E>> getPath() {
            return path;
        }

        public Node<E> getN(final int N)
        {
            return getPath().get(N);
        }

        public Stream<Node<E>> flow() {
            return super.flow(chooser);
        }

        private Node<E> link(final List<E> points, int i, final Node<E> parent)
        {
            if (i == points.size())
               return parent;
            else
                return link(points, i+1, parent.link(points.get(i))) ;

        }
    }
}
