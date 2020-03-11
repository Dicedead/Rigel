package ch.epfl.rigel.astronomy;

import java.util.List;

/**
 * Representation of a group of stars
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Asterism {
    private final List<Star> starList;
    public Asterism(List<Star> stars){
        starList = stars;
    };
    public List<Star> stars(){
        return starList;
    };
}
