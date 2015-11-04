import org.la4j.Matrix;
import org.la4j.matrix.sparse.CRSMatrix;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hjh on 15-10-11.
 */
public class Learn {

    public static void main(String[] args) {

        Matrix matrix = new CRSMatrix(10, 5);

        Matrix subMatrix = new CRSMatrix(7, 2);

        for (int i = 0; i < subMatrix.rows(); i++) {

            subMatrix.set(i, 0, i + 1);

        }

        System.out.print(subMatrix.toString() + "\n");

//        Matrix newMatrix = matrix.insert(subMatrix, 2, 0, subMatrix.rows(), subMatrix.columns());

//        System.out.print(newMatrix.toString() + "\n");

        List<Integer> tst = new ArrayList<Integer>();

        tst.add(1);

        tst.add(3);

        tst.add(5);

        System.out.print(tst.size());

        updateList(tst);

        System.out.print(tst.size());
    }

    public static void updateList(List<Integer> in) {

        in.add(100);

    }
}
