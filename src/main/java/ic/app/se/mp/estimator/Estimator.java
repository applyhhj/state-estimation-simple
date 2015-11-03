package ic.app.se.mp.estimator;

import ic.app.se.simple.common.ComplexMatrix;
import org.la4j.Matrix;
import org.la4j.matrix.dense.Basic1DMatrix;
import org.la4j.matrix.sparse.CRSMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ic.app.se.simple.common.Utils.MatrixExtend.insertMatrix;

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

    private ComplexMatrix Sf;

    private ComplexMatrix St;

    private Matrix HF;

    private ComplexMatrix Vnorm;

    private ComplexMatrix VnormMatrix;

    private ComplexMatrix V;

    private ComplexMatrix VMatrix;

    private PowerSystem powerSystem;

    public Estimator(PowerSystem powerSystem) {

        this.powerSystem = powerSystem;

        computeDSbusDv();

        computeDSbrDv();

        HF = composeFullHMatrix();

//        print();

    }

    public void print() {

        dSbDvm.print("dSbus_dVm");

        dSbDva.print("dSbus_dVa");

        dSfDvm.print("dSf_dVm");

        dSfDva.print("dSf_dVa");

        dStDvm.print("dSt_dVm");

        dStDva.print("dSt_dVa");

        Sf.print("Sf");

        St.print("St");

        System.out.print("Full H matrix\n" + HF.toString());

    }

    /*  this is the measurement jacobi matrix
    *          ang1      ang2 ....   angNb   Vm1   Vm2 ....  VmNb
    *  PF1  dPF1/dAng1
    *  PF2
    *  .
    *  .
    *  PFN
    *  PT1
    *  .
    *  .
    *  PTN
    *  PB1
    *  .
    *  .
    *  PBN
    *  ang1
    *  ang2
    *  .
    *  .
    *  angNb
    *  QF1
    *  .
    *  .
    *  QFN
    *  QT1
    *  .
    *  .
    *  QTN
    *  QB1
    *  .
    *  .
    *  QBN
    *  Vm1
    *  .
    *  .
    *  VmNb
    *
    *
    * */
    private Matrix composeFullHMatrix() {

        int nb = powerSystem.getMpData().getBusData().getN();

        int nbr = powerSystem.getMpData().getBranchData().getN();

        int coln = 2 * nb;

        int rown = 4 * nbr + 4 * nb;

        Matrix zeroNb = new CRSMatrix(nb, nb);

        Matrix oneNb = CRSMatrix.diagonal(nb, 1);

        Matrix HFull = new CRSMatrix(rown, coln);

//        HFull=HFull.insert(dSfDva.getR());
        HFull = insertMatrix(HFull, dSfDva.getR());

//        HFull=HFull.insert(dSfDvm.getR(), 0, nb, dSfDvm.getRows(), dSfDvm.getCols());
        HFull = insertMatrix(HFull, dSfDvm.getR(), 0, nb);

//        HFull=HFull.insert(dStDva.getR(), nbr, 0, dStDva.getRows(), dStDva.getCols());
        HFull = insertMatrix(HFull, dStDva.getR(), nbr, 0);

//        HFull.insert(dStDvm.getR(), nbr, nb, dStDvm.getRows(), dStDvm.getCols());
        HFull = insertMatrix(HFull, dStDvm.getR(), nbr, nb);

//        HFull.insert(dSbDva.getR(), nbr * 2, 0, dSbDva.getRows(), dSbDva.getCols());
        HFull = insertMatrix(HFull, dSbDva.getR(), nbr * 2, 0);

//        HFull.insert(dSbDvm.getR(), nbr * 2, nb, dSbDvm.getRows(), dSbDvm.getCols());
        HFull = insertMatrix(HFull, dSbDvm.getR(), nbr * 2, nb);

//        HFull.insert(oneNb, nbr * 2 + nb, 0, oneNb.rows(), oneNb.columns());
        HFull = insertMatrix(HFull, oneNb, nbr * 2 + nb, 0);

//        HFull.insert(zeroNb, nbr * 2 + nb, nb, zeroNb.rows(), zeroNb.columns());
        HFull = insertMatrix(HFull, zeroNb, nbr * 2 + nb, nb);

//        HFull.insert(dSfDva.getI(), 2 * (nb + nbr), 0, dSfDva.getRows(), dSfDva.getCols());
        HFull = insertMatrix(HFull, dSfDva.getI(), 2 * (nb + nbr), 0);

//        HFull.insert(dSfDvm.getI(), 2 * (nb + nbr), nb, dSfDvm.getRows(), dSfDvm.getCols());
        HFull = insertMatrix(HFull, dSfDvm.getI(), 2 * (nb + nbr), nb);

//        HFull.insert(dStDva.getI(), 3 * nbr + 2 * nb, 0, dStDva.getRows(), dStDva.getCols());
        HFull = insertMatrix(HFull, dStDva.getI(), 3 * nbr + 2 * nb, 0);

//        HFull.insert(dStDvm.getI(), 3 * nbr + 2 * nb, nb, dStDvm.getRows(), dStDvm.getCols());
        HFull = insertMatrix(HFull, dStDvm.getI(), 3 * nbr + 2 * nb, nb);

//        HFull.insert(dSbDva.getI(), 4 * nbr + 2 * nb, 0, dSbDva.getRows(), dSbDva.getCols());
        HFull = insertMatrix(HFull, dSbDva.getI(), 4 * nbr + 2 * nb, 0);

//        HFull.insert(dSbDvm.getI(), 4 * nbr + 2 * nb, nb, dSbDvm.getRows(), dSbDvm.getCols());
        HFull = insertMatrix(HFull, dSbDvm.getI(), 4 * nbr + 2 * nb, nb);

//        HFull.insert(zeroNb, 4 * nbr + 3 * nb, 0, zeroNb.rows(), zeroNb.columns());
        HFull = insertMatrix(HFull, zeroNb, 4 * nbr + 3 * nb, 0);

//        HFull.insert(oneNb, 4 * nbr + 3 * nb, nb, oneNb.rows(), oneNb.columns());
        HFull = insertMatrix(HFull, oneNb, 4 * nbr + 3 * nb, nb);

        System.out.print(HFull.toString());

        return HFull;

    }

    private void computeDSbrDv() {

        ComplexMatrix Yf, Yt, If, It, IfMatrix, ItMatrix, Vf, Vt, VfNorm, VtNorm, VfMatrix, VtMatrix;

        Yf = powerSystem.getyMatrix().getYf();

        Yt = powerSystem.getyMatrix().getYt();

        If = Yf.multiply(V);

        It = Yt.multiply(V);

        IfMatrix = expandVectorToDiagonalMatrix(If);

        ItMatrix = expandVectorToDiagonalMatrix(It);

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

        dSfDva = IfMatrix.conj().multiply(VNbrNbF).minus(VfMatrix.multiply(Yf.multiply(VMatrix).conj())).multiplyJ();

        dSfDvm = VfMatrix.multiply(Yf.multiply(VnormMatrix).conj()).add(IfMatrix.conj().multiply(VNbrNbNormF));

        dStDva = ItMatrix.conj().multiply(VNbrNbT).minus(VtMatrix.multiply(Yt.multiply(VMatrix).conj())).multiplyJ();

        dStDvm = VtMatrix.multiply(Yt.multiply(VnormMatrix).conj()).add(ItMatrix.conj().multiply(VNbrNbNormT));

        Sf = Vf.hadamardMultiply(If.conj());

        St = Vt.hadamardMultiply(It.conj());

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

            internalIdx = powerSystem.getMpData().getBusData().getTOI().get(ij[i]) - 1;

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
