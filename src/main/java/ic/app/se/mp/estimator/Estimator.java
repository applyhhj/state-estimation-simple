package ic.app.se.mp.estimator;

import ic.app.se.simple.common.ComplexMatrix;
import org.la4j.Matrix;
import org.la4j.matrix.dense.Basic1DMatrix;
import org.la4j.matrix.sparse.CRSMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2015/11/2.
 */
public class Estimator {

    private static Logger logger = LoggerFactory.getLogger(Estimator.class);

    private ComplexMatrix dSbDvm;

    private ComplexMatrix dSbDva;

    private ComplexMatrix dSfDvm;

    private ComplexMatrix dSfDva;

    private ComplexMatrix dStDvm;

    private ComplexMatrix dStDva;

    private ComplexMatrix Vnorm;

    private ComplexMatrix VnormMatrix;

    private ComplexMatrix V;

    private ComplexMatrix VMatrix;

    private PowerSystem powerSystem;

    public Estimator(PowerSystem powerSystem) {

        this.powerSystem = powerSystem;

        computeDSbusDv();

//        print();

    }

    public void print() {

        dSbDvm.print("dSbus_dVm");

        dSbDva.print("dSbus_dVa");

    }

    private void computeDSbrDv() {

        ComplexMatrix Yf, Yt, IfMatrix, ItMatrix, Vf, Vt, VfNorm, VtNorm, VfMatrix, VtMatrix;

        Yf = powerSystem.getyMatrix().getYf();

        Yt = powerSystem.getyMatrix().getYt();

        IfMatrix = expandVectorToDiagonalMatrix(Yf.multiply(V));

        ItMatrix = expandVectorToDiagonalMatrix(Yt.multiply(V));

        Vf = getVft(powerSystem.getMpData().getBranchData().getI(), V);

        Vt = getVft(powerSystem.getMpData().getBranchData().getJ(), V);

        VfNorm = getVft(powerSystem.getMpData().getBranchData().getI(), Vnorm);

        VtNorm = getVft(powerSystem.getMpData().getBranchData().getJ(), Vnorm);

        VfMatrix = expandVectorToDiagonalMatrix(Vf);

        VtMatrix = expandVectorToDiagonalMatrix(Vt);

        int NBranch = powerSystem.getMpData().getBranchData().getN();

        int NBus = powerSystem.getMpData().getBusData().getN();

        int[] idxBranch = new int[NBranch];

        int[] idxBusFInt = new int[NBranch];

        int[] idxBusTInt = new int[NBranch];

        for (int i = 0; i < NBranch; i++) {

            idxBranch[i] = i;

        }

        for (int i = 0; i < NBranch; i++) {

            idxBusFInt[i] = powerSystem.getMpData().getBusData().getTOI().get(
                    powerSystem.getMpData().getBranchData().getI()[i]) - 1;

        }

        for (int i = 0; i < NBranch; i++) {

            idxBusTInt[i] = powerSystem.getMpData().getBusData().getTOI().get(
                    powerSystem.getMpData().getBranchData().getJ()[i]) - 1;

        }

        ComplexMatrix VNbrNbF, VNbrNbT, VNbrNbNormF, VNbrNbNormT;

        VNbrNbF = toSpareMatrix(idxBranch, idxBusFInt, Vf, NBranch, NBus);

        VNbrNbT = toSpareMatrix(idxBranch, idxBusTInt, Vt, NBranch, NBus);

        VNbrNbNormF = toSpareMatrix(idxBranch, idxBusFInt, VfNorm, NBranch, NBus);

        VNbrNbNormT = toSpareMatrix(idxBranch, idxBusTInt, VtNorm, NBranch, NBus);

        dSbDva = IfMatrix.conj().multiply(VNbrNbF).minus(VfMatrix.multiply(Yf.multiply(VMatrix).conj())).multiplyJ();

        dSfDvm = VfMatrix.multiply(Yf.multiply(VnormMatrix).conj()).add(IfMatrix.conj().multiply(VNbrNbNormF));

        dStDva = ItMatrix.conj().multiply(VNbrNbT).minus(VtMatrix.multiply(Yt.multiply(VMatrix).conj())).multiplyJ();

        dStDvm = VtMatrix.multiply(Yt.multiply(VnormMatrix).conj()).add(ItMatrix.conj().multiply(VNbrNbNormT));

    }

    //    index should start from 0
    private ComplexMatrix toSpareMatrix(int[] i, int[] j, ComplexMatrix matrix, int rows, int cols) {

        if (i.length != j.length || matrix.getCols() > 1) {

            logger.error("Invalid input!");

            return null;

        }

        Matrix R = new CRSMatrix(rows, cols);

        Matrix I = new CRSMatrix(rows, cols);

        for (int k = 0; k < i.length; k++) {

            R.set(i[k], j[k], matrix.getR().get(k, 0));

            I.set(i[k], j[k], matrix.getI().get(k, 0));

        }

        return new ComplexMatrix(R, I);

    }

    private void computeDSbusDv() {

        ComplexMatrix Ibus;

        V = powerSystem.getPowerFlow().getV();

        Ibus = powerSystem.getyMatrix().getYbus().multiply(V);

        Vnorm = computeVnorm(V);

        VMatrix = expandVectorToDiagonalMatrix(V);

        Ibus = expandVectorToDiagonalMatrix(Ibus);

        VnormMatrix = expandVectorToDiagonalMatrix(Vnorm);

        dSbDvm = VMatrix.multiply(powerSystem.getyMatrix().getYbus().multiply(VnormMatrix).conj())
                .add(Ibus.conj().multiply(VnormMatrix));

        dSbDva = VMatrix.multiplyJ().multiply(Ibus.minus(powerSystem.getyMatrix().getYbus().multiply(VMatrix)).conj());

    }

    private ComplexMatrix expandVectorToDiagonalMatrix(ComplexMatrix vec) {

        ComplexMatrix ret = new ComplexMatrix(new CRSMatrix(vec.getR().rows(), vec.getR().rows()),
                new CRSMatrix(vec.getR().rows(), vec.getR().rows()));

        for (int i = 0; i < vec.getR().rows(); i++) {

            ret.getR().set(i, i, vec.getR().get(i, 0));

            ret.getI().set(i, i, vec.getI().get(i, 0));

        }

        return ret;

    }

    private ComplexMatrix computeVnorm(ComplexMatrix v) {

        ComplexMatrix ret = new ComplexMatrix(new CRSMatrix(v.getRows(), v.getCols()),
                new CRSMatrix(v.getRows(), v.getCols()));

        double vr, vi, vm;

        for (int i = 0; i < v.getRows(); i++) {

            vr = v.getR().get(i, 0);

            vi = v.getI().get(i, 0);

            vm = Math.sqrt(vr * vr + vi * vi);

            ret.getR().set(i, 0, vr / vm);

            ret.getI().set(i, 0, vi / vm);

        }

        return ret;

    }

    //    input are external numbers, will convert to internal numbering
    private ComplexMatrix getVft(int[] ij, ComplexMatrix VMatrix) {

        if (VMatrix.getCols() != 1) {

            logger.error("Not a vector!");

            return null;

        }

        int n = ij.length;

        Matrix vftR = new Basic1DMatrix(n, 1);

        Matrix vftI = new Basic1DMatrix(n, 1);

        int internalIdx;

        for (int i = 0; i < n; i++) {

            internalIdx = powerSystem.getMpData().getBusData().getTOI().get(ij[i]);

            vftR.set(i, 0, VMatrix.getR().get(internalIdx, 0));

            vftI.set(i, 0, VMatrix.getI().get(internalIdx, 0));

        }

        return new ComplexMatrix(vftR, vftI);

    }

    public ComplexMatrix getdSbDva() {
        return dSbDva;
    }

    public ComplexMatrix getdSbDvm() {
        return dSbDvm;
    }
}
