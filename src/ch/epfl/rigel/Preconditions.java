package ch.epfl.rigel;

import ch.epfl.rigel.math.Interval;

public final class Preconditions {

    private Preconditions(){}

    /**
     * Validity checking method
     * @param isTrue Boolean to check
     */
    public static void checkArgument(boolean isTrue){
        if (!isTrue) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Checking value containment in an interval
     *
     * @param interval Said interval
     * @param value Said value
     */
    public static double checkInInterval(Interval interval, double value) {
        if (!interval.contains(value)) {
            throw new IllegalArgumentException();
        }
        return value;
    }

}
