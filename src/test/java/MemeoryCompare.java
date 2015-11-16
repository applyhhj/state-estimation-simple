import org.la4j.Matrix;
import org.la4j.matrix.sparse.CRSMatrix;
import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.PrimitiveMatrix;

/**
 * Created on 2015/11/6.
 */
public class MemeoryCompare {

    public static void main(String[] args) {
        final BasicMatrix.Factory<PrimitiveMatrix> tmpFactory = PrimitiveMatrix.FACTORY;
        final Access2D.Builder<PrimitiveMatrix> tmpBuilder = tmpFactory.getBuilder(500, 500);
        final Matrix matrix = new CRSMatrix(500, 500);
        for (int j = 0; j < 500; j++) {
            for (int i = 0; i < 500; i++) {
                tmpBuilder.set(i, j, i + j);
                matrix.set(i, j, i + j);
            }
        }
        final BasicMatrix tmpiiii = tmpBuilder.build();
    }

}
