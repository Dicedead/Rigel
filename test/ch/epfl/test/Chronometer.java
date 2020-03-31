package ch.epfl.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;
import java.util.function.Function;

public class Chronometer
{
    private Chronometer() {
        throw new UnsupportedOperationException();
    }
    static public <A> List<A> generateRandomArgsD(Function<Double, A> transition, int number)
    {
        List<A> res = new ArrayList<>(number);
        var rand = new SplittableRandom();
        for (int i = 0; i < number; i++) {
            res.add(transition.apply(rand.nextDouble()));
        }

        return res;
    }

    static public <A> List<A> generateRandomArgsI(Function<Integer, A> transition, int number)
    {
        List<A> res = new ArrayList<>(number);
        var rand = new SplittableRandom();
        for (int i = 0; i < number; i++) {
            res.add(transition.apply(rand.nextInt()));
        }

        return res;
    }

    static public <A> List<A> generateRandomArgsB(Function<Boolean, A> transition, int number)
    {
        List<A> res = new ArrayList<>(number);
        var rand = new SplittableRandom();
        for (int i = 0; i < number; i++) {
            res.add(transition.apply(rand.nextBoolean()));
        }

        return res;
    }

    static public <A, B, C, D> double[] battle(final Function<A, B> f, final Function<C, D> g, A arg1, C arg2, int repetition)
    {
        var t1 = System.nanoTime();
        for (int i = 0; i < repetition; i++) {
            f.apply(arg1);
        }
        var t2 = System.nanoTime();
        double elapsed1 = t2 - t1;

        var t3 = System.nanoTime();
        for (int i = 0; i < repetition; i++) {
            g.apply(arg2);
        }
        var t4 = System.nanoTime();
        double elapsed2 = t4 - t3;

        return new double[]{elapsed1, elapsed2};
    }
}
