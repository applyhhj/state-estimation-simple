package ic.app.se.simple.common;

import org.la4j.Matrix;

/**
 * Created by Administrator on 2015/11/2.
 */
public class ComplexMatrix {

    private Matrix R;

    private Matrix I;

    public ComplexMatrix(Matrix R, Matrix I) {

        this.R = R;

        this.I = I;

    }

    public Matrix getI() {
        return I;
    }

    public void setI(Matrix i) {
        I = i;
    }

    public Matrix getR() {
        return R;
    }

    public void setR(Matrix r) {
        R = r;
    }

    public ComplexMatrix multiply(ComplexMatrix matrix) {

        Matrix r, i;

        r = R.multiply(matrix.getR()).add(I.multiply(-1).multiply(matrix.getI()));

        i = R.multiply(matrix.getI()).add(I.multiply(matrix.getR()));

        return new ComplexMatrix(r, i);

    }

    public ComplexMatrix add(ComplexMatrix matrix) {

        return new ComplexMatrix(R.add(matrix.getR()), I.add(matrix.getI()));

    }

    public ComplexMatrix minus(ComplexMatrix matrix) {

        return new ComplexMatrix(R.add(matrix.getR().multiply(-1)), I.add(matrix.getI().multiply(-1)));

    }

}
