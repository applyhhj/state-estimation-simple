package ic.app.se.simple.common;

import org.la4j.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2015/11/2.
 */
public class ComplexMatrix {

    private static Logger logger = LoggerFactory.getLogger(ComplexMatrix.class);

    private Matrix R;

    private Matrix I;

    private int rows;

    private int cols;

    public ComplexMatrix(Matrix R, Matrix I) {

        if (R.rows() != I.rows() || R.columns() != I.columns()) {

            logger.error("R and I are in different shape!");

            return;

        }

        this.R = R;

        this.I = I;

        rows = R.rows();

        cols = R.columns();

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

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public ComplexMatrix conj() {

        return new ComplexMatrix(R.multiply(1), I.multiply(-1));

    }

    public ComplexMatrix multiply(ComplexMatrix matrix) {

        Matrix r, i;

        r = R.multiply(matrix.getR()).add(I.multiply(-1).multiply(matrix.getI()));

        i = R.multiply(matrix.getI()).add(I.multiply(matrix.getR()));

        return new ComplexMatrix(r, i);

    }

    public ComplexMatrix multiplyJ() {

        return new ComplexMatrix(I.multiply(-1), R.multiply(1));

    }

    public ComplexMatrix add(ComplexMatrix matrix) {

        return new ComplexMatrix(R.add(matrix.getR()), I.add(matrix.getI()));

    }

    public ComplexMatrix minus(ComplexMatrix matrix) {

        return new ComplexMatrix(R.add(matrix.getR().multiply(-1)), I.add(matrix.getI().multiply(-1)));

    }

    public void print(String title) {

        System.out.print(title + "\n");

        print();

    }

    public void print() {

        System.out.print("Real part:\n" + R.toString() + "\n" + "Imaginary part:\n" + I.toString());

    }

}
