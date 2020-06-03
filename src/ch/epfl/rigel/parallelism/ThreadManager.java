package ch.epfl.rigel.parallelism;

import ch.epfl.rigel.math.graphs.GraphNode;
import ch.epfl.rigel.math.graphs.Tree;
import ch.epfl.rigel.math.sets.abstraction.AbstractMathSet;
import ch.epfl.rigel.math.sets.implement.MathSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.concurrent.Task;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ch.epfl.rigel.math.sets.implement.MathSet.of;


/**
 * Multithreaded environment manager
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class  ThreadManager<T> {

    private final static int CPU_CORES = Runtime.getRuntime().availableProcessors();
    private final ExecutorService threadPool;
    private final ObservableSet<List<Task<?>>> tasksQueue;
    private final AbstractMathSet<Tree<Function<T , Task<?>>>> taskForest;

    private static final Predicate<Method> IS_LEAF = m -> m.getAnnotation(Requires.class).requirements().length == 0;

    private static final Function<Method, Predicate<Method>> HAS_METHOD_AS_REQUIREMENT = m ->
            (t -> Arrays.stream(t.getAnnotation(Requires.class).requirements()).anyMatch(s -> s.equals(m.getName())));

    /**
     * Main ThreadManager constructor
     */
    public ThreadManager(Class<T> t, T ob)
    {
        threadPool          = Executors.newCachedThreadPool();
        tasksQueue          = FXCollections.synchronizedObservableSet(FXCollections.observableSet(new ArrayList<>()));
        taskForest            = constructTaskTree(t);
        tasksQueue.addListener((SetChangeListener<List<Task<?>>>)c -> {
            if (c.wasAdded())
                c.getElementAdded().forEach(threadPool::submit);
            else if (c.wasRemoved())
                c.getElementRemoved().forEach(Task::cancel);
        });
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Requires {

        String[] requirements() default {};
        int priority() default 1;

    }

    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.LOCAL_VARIABLE)
    public @interface Export {
    }

    private static <T> GraphNode<Function<T , Task<?>>> root(Class<T> indicator)
    {
        return new GraphNode<>(t -> new Task<Void>() {
            @Override
            protected Void call(){
                return null;
            }
        });
    }

    private static <T> Optional<Method> extractUniqueMethod(String name, Class<T> c)
    {
        return Arrays.stream(c.getMethods()).filter(m -> m.getName().equals(name)).findAny();
    }

    private static Task<?> methodToTask(Method m, Object O)
    {
        return new Task<>() {
            @Override
            protected Object call() throws Exception {
                return m.invoke(O);
            }
        };
    }

    private static <T> Map<Method, GraphNode<Function<T , Task<?>>>> recurConstruct(Map<Method, GraphNode<Function<T , Task<?>>>> res, Class<T> tClass)
    {
        if (res.keySet().stream().allMatch(IS_LEAF))
            return res;

        return recurConstruct(res
                .keySet()
                .stream()
                .flatMap(m -> IS_LEAF.test(m) ? Stream.of(Optional.of(m)) :
                        Arrays.stream(m.getAnnotation(Requires.class).requirements())
                        .map(s -> extractUniqueMethod(s, tClass)))
                .filter(Optional::isPresent)
                .collect(Collectors.toMap(Optional::get, k -> new GraphNode<Function<T , Task<?>>>((t -> methodToTask(k.get(), t))))),
                tClass);
    }

    private AbstractMathSet<Tree<Function<T , Task<?>>>> constructTaskTree(Class<T> c)
    {

        Set<Method> methods = extractMultiThreaded(c);
        Set<Method> singles = extractSingleThreaded(c);
        //Find requirementless methods
        final var requirementLess = methods.stream().filter(m -> methods.stream().noneMatch(HAS_METHOD_AS_REQUIREMENT.apply(m)))
                .collect(Collectors.toSet());

        final AbstractMathSet<Map<Method, GraphNode<Function<T , Task<?>>>>> leafs =
                        requirementLess
                            .stream().map(m -> recurConstruct(Map.of(m, new GraphNode<Function<T, Task<?>>>(t -> methodToTask(m, t))), c))
                            .collect(MathSet.toMathSet());

        var singleThreaded = singles.stream().map(m -> new Tree<>(of(new GraphNode<Function<T , Task<?>>>(t -> methodToTask(m, t))))).collect(MathSet.toMathSet());
        return leafs.image(m -> new Tree<>(m.values().stream().map(GraphNode::hierarchy).collect(Collectors.toSet()))).union(singleThreaded);
    }

    private static boolean isMultithreaded (final Method object) {
        return object.getClass().isAnnotationPresent(Requires.class);
    }

    private static Set<Method> extractMultiThreaded(final Class<?> Class)
    {
        return Arrays.stream(Class.getMethods()).filter(ThreadManager::isMultithreaded).collect(Collectors.toSet());
    }
    private static Set<Method> extractSingleThreaded(final Class<?> Class)
    {
        return Arrays.stream(Class.getMethods()).filter(Predicate.not(ThreadManager::isMultithreaded)).collect(Collectors.toSet());
    }

    private void assignTasks()
    {
        for (Tree<Function<T , Task<?>>> t : taskForest)
        {


        }

    }

    /**
     *
     * @return the number of available CPU cores counting soft ones like intel's hyperthreading
     */
    public static int getCpuCores() {
        return CPU_CORES;
    }

}
