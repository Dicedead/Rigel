package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.Preconditions;

import java.util.function.UnaryOperator;

/**
 * Functional class representing a 2x3 matrix for scaling, translating and rotating transformations.
 * Lighter subsidiary to JavaFX's Transform.
 * @see javafx.scene.transform.Transform
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class PlanarTransformation implements UnaryOperator<CartesianCoordinates> {
    /*
      JavaFX's Transform class has to go through many processes and intermediate types (Scale, Rotate, Point2D,...)
      which are of no use for this project, therefore we've remade Transform which greatly improved efficiency and
      possibly -for once in our project...- code readability in SkyCanvasPainter.
     */

    private static final double BASICALLY_ZERO = 1e-13;

    private final double Mxx, Mxy, Myy, Myx, Tx, Ty;
    private final double determinant;

    private final boolean isDiagonal;
    /* A bit of a shortcut name as isDiagonal also implies Mxx = -Myy */

    private PlanarTransformation(double mxx, double mxy, double myx, double myy, double tx, double ty)  {
        Mxx = mxx;
        Mxy = mxy;
        Myy = myy;
        Myx = myx;
        Tx = tx;
        Ty = ty;

        determinant =  Mxx * Myy - Mxy * Myx;

        isDiagonal = (Mxx > 0) && (Myy == -Mxx) && (Myx == 0) && (Mxy == 0);
    }

    /**
     * Main factory constructor of the following matrix:
     * [ mxx mxy tx ]
     * [ myx myy ty ]
     *
     * @param mxx (double) position (1,1)
     * @param mxy (double) position (1,2)
     * @param myx (double) position (2,1)
     * @param myy (double) position (2,2)
     * @param tx  (double) position (1,3) - translation coefficient, set to 0 if no translation on x axis wanted
     * @param ty  (double) position (2,3) - translation coefficient, set to 0 if no translation on y axis wanted
     * @return (PlanarTransformation) loaded 2x3 matrix
     */
    public static PlanarTransformation of(double mxx, double mxy, double myx, double myy, double tx, double ty) {
        return new PlanarTransformation(mxx, mxy, myx, myy, tx, ty);
    }

    /**
     * Constructs the following matrix:
     * [ dilatX    0      tx ]
     * [   0    dilatY    ty ]
     *
     * @param dilatX (double) dilatation coefficient, position (1,1)
     * @param dilatY (double) dilatation coefficient, position (2,2)
     * @param tx    (double) position (1,3) - translation coefficient, set to 0 if no translation on x axis wanted
     * @param ty    (double) position (2,3) - translation coefficient, set to 0 if no translation on y axis wanted
     * @return (PlanarTransformation) loaded diagonal matrix
     */
    public static PlanarTransformation ofDilatAndTrans(double dilatX, double dilatY, double tx, double ty) {
        return new PlanarTransformation(dilatX, 0, 0, dilatY, tx, ty);
    }

    /**
     * Constructs the following matrix:
     * [ dilat     0      tx ]
     * [   0    -dilat    ty ]
     *
     * @param dilat (double) dilatation coefficient, with same sign at position (1,1), opposite sign at (2,2)
     * @param tx    (double) position (1,3) - translation coefficient, set to 0 if no translation on x axis wanted
     * @param ty    (double) position (2,3) - translation coefficient, set to 0 if no translation on y axis wanted
     * @return (PlanarTransformation) loaded diagonal matrix
     */
    public static PlanarTransformation ofDilatAndTrans(double dilat, double tx, double ty) {
        return new PlanarTransformation(dilat, 0, 0, -dilat, tx, ty);
    }

    /**
     * Constructs the following rotation matrix:
     * [ cos(rad) -sin(rad) 0 ]
     * [ sin(rad) cos(rad)  0 ]
     *
     * @param rad (double) angle of rotation, given in radians
     * @return (PlanarTransformation)
     */
    public static PlanarTransformation rotation(double rad) {
        double cosRad = Math.cos(rad);
        double sinRad = Math.sin(rad);
        return new PlanarTransformation(cosRad, -sinRad, sinRad, cosRad, 0, 0);
    }

    /**
     * Composes two planar transformations: multiplies the 2x2 left matrices and adds up translations
     *
     * @param other (PlanarTransformation) other transformation
     * @return (PlanarTransformation) resulting composition matrix
     */
    public PlanarTransformation concat(PlanarTransformation other) {
        return new PlanarTransformation(
                Mxx * other.Mxx + Mxy * other.Myx,
                Mxx * other.Mxy + Mxy * other.Myy,
                Myx * other.Mxx + Myy * other.Myx,
                Myx * other.Mxy + Myy * other.Myy,
                Tx + other.Tx,
                Ty + other.Ty);
    }

    /**
     * Constructs the inverse transformation of trans, namely:
     * 1/ det [  myy -mxy mxy*ty - myy*tx ]
     *        [ -myx  mxx myx*tx - mxx*ty ]
     *
     * @param trans (PlanarTransformation) input matrix:
     * [ mxx mxy tx ]
     * [ myx myy ty ]
     *
     * @return (PlanarTransformation) inverse transformation of trans
     * @throws IllegalArgumentException if determinant of input matrix == 0 (ie input matrix isn't invertible)
     */
    public static PlanarTransformation inverseOf(PlanarTransformation trans) {
        Preconditions.checkArgument(Math.abs(trans.determinant) >= BASICALLY_ZERO,
                "PlanarTransformation: tried to invert non invertible matrix (det == or really close to 0).");
        double inversDet = 1/trans.determinant;
        return new PlanarTransformation(inversDet * trans.Myy, -inversDet * trans.Mxy, -inversDet * trans.Myx,
                inversDet * trans.Mxx, inversDet * (trans.Mxy * trans.Ty - trans.Myy * trans.Tx),
                inversDet * (trans.Myx * trans.Tx - trans.Mxx * trans.Ty));
    }

    /**
     * Short syntax method for applying the inverse of current transformation
     *
     * @return (PlanarTransformation) inverse of transformation (if this.determinant != 0)
     */
    public PlanarTransformation invert() {
        return inverseOf(this);
    }

    /**
     * Computes the product this * cartesCoords; let cartesCoords = (x,y):
     * [ mxx mxy tx ] [ x ]    [ mxx * x + mxy * y + tx ]
     * [ myx myy ty ] [ y ]  = [ myx * x + myy * y + ty ]
     *                [ 1 ]
     *
     * @param cartesCoords (CartesianCoordinates) Input 2x1 vector, treated as a 2D point
     * @return (CartesianCoordinates) 2x1 vector resulting of the product
     */
    @Override
    public CartesianCoordinates apply(final CartesianCoordinates cartesCoords) {
        return apply(cartesCoords.x(), cartesCoords.y());
    }

    /**
     * @see PlanarTransformation#apply(CartesianCoordinates)
     * Alternate apply method not requiring the creation of a CartesianCoordinates object
     *
     * @param x (double) 1st coefficient of input 2x1 vector
     * @param y (double) 2nd coefficient of input 2x1 vector
     * @return (CartesianCoordinates) 2x1 vector resulting of the product
     */
    public CartesianCoordinates apply(double x, double y) {
        return isDiagonal ? CartesianCoordinates.of(Mxx * x + Tx, Myy * y + Ty) :
                CartesianCoordinates.of(Mxx * x + Mxy * y + Tx, Myx * x + Myy * y + Ty);
    }

    /**
     * Computes the product this * cartesCoords; let cartesCoords = (x,y):
     * [ mxx mxy ] [ x ]    [ mxx * x + mxy * y ]
     * [ myx myy ] [ y ]  = [ myx * x + myy * y ]
     * This method thus does not add any translation to the input vector
     *
     * @param cartesianCoordinates (CartesianCoordinates) Input 2x1 vector
     * @return (CartesianCoordinates) 2x1 vector resulting of the product
     */
    public CartesianCoordinates applyVector(CartesianCoordinates cartesianCoordinates) {
        return applyVector(cartesianCoordinates.x(), cartesianCoordinates.y());
    }

    /**
     * @see PlanarTransformation#apply(CartesianCoordinates)
     * Alternate apply method not requiring the creation of a CartesianCoordinates object
     *
     * @param x (double) 1st coefficient of input 2x1 vector
     * @param y (double) 2nd coefficient of input 2x1 vector
     * @return (CartesianCoordinates) 2x1 vector resulting of the product
     */
    public CartesianCoordinates applyVector(double x, double y) {
        return isDiagonal ? CartesianCoordinates.of(Mxx * x, Myy * y) :
        CartesianCoordinates.of(Mxx * x + Mxy * y, Myx * x + Myy * y);
    }

    /**
     * Stretches a distance after application to this transformation, computing the euclidean norm of
     * [ initialDistance ]  after transformation (translation-less).
     * [       0         ]
     *
     * @param initialDistance (double) distance before transformation
     * @return (double) distance after transformation
     */
    public double applyDistance(double initialDistance) {
        return isDiagonal ? Math.abs(Mxx * initialDistance) :
                euclideanNormOf(Mxx * initialDistance, Myx * initialDistance);
    }

    /**
     * @return (double) determinant, suitable to check whether the transformation is invertible
     */
    public double getDeterminant() {
        return determinant;
    }

    /**
     * Compute the Euclidean norm of a vector
     *
     * @param coords (CartesianCoordinates) input vector
     * @return (double) coords' norm
     */
    public static double euclideanNormOf(CartesianCoordinates coords) {
        return euclideanNormOf(coords.x(), coords.y());
    }

    /**
     * Compute the Euclidean norm of a vector
     *
     * @param x (double) first coordinate of input vector
     * @param y (double) second coordinate of input vector
     * @return (double) vector's norm
     */
    public static double euclideanNormOf(double x, double y) {
        return Math.sqrt(euclideanNormSquared(x, y));
    }

    /**
     * Compute the square of the Euclidean norm of a vector
     *
     * @param x (double) first coordinate of input vector
     * @param y (double) second coordinate of input vector
     * @return (double) vector's norm squared
     */
    public static double euclideanNormSquared(double x, double y) {
        return x*x + y*y;
    }

    /**
     * Computes the square of the euclidean norm of the vector joining coord1 to coord2
     *
     * @param coord1 (CartesianCoordinates)
     * @param coord2 (CartesianCoordinates)
     * @return (double)
     */
    public static double euclideanDistSquared(CartesianCoordinates coord1, CartesianCoordinates coord2) {
        return euclideanNormSquared(coord1.x() - coord2.x(), coord1.y() - coord2.y());
    }

    /**
     * Computes the euclidean norm of the vector joining coord1 to coord2
     *
     * @param coord1 (CartesianCoordinates)
     * @param coord2 (CartesianCoordinates)
     * @return (double)
     */
    public static double euclideanDistance(CartesianCoordinates coord1, CartesianCoordinates coord2) {
        return euclideanNormOf(coord1.x() - coord2.x(), coord1.y() - coord2.y());
    }
}
