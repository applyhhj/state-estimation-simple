package ic.app.se.simple.common;

import org.la4j.Matrix;
import org.la4j.matrix.dense.Basic1DMatrix;
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

    public ComplexMatrix hadamardMultiply(ComplexMatrix matrix) {

        if (cols != matrix.getCols() || rows != matrix.getRows()) {

            logger.error("Not the same size!");

        }

        Matrix real = new Basic1DMatrix(rows, cols);

        Matrix imag = new Basic1DMatrix(rows, cols);

        double r1, i1, r2, i2;

        for (int j = 0; j < cols; j++) {

            for (int i = 0; i < rows; i++) {

                r1 = R.get(i, j);

                r2 = matrix.getR().get(i, j);

                i1 = I.get(i, j);

                i2 = matrix.getI().get(i, j);

                real.set(i, j, r1 * r2 - i1 * i2);

                imag.set(i, j, r1 * i2 + r2 * i1);

            }

        }

        return new ComplexMatrix(real, imag);

    }

    public void print(String title) {

        System.out.print("\n" + title + "\n");

        print();

    }

    public void print() {

        System.out.print("Real part:\n" + R.toString() + "\n" + "Imaginary part:\n" + I.toString());

    }

}
