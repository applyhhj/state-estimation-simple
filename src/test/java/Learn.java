import org.la4j.Matrix;
import org.la4j.matrix.sparse.CRSMatrix;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hjh on 15-10-11.
 */
public class Learn {

    public static void main(String[] args) {

        Matrix matrix = new CRSMatrix(10, 10);

        matrix.set(0, 1, 1.0);

        byte[] mb = matrix.toBinary();

        System.out.print(mb.length + "\n");

        byte[] dmb = matrix.toDenseMatrix().toBinary();

        System.out.print(dmb.length + "\n");

        System.out.print(matrix.toString());

        System.exit(0);

        List<Double> testlist=new ArrayList<Double>(11);

        for (int i = 0; i < 5; i++) {

            switch (i){

                case 1:

                case 2:

                case 3:{

                    System.out.print("multi\n");

                    break;

                }

                case 4:{

                    System.out.print("single\n");

                    break;

                }

            }

        }


    }
}
