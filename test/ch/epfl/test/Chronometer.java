package ch.epfl.test;

import java.util.*;
import java.util.function.Function;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Collectors;

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

    static public Map<Integer, Long> battle(final List<Method> func, List<Object[]> args, Object[] from, int repetition) throws InvocationTargetException, IllegalAccessException {
        Map<Integer, Long> results = new HashMap<>(func.size());
        for (int j = 0; j < func.size(); ++j) {
            var t1 = System.nanoTime();
            if (! (from == null)) {
                for (int i = 0; i < repetition; i++) {
                    func.get(j).invoke(from[j], args.get(j));
                }
                var t2 = System.nanoTime();
                results.put(j, (t2 - t1));
            }
            else{
                for (int i = 0; i < repetition; i++) {
                    func.get(j).invoke(null, args.get(j));
                }
                var t2 = System.nanoTime();
                results.put(j, (t2 - t1));
            }
        }
        return results;
    }

    static public String prettyPrint (Map<Integer, Long> k)
    {
        return "Results \n******************* \n" +
                k.entrySet().stream().map(E -> "\n Function " + E.getKey().toString() + " scored " + E.getValue()).collect(Collectors.joining());
    }

}
