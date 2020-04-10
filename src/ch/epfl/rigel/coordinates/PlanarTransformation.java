package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.Preconditions;

import java.util.function.Function;

/**
 * Functional class representing a 2x3 matrix for scaling, translating and rotating transformations.
 * Lighter subsidiary for JavaFX's Transform.
 * @see javafx.scene.transform.Transform
 *
 * @author Alexandre Sallinen (303162)
 * @author Salim Najib (310003)
 */
public final class PlanarTransformation implements Function<CartesianCoordinates, CartesianCoordinates> {
    /*
      JavaFX's Transform class has to go through many processes and intermediate types (Scale, Rotate, Point2D,...)
      which are of no use for this project, therefore we've remade Transform which greatly improved efficiency and
      possibly -for once in this project...- code readability in SkyCanvasPainter.
     */

    private final double Mxx, Mxy, Myy, Myx, Tx, Ty;
    private final double determinant;

    private PlanarTransformation(final double mxx, final double mxy, final double myx, final double myy, final double tx,
                                 final double ty)  {
        Mxx = mxx;
        Mxy = mxy;
        Myy = myy;
        Myx = myx;
        Tx = tx;
        Ty = ty;

        determinant =  Mxx * Myy - Mxy * Myx;
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
    public static PlanarTransformation of(final double mxx, final double mxy, final double myx, final double myy, final double tx,
                                          final double ty) {
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
    public static PlanarTransformation ofDilatAndTrans(final double dilatX, final double dilatY, final double tx, final double ty) {
        return new PlanarTransformation(dilatX, 0, 0, dilatY, tx, ty);
    }

    /**
     * Constructs the following matrix:
     * [ dilat     0      tx ]
     * [   0    -dilat    ty ]
     *
     * @param dilat (double) dilatation coefficient, position (1,1)
     * @param tx    (double) position (1,3) - translation coefficient, set to 0 if no translation on x axis wanted
     * @param ty    (double) position (2,3) - translation coefficient, set to 0 if no translation on y axis wanted
     * @return (PlanarTransformation) loaded diagonal matrix
     */
    public static PlanarTransformation ofDilatAndTrans(final double dilat,  final double tx, final double ty) {
        return new PlanarTransformation(dilat, 0, 0, -dilat, tx, ty);
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
    public static PlanarTransformation inverseOf(final PlanarTransformation trans) throws IllegalArgumentException{
        Preconditions.checkArgument(trans.determinant != 0);
        final double inversDet = 1/trans.determinant;
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
     * @param cartesCoords (CartesianCoordinates) Input 2x1 vector
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
    public CartesianCoordinates apply(final double x, final double y) {
        return CartesianCoordinates.of(Mxx * x + Mxy * y + Tx, Myx * x + Myy * y + Ty);
    }

    /**
     * Computes the product this * cartesCoords; let cartesCoords = (x,y):
     * [ mxx mxy ] [ x ]    [ mxx * x + mxy * y ]
     * [ myx myy ] [ y ]  = [ myx * x + myy * y ]
     * This method thus does not add any translation
     *
     * @param cartesianCoordinates (CartesianCoordinates) Input 2x1 vector
     * @return (CartesianCoordinates) 2x1 vector resulting of the product
     */
    public CartesianCoordinates applyVectorial(final CartesianCoordinates cartesianCoordinates) {
        return applyVectorial(cartesianCoordinates.x(), cartesianCoordinates.y());
    }

    /**
     * @see PlanarTransformation#apply(CartesianCoordinates)
     * Alternate apply method not requiring the creation of a CartesianCoordinates object
     *
     * @param x (double) 1st coefficient of input 2x1 vector
     * @param y (double) 2nd coefficient of input 2x1 vector
     * @return (CartesianCoordinates) 2x1 vector resulting of the product
     */
    public CartesianCoordinates applyVectorial(final double x, final double y) {
        return CartesianCoordinates.of(Mxx * x + Mxy * y, Myx * x + Myy * y);
    }

    /**
     * Stretches a distance after application to this transformation
     *
     * @param initialDistance (double) distance before transformation
     * @return (double) distance after transformation
     */
    public double applyDistance(final double initialDistance) {
        return Math.abs(Mxx * initialDistance + Mxy * initialDistance) + Math.abs(Myx * initialDistance + Myy * initialDistance);
    }

    /**
     * @return (double) determinant, suitable to check whether the transformation is invertible
     */
    public double getDeterminant() {
        return determinant;
    }
}
