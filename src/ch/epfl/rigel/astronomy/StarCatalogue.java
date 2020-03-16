package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public final class StarCatalogue {

    private final List<Star> stars;
    private final Map<Asterism, List<Integer>> map;

    /**
     * Constructs a catalogue of stars in asterisms
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
        this.stars = List.copyOf(stars);
        map = new HashMap<>();

        /*TODO: fill this map using Loader/Builder methods implemented below */

    }

    public List<Star> stars()
    {
        return stars;
    }

    public Set<Asterism> asterisms()
    {
        return map.keySet();
    }

    public List<Integer> asterismIndices(Asterism asterism)
    {
        Preconditions.checkArgument(map.containsKey(asterism));
        return map.get(asterism);
    }

    public static final class Builder {

        private List<Star> starsToBuild;
        private List<Asterism> asterismsToBuild;

        public Builder() {
            starsToBuild     = List.of();
            asterismsToBuild = List.of();
        }

        public Builder addStar(Star star) {
            starsToBuild.add(star);
            return this;
        }

        public Builder addAsterism(Asterism asterism) {
            asterismsToBuild.add(asterism);
            return this;
        }

        public List<Star> stars() {
            return Collections.unmodifiableList(starsToBuild);
        }

        public List<Asterism> asterisms() {
            return Collections.unmodifiableList(asterismsToBuild);
        }

        public StarCatalogue build() {
            return new StarCatalogue(starsToBuild,asterismsToBuild);
        }

        public Builder loadFrom(InputStream inputStream, Loader loader) throws IOException {

            //TODO

            return this;
        }
    }

    public interface Loader {
        public abstract void load(InputStream inputStream, Builder builder) throws IOException;
    }
}
