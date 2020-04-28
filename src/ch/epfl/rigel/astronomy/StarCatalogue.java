package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Catalogue of stars and asterisms
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class StarCatalogue {

    private final List<Star> starList;
    private final Map<Asterism, List<Integer>> asterismMap;
    private final Map<Star, Integer> starToIndexMap;
    private final Set<Asterism> immutableAsterismSet;
    /**
     * Constructs a catalogue of stars in and possibly out of asterisms
     *
     * @param stars     (List<Star>) List of all the stars in the asterisms and possibly more
     * @param asterisms (List<Asterism>) List of asterisms grouping some or all the stars in 'stars' List
     * @throws IllegalArgumentException if a star in an asterim isn't listed in stars
     */
    public StarCatalogue(List<Star> stars, List<Asterism> asterisms) {

        this.starToIndexMap = Collections.unmodifiableMap(IntStream.range(0,stars.size()).boxed()
                .collect(Collectors.toMap(stars::get,Function.identity(), (o1,o2)->o1)));
        //Although this map causes some spatial complexity, it avoids an O(n*m) call to indexOf below

        this.asterismMap = asterisms.stream().collect(Collectors.toMap(Function.identity(),
                asterism -> { Preconditions.checkArgument(starToIndexMap.keySet().containsAll(asterism.stars())); //(*)
                return asterism.stars().stream().map(starToIndexMap::get).collect(Collectors.toUnmodifiableList());},
                (v, u) -> u)); //the method ref is equivalent to: star -> starToIndexMap.get(star)

        /* (*): starToIndexMap is a HashMap, therefore calling containsAll upon its keySet may be better but no worse
                than upon a List - depends of the hash. In this case, it proved to speed up the construction of
                StarCatalogue instances by 20+ times in average.*/

        this.starList = List.copyOf(stars);
        this.immutableAsterismSet = Collections.unmodifiableSet(asterismMap.keySet());
        //keySet allows for retain & retainAll, need to make it immutable; as the map's visibility is restrained to this
        //class and the Asterisms are immutable objects, this O(1) call suffices.

    }

    /**
     * @return (List <Star>) all the stars in the catalogue as a list (immutable)
     */
    public List<Star> stars() {
        return starList;
    }

    /**
     * @return (Set <Asterism>) an immutable set of all the asterisms in the catalogue
     */
    public Set<Asterism> asterisms() {
        return immutableAsterismSet;
    }

    /**
     * Method for finding the indices of the stars (given in asterism) in catalogue.stars()
     *
     * @param asterism (Asterism)
     * @return (List <Integer>) an immutable list of said indices
     */
    public List<Integer> asterismIndices(Asterism asterism) {
        Preconditions.checkArgument(asterismMap.containsKey(asterism));
        return asterismMap.get(asterism);
    }

    /***
     * Builds a StarCatalogue instance
     */
    public static final class Builder {

        private final List<Star> starsToBuild;
        private final List<Asterism> asterismsToBuild;

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
         * @return (List <Star>) unmodifiable view of the starList being built
         */
        public List<Star> stars() {
            return Collections.unmodifiableList(starsToBuild);
        }

        /**
         * @return (List <Asterism>) unmodifiable view of the list of asterisms being built
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
     * Abstraction of a resource loader
     */
    public interface Loader {

        /**
         * Abstracts the loading of an inputStream into a StarCatalogue.Builder
         *
         * @param inputStream (InputStream) data to be loaded
         * @param builder     (StarCatalogue.Builder)
         * @throws IOException (I/O method)
         */
        void load(InputStream inputStream, Builder builder) throws IOException;
    }
}
