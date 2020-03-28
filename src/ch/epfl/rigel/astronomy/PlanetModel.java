package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;

import java.util.List;

/**
 * Mathematical model of all planets
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

    private final double Tp, epsilon, lonPer, excent, a, inc, lonN, theta0, V0;
    private final String name;

    private PlanetModel(String name, double v, double v1, double v2, double v3, double v4, double v5, double v6, double v7, double v8) {
        this.name = name;
        Tp = v;
        epsilon = Angle.ofDeg(v1);
        lonPer = Angle.ofDeg(v2);
        excent = v3;
        a = v4;
        inc = Angle.ofDeg(v5);
        lonN = Angle.ofDeg(v6);
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
        final double meanAnomaly = (Angle.TAU * daysSinceJ2010) / (DAYS_IN_TROP_YEAR * Tp) + epsilon - lonPer;
        final double trueAnomaly = meanAnomaly + 2 * excent * Math.sin(meanAnomaly);
        final double distanceToSun = a * (1 - excent * excent) / (1 + excent * Math.cos(trueAnomaly));
        final double helioLon = trueAnomaly + lonPer;
        final double sinl_LonN = Math.sin(helioLon - lonN);
        final double psi = Math.asin(sinl_LonN * Math.sin(inc));

        final double distanceToSun_Pr = distanceToSun * Math.cos(psi);
        final double helioLon_Pr = Math.atan2(sinl_LonN * Math.cos(inc), Math.cos(helioLon - lonN)) + lonN;

        //Making private auxiliary methods for computing meanAnomaly, trueAnomaly, distanceToSun & helioLon values for the
        //Earth seemed a little bit overkill.
        final double meanAnomaly_E = (Angle.TAU * daysSinceJ2010) / (DAYS_IN_TROP_YEAR * EARTH.Tp) + EARTH.epsilon - EARTH.lonPer;
        final double trueAnomaly_E = meanAnomaly_E + 2 * EARTH.excent * Math.sin(meanAnomaly_E);
        final double distanceToSun_E = EARTH.a * (1 - EARTH.excent * EARTH.excent) / (1 + EARTH.excent * Math.cos(trueAnomaly_E));
        final double helioLon_E = trueAnomaly_E + EARTH.lonPer;

        final double sinl_Pr_L = Math.sin(helioLon_Pr - helioLon_E);

        final double lambda = (this.ordinal() <= 1) ?
                Angle.normalizePositive(Math.PI + helioLon_E + Math.atan2(-1 * distanceToSun_Pr * sinl_Pr_L,
                        distanceToSun_E - distanceToSun_Pr * Math.cos(helioLon_E - helioLon_Pr))) :
                Angle.normalizePositive(helioLon_Pr + Math.atan2(distanceToSun_E * sinl_Pr_L,
                        distanceToSun_Pr - distanceToSun_E * Math.cos(helioLon_Pr - helioLon_E)));

        final double beta = Math.atan((distanceToSun_Pr * Math.tan(psi) * Math.sin(lambda - helioLon_Pr)) / (distanceToSun_E * sinl_Pr_L));

        //ANGULAR SIZE & MAGNITUDE
        final double rho = Math.sqrt(distanceToSun_E * distanceToSun_E + distanceToSun * distanceToSun - 2 * distanceToSun_E *
                distanceToSun * Math.cos(helioLon - helioLon_E) * Math.cos(psi));

        final double sqrtOfPhase = Math.sqrt((1 + Math.cos(lambda - helioLon)) / 2);

        return new Planet(name,
                eclipticToEquatorialConversion.apply(EclipticCoordinates.of(lambda, beta)), (float) (theta0 / rho),
                (float) (V0 + 5 * Math.log10(distanceToSun * rho / sqrtOfPhase)));
    }
}
