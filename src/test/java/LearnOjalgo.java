import org.la4j.Matrix;
import org.la4j.matrix.sparse.CRSMatrix;
import org.ojalgo.OjAlgoUtils;
import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.ComplexMatrix;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.matrix.decomposition.DecompositionStore;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.matrix.task.InverterTask;
import org.ojalgo.matrix.task.SolverTask;
import org.ojalgo.matrix.task.TaskException;
import org.ojalgo.netio.BasicLogger;
import org.ojalgo.random.Weibull;
import org.ojalgo.scalar.ComplexNumber;

import java.util.Random;

/**
 * Created by Administrator on 2015/11/5.
 */
public class LearnOjalgo {

    //matrix conj will transpose the original matrix
//    Primitive matrix multiply complex matrix will get primitive matrix
    public static void main(String[] args) {

        BasicLogger.debug();
        BasicLogger.debug(LearnOjalgo.class.getSimpleName());
        BasicLogger.debug(OjAlgoUtils.getTitle());
        BasicLogger.debug(OjAlgoUtils.getDate());
        BasicLogger.debug();

//        physicalStore();

//        System.exit(0);

        final BasicMatrix.Factory<PrimitiveMatrix> tmpFactory = PrimitiveMatrix.FACTORY;
        // A MatrixFactory has 13 different methods that return BasicMatrix instances.

        final PhysicalStore.Factory<Double, PrimitiveDenseStore> tmpphFactory = PrimitiveDenseStore.FACTORY;
        final double[][] tmphData = new double[][]{{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}, {7.0, 8.0, 9.0}, {10.0, 11.0, 12.0}, {13.0, 14.0, 15.0}};
        final PrimitiveDenseStore tmpphH = tmpphFactory.rows(tmphData);
//        System.out.print(tmpphH);
        final BasicMatrix ph2bas = tmpFactory.copy(tmpphH);
        System.out.print(ph2bas.getRowsRange(0, (int) ph2bas.countRows() / 2) + "\n\n");
        System.out.print(ph2bas.getRowsRange((int) ph2bas.countRows() / 2, (int) ph2bas.countRows()));

        final BasicMatrix tmpA = tmpFactory.makeEye(5, 3);
        System.out.print(tmpA);
        // Internally this creates an "eye-structure" - not a large array...
        final BasicMatrix tmpB = tmpFactory.makeFilled(300, 2, new Weibull(5.0, 2.0));
        // When you create a matrix with random elements you can specify their distribution.

//        final BasicMatrix tmpC = tmpB.multiplyLeft(tmpA);
//        final BasicMatrix tmpD = tmpA.multiply(tmpB);
        // ojAlgo differentiates between multiplying from the left and from the right.
        // The matrices C and D will be equal, but the code executed to calculate them are different.
        // The second alternative, resulting in D, will be MUCH faster!

        final BasicMatrix tmpE = tmpA.add(1000, 19, 3.14);
        final BasicMatrix tmpF = tmpE.add(10, 270, 2.18);
        // The BasicMatrix interface does not specify a set-method for matrix elements.
        // BasicMatrix instances are immutable.
        // The add(...) method should only be used to modify a small number of elements.

//        // Don't do this!!!
//        BasicMatrix tmpG = tmpFactory.makeZero(500, 500);
//        for (int j = 0; j < tmpG.countColumns(); j++) {
//            for (int i = 0; i < tmpG.countRows(); i++) {
//                tmpG = tmpG.add(i, j, 100.0 * Math.min(i, j));
//                // Note that add(..) actually adds the specified value to whatever is already there.
//                // In this case that, kind of, works since the base matrix is all zeros.
//                // Completely populating a matrix this way is a really bad idea!
//            }
//        }
//        // Don't do this!!!

        final double[][] tmpData = new double[][]{{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}, {7.0, 8.0, 9.0}};
        final double[][] A = new double[][]{{1.0, 1.0, 1.0}, {1 / 28.0, 1 / 30.0, 1 / 35.0}, {1 / 35.0, 1 / 30.0, 1 / 28.0}};
        final double[] B = new double[]{142.0, 4.5, 4.7};
        BasicMatrix Ab = tmpFactory.rows(A);
        System.out.print("\nAb\n" + Ab);
        BasicMatrix Bb = tmpFactory.rows(B);
        System.out.print("\nBb\n" + Bb);
        BasicMatrix x = solveLinear(Ab, Bb.transpose());
        System.out.print("\nx\n" + x);


        BasicMatrix tmpH = tmpFactory.rows(tmpData);
//        System.out.print(tmpH.modify(PrimitiveFunction.INVERT));
//        System.out.print(tmpH);

        final BasicMatrix tmp1111 = tmpFactory.makeEye(3, 3).multiply(2);
        BasicMatrix tmpH1 = tmp1111.multiplyElements(tmpH);
//        System.out.print(tmpH1.toString());

        BasicMatrix tmp22 = tmpH.add(1, 1, tmp1111);
//        System.out.print(tmp22.toString());
        // A, perhaps, natural way to create a small matrix, but the arrays are copied.
        // You do not want to create that array just as an intermediate step towards populating your matrix.
        // Doing it this way is clumsy for larger matrices.

        final Access2D.Builder<PrimitiveMatrix> tmpBuilder = tmpFactory.getBuilder(500, 500);
        final Matrix matrix = new CRSMatrix(500, 500);
        for (int j = 0; j < 500; j++) {
            for (int i = 0; i < 500; i++) {
                tmpBuilder.set(i, j, i + j);
//                matrix.set(i,j,i+j);
            }
        }
        final BasicMatrix tmpiiii = tmpBuilder.build();

        final BasicMatrix.Factory<ComplexMatrix> cplxtmpFactory = ComplexMatrix.FACTORY;
        final Access2D.Builder<ComplexMatrix> cplxtmpBuilder = cplxtmpFactory.getBuilder(5, 5);
        // If you want to individually set many/all elements of a larger matrix you should use the builder.
        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < 5; i++) {
//                cplxtmpBuilder.set(i, j, 100.0 * Math.min(i, j));
                cplxtmpBuilder.set(i, j, new ComplexNumber(i + 1, j + 1));
            }
        }
        final BasicMatrix tmpcplx = cplxtmpBuilder.build();
        System.out.print(tmpcplx);
        System.out.print(tmpcplx.conjugate());
//        System.out.print(tmpcplx.conjugate().toString());
//        System.out.print(tmpcplx.modify(ComplexFunction.CONJUGATE));
//        final BasicMatrix tmpcplxAbs=tmpcplx.modify(ComplexFunction.ABS);
//        final BasicMatrix tmpcplxAgl=tmpcplx.modify(ComplexFunction.ATAN);
//        final BasicMatrix tmpCplx1=tmpcplx.modify(ComplexFunction.CONJUGATE);
//        System.out.print("\n"+tmpCplx1.toString());
        final PhysicalStore<ComplexNumber> tmpps = tmpcplx.toComplexStore();
//        System.out.print("\n"+tmpps.get(0,0).phase()/Math.PI);

        final BasicMatrix tmpI = tmpBuilder.build();
        // Now you've seen 4 of the 13 MatrixFactory methods...

//        final BasicMatrix tmpJ = tmpA.mergeRows(tmpD);
//        final BasicMatrix tmpK = tmpJ.selectRows(1, 10, 100, 1000);
        // Sometimes it's practical to only use the factory/builder to create parts of the final matrix.


    }

    private static void physicalStore() {

        final PhysicalStore.Factory<Double, PrimitiveDenseStore> tmpFactory = PrimitiveDenseStore.FACTORY;

        final PrimitiveDenseStore tmpA = tmpFactory.makeEye(5000, 300);
        // A PrimitiveDenseStore is always a "full array". No smart data structures here...
        final PrimitiveDenseStore tmpB = tmpFactory.makeFilled(300, 2, new Weibull(5.0, 2.0));
        // The BasicMatrix and PhysicalStore factories are very similar. They both inherit a common interface.

        final MatrixStore<Double> tmpC = tmpB.multiplyLeft(tmpA);
        final MatrixStore<Double> tmpD = tmpA.multiply(tmpB);
        // When both matrices are PhysicalStore instances there is no major difference
        // between multiplying from the left and from the right.

        tmpA.set(1000, 19, 3.14);
        tmpA.set(10, 270, 2.18);
        // PhysicalStore instances are mutable - very mutable.
        // None of the methods defined in the PhysicalStore interface return matrices (of any type).
        // Most methods don't return anything at all... They just mutate the matrix.

        final PrimitiveDenseStore tmpE = tmpA.copy();
        final MatrixStore<Double> tmpF = tmpA.transpose();
        // If you need a copy to work with, you have to explicitly make that copy.

        final double[][] tmpData = new double[][]{{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}, {7.0, 8.0, 9.0}};
        final PrimitiveDenseStore tmpH = tmpFactory.rows(tmpData);
        // A, perhaps, natural way to create a small matrix, but the arrays are copied.
        // Doing it this way is clumsy for larger matrices.
        // If you don't have to create that array - then don't.
        Random random = new Random();
        final PrimitiveDenseStore tmpI = tmpFactory.makeZero(500, 500);
        for (int j = 0; j < tmpI.getColDim(); j++) {
            for (int i = 0; i < tmpI.getRowDim(); i++) {
                tmpI.set(i, j, 1 * random.nextGaussian());
            }
        }

//        for (int j = 0; j < tmpI.countRows()/5; j++) {
//            for (int i = 0; i < tmpI.countColumns(); i++) {
//                System.out.printf("%10.4f",tmpI.get(j, i));
//            }
//            System.out.print("\n");
//        }
        // Doing this is, of course, no problem at all! In many cases it's what you should do.
        MatrixStore<Double> res = null;
        InverterTask<Double> tmpInverter = InverterTask.PRIMITIVE.make(tmpI, false);
        final DecompositionStore<Double> tmpAlloc = tmpInverter.preallocate(tmpI);
        try {
            res = tmpInverter.invert(tmpI, tmpAlloc);
        } catch (TaskException e) {
            e.printStackTrace();
        }


        MatrixStore<Double> mul = res.multiply(tmpI);

        print(mul);

        final MatrixStore<Double> tmpJ = tmpA.builder().right(tmpD).build();
        final MatrixStore<Double> tmpK = tmpJ.builder().row(1, 10, 100, 1000).build();
        // Once you have a MatrixStore instance you can build on it, logically.

        final MatrixStore<Double> tmpG = tmpA.builder().right(tmpD).row(1, 10, 100, 1000).build();
        // And of course you can do it in one movement. The matrices K and G are equal.

    }

    private static void print(MatrixStore<Double> matrixStore) {

        for (int j = 0; j < matrixStore.countRows() / 5; j++) {
            for (int i = 0; i < matrixStore.countColumns(); i++) {
                System.out.printf("%10.4f", matrixStore.get(j, i));
            }
            System.out.print("\n");
        }


    }


    public static BasicMatrix solveLinear(BasicMatrix matA, BasicMatrix matB) {

        MatrixStore<Double> matAStore = matA.toPrimitiveStore();

        MatrixStore<Double> matBStore = matB.toPrimitiveStore();

        MatrixStore<Double> result = null;

        SolverTask<Double> tmpSolver = SolverTask.PRIMITIVE.make(matAStore, matBStore, false);

        final DecompositionStore<Double> tmpAlloc = tmpSolver.preallocate(matAStore, matBStore);

        try {

            result = tmpSolver.solve(matAStore, matBStore, tmpAlloc);

        } catch (TaskException ex) {

            ex.printStackTrace();

        }

        BasicMatrix.Factory<PrimitiveMatrix> basicRealMatrix2dFactory = PrimitiveMatrix.FACTORY;

        return result == null ? null : basicRealMatrix2dFactory.copy(result);

    }

}
