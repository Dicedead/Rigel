package ch.epfl.rigel;

public final class Preconditions {
    private Preconditions(){}
    public static void checkArgument(boolean isTrue){
        if (!isTrue) {
            throw new IllegalArgumentException();
        }
    }
}
