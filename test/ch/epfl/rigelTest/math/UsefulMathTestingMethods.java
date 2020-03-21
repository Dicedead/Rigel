package ch.epfl.rigelTest.math;

public class UsefulMathTestingMethods {

    public static double hoursFromHMS(int hour, int min, double sec) {
        return (hour*3600 + min*60 + sec)/3600d;
    }

}
