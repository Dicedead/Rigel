package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;

import java.util.List;

/**
 * Representation of a group of stars
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class Asterism {
    private final List<Star> starList;

    /**
     * Construct an immutable Asterim
     *
     * @param stars (List<Star>) Stars to be considered as an asterism
     * @throws IllegalArgumentException if stars is an empty list
     */
    public Asterism(List<Star> stars) {
        Preconditions.checkArgument(!(stars.isEmpty()), "Asterism: Given list of stars is empty.");
        starList = List.copyOf(stars);
    }

    /**
     * @return (List <Star>) immutable list of stars in the asterism
     */
    public List<Star> stars() {
        return starList;
    }
}
