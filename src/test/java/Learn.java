import org.la4j.Matrix;
import org.la4j.matrix.sparse.CRSMatrix;

/**
 * Created by hjh on 15-10-11.
 */
public class Learn {

    public static void main(String[] args) {

        Matrix matrix = new CRSMatrix(10, 10);

        matrix.set(0, 1, 1.0);

        matrix.set(3, 6, 1.0);

        Matrix newMatrix = matrix.multiply(-1);

        matrix.set(0, 1, 1.0);

        System.out.print(newMatrix.toString() + "\n");

        System.out.print(matrix.toString() + "\n");


    }
}
