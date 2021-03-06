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
    URANUS("Uranus", 84.039492, 356.135400, 172.884833, 0.046321,
            19.21814, 0.773059, 73.926961, 65.80, -7.19),
    NEPTUNE("Neptune", 165.84539, 326.895127, 23.07, 0.010483,
            30.1985, 1.7673, 131.879, 62.20, -6.87);

    public final static List<PlanetModel> EXTRATERRESTRIAL = List.of(MERCURY, VENUS, MARS, JUPITER, SATURN, URANUS, NEPTUNE);

    private final static double DAYS_IN_TROP_YEAR = 365.242191;
    private final static double QUOTIENT = Angle.TAU / DAYS_IN_TROP_YEAR;

    private final double Tp, epsilon, lonPer, excent, a, inc, lonN, theta0, V0;
    private final String name;

    PlanetModel(String name, double v, double v1, double v2, double v3, double v4, double v5, double v6, double v7, double v8) {
        this.name = name;
        this.Tp = v;
        this.epsilon = Angle.ofDeg(v1);
        this.lonPer = Angle.ofDeg(v2);
        this.excent = v3;
        this.a = v4;
        this.inc = Angle.ofDeg(v5);
        this.lonN = Angle.ofDeg(v6);
        this.theta0 = Angle.ofArcsec(v7);
        this.V0 = v8;
    }

    /**
     * Builds a Planet at a given point and time.
     *
     * @param daysSinceJ2010        (double)
     * @param eclipToEquaConversion Celestial object's coordinates
     * @return (Planet) Fully created Planet with appropriate time, geographic and physical parameters.
     */
    @Override
    public Planet at(double daysSinceJ2010, EclipticToEquatorialConversion eclipToEquaConversion) {

        //DETERMINATION OF COORDINATES LAMBDA AND BETA
        double meanAnomaly = (daysSinceJ2010*QUOTIENT)/Tp + epsilon - lonPer;
        double trueAnomaly = meanAnomaly + 2 * excent * Math.sin(meanAnomaly);
        double helioLon = trueAnomaly + lonPer;
        double sinl_LonN = Math.sin(helioLon - lonN);
        double psi = Math.asin(sinl_LonN * Math.sin(inc));
        double distanceToSun = a * (1 - excent * excent) / (1 + excent * Math.cos(trueAnomaly));

        double distanceToSun_Pr = distanceToSun * Math.cos(psi);
        double helioLon_Pr = Math.atan2(sinl_LonN * Math.cos(inc), Math.cos(helioLon - lonN)) + lonN;

        //Making private auxiliary methods for computing meanAnomaly, trueAnomaly, distanceToSun & helioLon values for the
        //Earth seemed a little bit overkill.
        double meanAnomaly_E = (QUOTIENT * daysSinceJ2010) / EARTH.Tp + EARTH.epsilon - EARTH.lonPer;
        double trueAnomaly_E = meanAnomaly_E + 2 * EARTH.excent * Math.sin(meanAnomaly_E);
        double distanceToSun_E = EARTH.a * (1 - EARTH.excent * EARTH.excent) / (1 + EARTH.excent * Math.cos(trueAnomaly_E));
        double helioLon_E = trueAnomaly_E + EARTH.lonPer;

        double sinl_Pr_L = Math.sin(helioLon_Pr - helioLon_E);

        double lambda = (this.ordinal() <= 1) ?
                Angle.normalizePositive(Math.PI + helioLon_E + Math.atan2(-1 * distanceToSun_Pr * sinl_Pr_L,
                        distanceToSun_E - distanceToSun_Pr * Math.cos(helioLon_E - helioLon_Pr))) :
                Angle.normalizePositive(helioLon_Pr + Math.atan2(distanceToSun_E * sinl_Pr_L,
                        distanceToSun_Pr - distanceToSun_E * Math.cos(helioLon_Pr - helioLon_E)));

        //ANGULAR SIZE & MAGNITUDE
        double rho = Math.sqrt(distanceToSun_E * distanceToSun_E + distanceToSun * distanceToSun - 2 * distanceToSun_E *
                distanceToSun * Math.cos(helioLon - helioLon_E) * Math.cos(psi));

        return new Planet(name,
                eclipToEquaConversion.apply(EclipticCoordinates.of(lambda,
                        Math.atan((distanceToSun_Pr * Math.tan(psi) * Math.sin(lambda - helioLon_Pr)) / (distanceToSun_E * sinl_Pr_L)))),
                (float) (theta0 / rho),
                (float) (V0 + 5 * Math.log10(distanceToSun * rho / Math.sqrt((1 + Math.cos(lambda - helioLon)) / 2))));
    }

    /**
     * @param s (String)
     * @return (PlanetModel) the planetModel such that its name equals given string
     * @throws IllegalArgumentException if given string is not a planet's name
     */
    public static PlanetModel getPlanetModelFromString(String s) {
        for(PlanetModel planetModel : PlanetModel.EXTRATERRESTRIAL) {
            if (planetModel.name.equals(s)) {
                return planetModel;
            }
        }
        if (s.equals(PlanetModel.EARTH.name)) return PlanetModel.EARTH;
        else throw new IllegalArgumentException("Fatal error (PlanetModel): Given string is not a Planet's name.");
    }
}
