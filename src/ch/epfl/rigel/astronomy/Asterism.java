package ch.epfl.rigel.astronomy;

import java.util.List;

public final class Asterism {
    private final List<Star> starList;
    public Asterism(List<Star> stars){
        starList = stars;
    };
    public List<Star> stars(){
        return starList;
    };
}
