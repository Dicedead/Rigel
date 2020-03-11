package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;

import java.util.List;

/**
 * Model of all planets
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public enum PlanetModel implements CelestialObjectModel<Planet> {

    MERCURY("Mercure", 0.24085, 75.5671, 77.612, 0.205627,
            0.387098, 7.0051, 48.449, 6.74, -0.42),
    VENUS("Vénus", 0.615207, 272.30044, 131.54, 0.006812,
            0.723329, 3.3947, 76.769, 16.92, -4.40),
    EARTH("Terre", 0.999996, 99.556772, 103.2055, 0.016671,
            0.999985, 0, 0, 0, 0),
    MARS("Mars", 1.880765, 109.09646, 336.217, 0.093348,
            1.523689, 1.8497, 49.632, 9.36, -1.52),
    JUPITER("Jupiter", 11.857911, 337.917132, 14.6633, 0.048907,
            5.20278, 1.3035, 100.595, 196.74, -9.40),
    SATURN("Saturne", 29.310579, 172.398316, 89.567, 0.053853,
            9.51134, 2.4873, 113.752, 165.60, -8.88),
    URANUS("Uranus", 84.039492, 271.063148, 172.884833, 0.046321,
            19.21814, 0.773059, 73.926961, 65.80, -7.19),
    NEPTUNE("Neptune", 165.84539, 326.895127, 23.07, 0.010483,
            30.1985, 1.7673, 131.879, 62.20, -6.87);

    static final public List<PlanetModel> ALL = List.of(PlanetModel.values());

    private final static double DAYS_IN_TROP_YEAR = 365.242191;

    private final double Tp, epsilon, LonPer, excent, a, inc, LonN, theta0, V0;
    private final String name;

    private PlanetModel(String name, double v, double v1, double v2, double v3, double v4, double v5, double v6, double v7, double v8) {
        this.name = name;
        Tp = v;
        epsilon = Angle.ofDeg(v1);
        LonPer = Angle.ofDeg(v2);
        excent = v3;
        a = v4;
        inc = v5;
        LonN = Angle.ofDeg(v6);
        theta0 = Angle.ofArcsec(v7);
        V0 = v8;
    }

    /**
     * Builds a Planet at a given point and time.
     *
     * @param daysSinceJ2010                 (double)
     * @param eclipticToEquatorialConversion Celestial object's coordinates
     * @return (Planet) Fully created Planet with appropriate time, geographic and physical parameters.
     */
    @Override
    public Planet at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {

        //DETERMINATION OF COORDINATES LAMBDA AND BETA
        double M = (Angle.TAU * daysSinceJ2010) / (DAYS_IN_TROP_YEAR * Tp) + epsilon - LonPer;
        double v = M + 2 * excent * Math.sin(M);
        double r = a * (1 - excent * excent) / (1 + excent * Math.cos(v));
        double l = v + LonPer;
        double sinl_LonN = Math.sin(l - LonN);
        double psi = Math.asin(sinl_LonN * Math.sin(inc));

        double r_Pr = r * Math.cos(psi);
        double l_Pr = Math.atan2(sinl_LonN * Math.cos(inc), Math.cos(l - LonN)) + LonN;

        //Making private auxiliary methods for computing M, v, r l values for the Earth seemed a little bit
        //overkill.
        double M_E = (Angle.TAU * daysSinceJ2010) / (DAYS_IN_TROP_YEAR * EARTH.Tp) + EARTH.epsilon - EARTH.LonPer;
        double v_E = M_E + 2 * EARTH.excent * Math.sin(M_E);
        double R = EARTH.a * (1 - EARTH.excent * EARTH.excent) / (1 + EARTH.excent * Math.cos(v_E));
        double L = v_E + EARTH.LonPer;

        double Lambda;
        double sinl_Pr_L = Math.sin(l_Pr - L);

        if (ALL.indexOf(this) <= 1) {
            Lambda = Math.PI + L + Math.atan2(-1 * r_Pr * sinl_Pr_L, R - r_Pr * Math.cos(L - l_Pr));
        } else {
            Lambda = l_Pr + Math.atan2(R * sinl_Pr_L, r_Pr - R * Math.cos(l_Pr - L));
        }

        double Beta = Math.atan2(r_Pr * Math.tan(psi) * Math.sin(Lambda - l_Pr),
                R * sinl_Pr_L);

        //ANGULAR SIZE & MAGNITUDE
        double rho = Math.sqrt(R * R + r * r - 2 * R * r * Math.cos(l - L) * Math.cos(psi));
        double angSize = theta0 / rho;

        double sqrt_F = Math.sqrt((1 + Math.cos(Lambda - l)) / 2);
        double magnitude = V0 + 5 * Math.log10(r * rho / sqrt_F);

        return new Planet(name, eclipticToEquatorialConversion
                .apply(EclipticCoordinates.of(Lambda, Beta)), (float) angSize, (float) magnitude);
    }
}
