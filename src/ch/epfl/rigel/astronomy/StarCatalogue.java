package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Catalogue of stars and asterisms
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class StarCatalogue {

    private final List<Star> starList;
    private final Map<Asterism, List<Integer>> asterismMap;

    /**
     * Constructs a catalogue of stars in and possibly out of asterisms
     *
     * @param stars (List<Star>) List of all the stars in the asterisms and possibly more
     * @param asterisms (List<Asterism>) List of asterisms grouping some or all the stars in 'stars' List
     * @throws IllegalArgumentException if a star in an asterim isn't listed in stars
     */
    public StarCatalogue(List<Star> stars, List<Asterism> asterisms)
    {
        for(Asterism currentAsterism : asterisms) {
            for(Star currentStar : currentAsterism.stars()) {
                Preconditions.checkArgument(stars.contains(currentStar));
            }
        }
        this.starList = List.copyOf(stars);
        asterismMap = new HashMap<>();

        /*TODO: fill this map using Loader/Builder methods implemented below */

    }

    /**
     * @return (List<Star>) all the stars in the catalogue as a list
     */
    public List<Star> stars()
    {
        return starList;
    }

    /**
     * @return (Set<Asterism>) a set of all the asterisms in the catalogue
     */
    public Set<Asterism> asterisms()
    {
        return asterismMap.keySet();
    }

    /**
     * Method for finding the indices of the stars (given in asterism) in starList
     *
     * @param asterism (Asterism)
     * @return (List<Integer>) a list of said indices
     */
    public List<Integer> asterismIndices(Asterism asterism)
    {
        Preconditions.checkArgument(asterismMap.containsKey(asterism));
        return asterismMap.get(asterism);
    }

    /***
     * Builds a StarCatalogue instance
     */
    public static final class Builder {

        private List<Star> starsToBuild;
        private List<Asterism> asterismsToBuild;

        /**
         * Default constructor initializing 2 empty lists of stars and asterisms
         */
        public Builder() {
            starsToBuild     = List.of();
            asterismsToBuild = List.of();
        }

        /**
         * Add star to catalogue
         *
         * @param star (Star)
         * @return (Builder) this
         */
        public Builder addStar(Star star) {
            starsToBuild.add(star);
            return this;
        }

        /**
         * Add asterism to catalogue
         *
         * @param asterism (Asterism)
         * @return (Builder) this
         */
        public Builder addAsterism(Asterism asterism) {
            asterismsToBuild.add(asterism);
            return this;
        }

        /**
         * @return (List<Star>) unmodifiable view of the starList being built
         */
        public List<Star> stars() {
            return Collections.unmodifiableList(starsToBuild);
        }

        /**
         * @return (List<Asterism>) unmodifiable view of the list of asterisms being built
         */
        public List<Asterism> asterisms() {
            return Collections.unmodifiableList(asterismsToBuild);
        }

        /**
         * Adds to the catalogue the stars and/or asterisms in the inputStream via the loader
         *
         * @param inputStream (InputStream)
         * @param loader (Loader)
         * @throws IOException (I/O method)
         * @return (Builder) this
         */
        public Builder loadFrom(InputStream inputStream, Loader loader) throws IOException {

            //TODO

            return this;
        }

        /**
         * @return (StarCatalogue) fully built and immutable StarCatalogue
         */
        public StarCatalogue build() {
            return new StarCatalogue(starsToBuild,asterismsToBuild);
        }
    }

    /**
     * Abstraction of a loader
     */
    public interface Loader {

        /**
         * Abstracts the loading of an inputStream into a StarCatalogue.Builder
         *
         * @param inputStream (InputStream) data to be loaded
         * @param builder (StarCatalogue.Builder)
         * @throws IOException (I/O method)
         */
        public abstract void load(InputStream inputStream, Builder builder) throws IOException;
    }
}
