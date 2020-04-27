package ch.epfl.rigel.gui.bonus;

import ch.epfl.rigel.astronomy.CelestialObject;

import java.util.function.Predicate;

@FunctionalInterface
public interface Filter<T>{
    String filterBy(T t);
    default Predicate<T> filterBy(String s)
    {
        return  t -> filterBy(t).equals(s);
    }
}
