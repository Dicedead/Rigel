package ch.epfl.rigel.gui.searchtool;

import java.util.function.Predicate;

public interface Filter<T>{
    String filterBy(T t);
    default Predicate<T> filterBy(String s)
    {
        return  t -> filterBy(t).equals(s);
    }
}
