package ch.epfl.rigelTest.astronomy;

public final class UsefulMathTestingMethods {

    static double hoursFromHMS(int hour, int min, double sec) {
        return (hour*3600 + min*60 + sec)/3600d;
    }

}
