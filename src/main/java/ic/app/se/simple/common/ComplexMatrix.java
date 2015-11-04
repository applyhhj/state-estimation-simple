package ic.app.se.simple.common;

import org.la4j.Matrix;
import org.la4j.matrix.dense.Basic1DMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static ic.app.se.simple.common.Utils.MatrixExtend.excludeMatrix;

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

        r = R.multiply(matrix.getR()).subtract(I.multiply(matrix.getI()));

        i = R.multiply(matrix.getI()).add(I.multiply(matrix.getR()));

        return new ComplexMatrix(r, i);

    }

    public ComplexMatrix multiplyJ() {

        return new ComplexMatrix(I.multiply(-1), R.multiply(1));

    }

    public ComplexMatrix add(ComplexMatrix matrix) {

        return new ComplexMatrix(R.add(matrix.getR()), I.add(matrix.getI()));

    }

    public ComplexMatrix subtract(ComplexMatrix matrix) {

        return new ComplexMatrix(R.subtract(matrix.getR()), I.subtract(matrix.getI()));

    }

    //    similar to dot product in matlab
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

    public Matrix abs() {

        Matrix ret = Matrix.zero(rows, cols);

        double real, imag, abs;

        for (int i = 0; i < rows; i++) {

            for (int j = 0; j < cols; j++) {

                real = R.get(i, j);

                imag = I.get(i, j);

                abs = Math.sqrt(real * real + imag * imag);

                ret.set(i, j, abs);

            }

        }

        return ret;

    }

    public Matrix angle() {

        Matrix ret = Matrix.zero(rows, cols);

        double real, imag, angle;

        for (int i = 0; i < rows; i++) {

            for (int j = 0; j < cols; j++) {

                real = R.get(i, j);

                imag = I.get(i, j);

                if (real == 0) {

                    if (imag > 0) {

                        angle = Math.PI / 2;

                    } else if (imag < 0) {

                        angle = -Math.PI / 2;

                    } else {

                        logger.warn("Both real and imaginary part are zero, set angle to 0.");

                        angle = 0;

                    }

                } else {

                    angle = Math.atan(imag / real);

                }

                ret.set(i, j, angle);

            }

        }

        return ret;

    }

    public ComplexMatrix copy() {

        return new ComplexMatrix(R.copy(), I.copy());

    }

    public void print(String title) {

        System.out.print("\n" + title + "\n");

        print();

    }

    public void print() {

        System.out.print("Real part:\n" + R.toString() + "\n" + "Imaginary part:\n" + I.toString());

    }

    public ComplexMatrix excludeSubMatrix(List<Integer> rows, List<Integer> cols) {

        return new ComplexMatrix(excludeMatrix(R, rows, cols), excludeMatrix(I, rows, cols));

    }

}
