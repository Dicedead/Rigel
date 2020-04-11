package ch.epfl.rigel.parallelism;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class Path<T> extends Tree<T> {

    private final Function<List<Node<T>>, Node<T>> chooser = l -> l.get(0);
    private final List<Node<T>> path;

    public Path(List<T> points)
    {
        super(new Node<T>(points.get(0)));
        link(points, 1, getRoot());
        path = super.recursList(chooser);
    }

    public Path(List<Node<T>> path, Node<T> root)
    {
        super(root);
        this.path = path;
    }

    public Node<T> head()
    {
        return getRoot();
    }
    public Node<T> tail()
    {
        return getN(path.size());
    }
    public List<Node<T>> getPath() {
        return path;
    }

    public Node<T> getN(final int N)
    {
        return getPath().get(N);
    }

    public Stream<Node<T>> flow() {
        return super.flow(chooser);
    }

    private Node<T> link(final List<T> points, int i, final Node<T> parent)
    {
        if (i == points.size())
           return parent;
        else
            return link(points, i+1, parent.link(points.get(i))) ;

    }

}