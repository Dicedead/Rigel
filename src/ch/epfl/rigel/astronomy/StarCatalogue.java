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
    private final Set<Asterism> immutableAsterismSet;

    /**
     * Constructs a catalogue of stars in and possibly out of asterisms
     *
     * @param stars     (List<Star>) List of all the stars in the asterisms and possibly more
     * @param asterisms (List<Asterism>) List of asterisms grouping some or all the stars in 'stars' List
     * @throws IllegalArgumentException if a star in an asterim isn't listed in stars
     */
    public StarCatalogue(List<Star> stars, List<Asterism> asterisms) {
        for (Asterism currentAsterism : asterisms) {
            Preconditions.checkArgument(stars.containsAll(currentAsterism.stars()));
        }
        this.starList = List.copyOf(stars);
        asterismMap = new HashMap<>();

        List<Integer> indicesList = new ArrayList<>();
        for (Asterism asterism : asterisms) {
            for (Star star : asterism.stars()) {
                indicesList.add(starList.indexOf(star));
            }
            asterismMap.put(asterism, List.copyOf(indicesList));
            indicesList.clear();
        }
        immutableAsterismSet = Set.copyOf(asterismMap.keySet());
    }

    /**
     * @return (List < Star >) all the stars in the catalogue as a list (immutable)
     */
    public List<Star> stars() {
        return starList;
    }

    /**
     * @return (Set < Asterism >) an immutable set of all the asterisms in the catalogue
     */
    public Set<Asterism> asterisms() {
        return immutableAsterismSet;
    }

    /**
     * Method for finding the indices of the stars (given in asterism) in starList
     *
     * @param asterism (Asterism)
     * @return (List < Integer >) an immutable list of said indices
     */
    public List<Integer> asterismIndices(Asterism asterism) {
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
            starsToBuild = new ArrayList<>();
            asterismsToBuild = new ArrayList<>();
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
         * @return (List < Star >) unmodifiable view of the starList being built
         */
        public List<Star> stars() {
            return Collections.unmodifiableList(starsToBuild);
        }

        /**
         * @return (List < Asterism >) unmodifiable view of the list of asterisms being built
         */
        public List<Asterism> asterisms() {
            return Collections.unmodifiableList(asterismsToBuild);
        }

        /**
         * Adds to the catalogue the stars and/or asterisms in the inputStream via the loader
         *
         * @param inputStream (InputStream)
         * @param loader      (Loader)
         * @return (Builder) this
         * @throws IOException (I/O method)
         */
        public Builder loadFrom(InputStream inputStream, Loader loader) throws IOException {
            loader.load(inputStream, this);
            return this;
        }

        /**
         * @return (StarCatalogue) fully built and immutable StarCatalogue
         */
        public StarCatalogue build() {
            return new StarCatalogue(starsToBuild, asterismsToBuild);
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
         * @param builder     (StarCatalogue.Builder)
         * @throws IOException (I/O method)
         */
        public abstract void load(InputStream inputStream, Builder builder) throws IOException;
    }
}
