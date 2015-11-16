package ic.app.se.mp.estimator;

import org.la4j.Matrix;
import org.ojalgo.matrix.decomposition.DecompositionStore;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.matrix.task.InverterTask;
import org.ojalgo.matrix.task.TaskException;

/**
 * Created by Administrator on 2015/11/5.
 */
public class OjalgoUtils {


    public static Matrix computeHTWHInv(Matrix HH, Matrix WWInv) {

        MatrixStore<Double> hhoj = la4jMatrixToMatrixStore(HH);

        MatrixStore<Double> wwinvoj = la4jMatrixToMatrixStore(WWInv);

        MatrixStore<Double> res = hhoj.transpose().multiply(wwinvoj).multiply(hhoj);

        res = invertOjMatrix(res);

        return ojMatrixStoreToLa4jMatrix(res);

    }

    public static MatrixStore<Double> la4jMatrixToMatrixStore(Matrix matrix) {

        final PhysicalStore.Factory<Double, PrimitiveDenseStore> matrixFactory = PrimitiveDenseStore.FACTORY;

        PrimitiveDenseStore WInvOjStore = matrixFactory.makeZero(matrix.rows(), matrix.columns());

        for (int i = 0; i < WInvOjStore.getRowDim(); i++) {

            for (int j = 0; j < WInvOjStore.getColDim(); j++) {

                WInvOjStore.set(i, j, matrix.get(i, j));

            }

        }

        MatrixStore<Double> ret = WInvOjStore.builder().build();

        return ret;

    }

    public static Matrix ojMatrixStoreToLa4jMatrix(MatrixStore<Double> matrixStore) {

        Matrix res = Matrix.zero((int) matrixStore.countRows(), (int) matrixStore.countColumns());

        for (int i = 0; i < res.rows(); i++) {

            for (int j = 0; j < res.columns(); j++) {

                res.set(i, j, matrixStore.get(i, j));

            }

        }

        return res;

    }

    public static MatrixStore<Double> invertOjMatrix(MatrixStore matrixStore) {

        MatrixStore<Double> res = null;

        InverterTask<Double> tmpInverter = InverterTask.PRIMITIVE.make(matrixStore, false);

        final DecompositionStore<Double> tmpAlloc = tmpInverter.preallocate(matrixStore);

        try {

            res = tmpInverter.invert(matrixStore, tmpAlloc);

        } catch (TaskException e) {

            e.printStackTrace();

        }

        return res;

    }

}
