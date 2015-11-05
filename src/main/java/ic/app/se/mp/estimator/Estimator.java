package ic.app.se.mp.estimator;

import ic.app.se.simple.common.ComplexMatrix;
import ic.app.se.simple.common.Constants;
import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.inversion.GaussJordanInverter;
import org.la4j.linear.GaussianSolver;
import org.la4j.matrix.dense.Basic1DMatrix;
import org.la4j.matrix.sparse.CRSMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ic.app.se.simple.common.Utils.MatrixExtend.*;

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

    private Matrix WInv;

    private ComplexMatrix VpfNorm;

    private ComplexMatrix VpfNormMatrix;

    private ComplexMatrix Vpf;

    private ComplexMatrix VpfMatrix;

    private PowerSystem powerSystem;

    private boolean converged;

    private boolean oneBadAtATime;

    private int maxItBadData;

    private int maxIt;

    private double badDataThreshold;

    private GaussianSolver gaussianSolver;

    public Estimator(PowerSystem powerSystem) {

        this.powerSystem = powerSystem;

        computeDSbusDv();

        computeDSbrDv();

        HF = composeFullHMatrix();

        converged = false;

        oneBadAtATime = true;

        maxItBadData = 50;

        maxIt = 100;

        badDataThreshold = 6.25;

        print();

    }

    public void estimate() {

        WInv = powerSystem.getMeasureSystem().getWInv();

        int ibad = 1;

        boolean hasBadData;

        Matrix zest = computeEstimatedMeasurement(powerSystem);

        Matrix deltz = powerSystem.getMeasureSystem().getZreal().subtract(zest);

        Matrix normF = deltz.transpose().multiply(WInv).multiply(deltz);

        while (!converged && ibad < maxItBadData) {

            hasBadData = false;

            Matrix HH = getHH(HF);

            Matrix WWInv = getWWInv(WInv);

            Matrix ddeltz = getDdeltz(deltz);

            ComplexMatrix VVs = getVVs(powerSystem.getState());

            Matrix VVsa = VVs.angle();

            Matrix VVsm = VVs.abs();

            int i = 0;

            while (!converged && i++ < maxIt) {

                Matrix b = HH.transpose().multiply(WWInv).multiply(ddeltz);

                Matrix A = HH.transpose().multiply(WWInv).multiply(HH);

                gaussianSolver = new GaussianSolver(A);

                Vector dx = gaussianSolver.solve(b.toColumnVector());

                VVsa = VVsa.add(getDdxa(dx));

                VVsm = VVsm.add(getDdxm(dx));

                updateVoltage(powerSystem.getState(), VVsa, VVsm, powerSystem.getMeasureSystem().getVbusExcludeIds());

                zest = computeEstimatedMeasurement(powerSystem);

                deltz = powerSystem.getMeasureSystem().getZreal().subtract(zest);

                ddeltz = getDdeltz(deltz);

                normF = ddeltz.transpose().multiply(WWInv).multiply(ddeltz);

                Matrix dx2 = dx.toRowMatrix().multiply(dx).toRowMatrix();

                if (dx2.get(0, 0) < Constants.ESTIMATOR.TOL) {

                    converged = true;

                    if (powerSystem.getOption().isVerbose()) {

                        logger.info("State estimator converged in {} iterations.", i);

                    }

                }

            }

            if (!converged && powerSystem.getOption().isVerbose()) {

                logger.info("State estimator did not converged in {} iterations.", i);

            }

//            check bad data
            Matrix WW = computeWW(WWInv);

            Matrix HTWHInv = new GaussJordanInverter(HH.transpose().multiply(WWInv).multiply(HH)).inverse();

            Matrix WR = WW.subtract(HH.multiply(HTWHInv).multiply(HH.transpose()).multiply(0.95));

            Matrix WRInvDiagVec = getDiagnalInvVector(WR);

            Matrix rN2 = ddeltz.hadamardProduct(ddeltz).hadamardProduct(WRInvDiagVec);

            double maxBad = rN2.max();

            Map<Integer, Double> baddata = badDataRecognition(rN2, oneBadAtATime);

            if (baddata.size() > 0) {

                hasBadData = true;

                converged = false;

                updateZExclude(baddata, powerSystem.getMeasureSystem().getzExcludeIds());

            }

            if (!hasBadData) {

                converged = true;

                if (powerSystem.getOption().isVerbose()) {

                    logger.info("No remaining bad data, after discarding data {} time(s).", ibad - 1);

                }

            }

            ibad++;

        }

    }

    private Matrix computeWW(Matrix WWInv) {

        Matrix ret = Matrix.zero(WWInv.rows(), WWInv.columns());

        if (WWInv.rows() != WWInv.columns()) {

            logger.error("Not a square matrix can not inverse!");

            return null;

        }

        double a;

        for (int i = 0; i < WWInv.columns(); i++) {

            a = WWInv.get(i, i);

            if (a == 0) {

                logger.error("Not a full rank matrix, can not inverse!");

                return null;

            }

            ret.set(i, i, 1 / WWInv.get(i, i));

        }

        return ret;

    }

    //    WARNNING: all exclude should be sorted in ascending order
    private void updateZExclude(Map<Integer, Double> baddata, List<Integer> zExclude) {

        List<Integer> badids = new ArrayList<Integer>();

        int excluded;

        for (Map.Entry<Integer, Double> e : baddata.entrySet()) {

            excluded = 0;

            for (Integer exczi : zExclude) {

                if (e.getKey() >= exczi) {

                    excluded++;

                }

            }

            badids.add(e.getKey() + excluded);

        }

        for (Integer badidx : badids) {

            int i = 0;

            while (i < zExclude.size()) {

                if (badidx == zExclude.get(i)) {

                    break;

                } else {

                    if (badidx < zExclude.get(i)) {

                        zExclude.add(i, badidx);

                        break;

                    }

                }

                i++;

            }

            if (i == zExclude.size()) {

                zExclude.add(badidx);

            }

        }

    }

    private Map<Integer, Double> badDataRecognition(Matrix rn2, boolean oneBadAtATime) {

        Map<Integer, Double> ret = new HashMap<Integer, Double>();

        if (oneBadAtATime) {

            double maxb = rn2.maxInColumn(0);

            for (int i = 0; i < rn2.rows(); i++) {

                if (rn2.get(i, 0) >= maxb) {

                    ret.put(i, maxb);

                }

            }

        } else {

            for (int i = 0; i < rn2.rows(); i++) {

                double tmp = rn2.get(i, 0);

                if (tmp >= badDataThreshold) {

                    ret.put(i, tmp);

                }

            }

        }

        return ret;

    }

    private Matrix getDiagnalInvVector(Matrix in) {

        int r = Math.min(in.rows(), in.columns());

        Matrix ret = Matrix.zero(r, 1);

        for (int i = 0; i < r; i++) {

            ret.set(i, 0, 1 / in.get(i, i));

        }

        return ret;

    }

    private void updateVoltage(ComplexMatrix v, Matrix vvsa, Matrix vvsm, List<Integer> excludeV) {

        int vi = 0;

        int vvi = 0;

        int exci = 0;

        while (vi < v.getRows()) {

            if (vi != excludeV.get(exci)) {

                v.getR().set(vi, 0, vvsm.get(vvi, 0) * Math.cos(vvsa.get(vvi, 0)));

                v.getI().set(vi, 0, vvsm.get(vvi, 0) * Math.sin(vvsa.get(vvi, 0)));

                vi++;

                vvi++;

            } else {

                vi++;

                exci++;

            }

        }

    }

    private Matrix getDdxa(Vector dx) {

        if (dx.length() % 2 != 0) {

            logger.error("Number of state variable should be even!");

            return null;

        }

        return dx.sliceLeft(dx.length() / 2).toColumnMatrix();

    }

    private Matrix getDdxm(Vector dx) {

        if (dx.length() % 2 != 0) {

            logger.error("Number of state variable should be even!");

            return null;

        }

        return dx.sliceRight(dx.length() / 2).toColumnMatrix();

    }

    private Matrix getHH(Matrix HF) {

        return excludeMatrix(
                HF,
                powerSystem.getMeasureSystem().getzExcludeIds(),
                powerSystem.getMeasureSystem().getStateExcludeIds()
        );

    }

    private Matrix getWWInv(Matrix WInv) {

        return excludeMatrix(
                WInv,
                powerSystem.getMeasureSystem().getzExcludeIds(),
                powerSystem.getMeasureSystem().getzExcludeIds()
        );

    }

    private Matrix getDdeltz(Matrix deltz) {

        return excludeMatrix(
                deltz,
                powerSystem.getMeasureSystem().getzExcludeIds(),
                null
        );

    }

    private ComplexMatrix getVVs(ComplexMatrix Vs) {

        return Vs.excludeSubMatrix(powerSystem.getMeasureSystem().getVbusExcludeIds(), null);

    }

    public Matrix computeEstimatedMeasurement(PowerSystem powerSystem) {

        ComplexMatrix Vsf = getVft(
                powerSystem.getMpData().getBranchData().getI(),
                powerSystem.getState(),
                powerSystem.getMpData().getBusData().getTOI());

        ComplexMatrix Vst = getVft(
                powerSystem.getMpData().getBranchData().getJ(),
                powerSystem.getState(),
                powerSystem.getMpData().getBusData().getTOI());

        ComplexMatrix Vs = powerSystem.getState();

        ComplexMatrix sfe, ste, sbuse;

        Matrix Vsa, Vsm;

        sfe = Vsf.hadamardMultiply(powerSystem.getyMatrix().getYf().multiply(Vs).conj());

        ste = Vst.hadamardMultiply(powerSystem.getyMatrix().getYt().multiply(Vs).conj());

        sbuse = Vs.hadamardMultiply(powerSystem.getyMatrix().getYbus().multiply(Vs).conj());

        Vsa = Vs.angle();

        Vsm = Vs.abs();

        return toMeasurementVector(
                powerSystem.getMeasureSystem().getNz(),
                powerSystem.getMeasureSystem().getNbr(),
                powerSystem.getMeasureSystem().getNb(),
                sfe,
                ste,
                sbuse,
                Vsa,
                Vsm);

    }

    public void print() {

        if (!powerSystem.getOption().isVerbose()) {

            return;

        }

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

        return HFull;

    }

    private void computeDSbrDv() {

        ComplexMatrix Yf, Yt, If, It, IfMatrix, ItMatrix, Vf, Vt, VfNorm, VtNorm, VfMatrix, VtMatrix;

        Yf = powerSystem.getyMatrix().getYf();

        Yt = powerSystem.getyMatrix().getYt();

        If = Yf.multiply(Vpf);

        It = Yt.multiply(Vpf);

        IfMatrix = expandVectorToDiagonalMatrix(If);

        ItMatrix = expandVectorToDiagonalMatrix(It);

        Vf = getVft(powerSystem.getMpData().getBranchData().getI(), Vpf, powerSystem.getMpData().getBusData().getTOI());

        Vt = getVft(powerSystem.getMpData().getBranchData().getJ(), Vpf, powerSystem.getMpData().getBusData().getTOI());

        VfNorm = getVft(powerSystem.getMpData().getBranchData().getI(), VpfNorm, powerSystem.getMpData().getBusData().getTOI());

        VtNorm = getVft(powerSystem.getMpData().getBranchData().getJ(), VpfNorm, powerSystem.getMpData().getBusData().getTOI());

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

        dSfDva = IfMatrix.conj().multiply(VNbrNbF).subtract(VfMatrix.multiply(Yf.multiply(VpfMatrix).conj())).multiplyJ();

        dSfDvm = VfMatrix.multiply(Yf.multiply(VpfNormMatrix).conj()).add(IfMatrix.conj().multiply(VNbrNbNormF));

        dStDva = ItMatrix.conj().multiply(VNbrNbT).subtract(VtMatrix.multiply(Yt.multiply(VpfMatrix).conj())).multiplyJ();

        dStDvm = VtMatrix.multiply(Yt.multiply(VpfNormMatrix).conj()).add(ItMatrix.conj().multiply(VNbrNbNormT));

        Sf = Vf.hadamardMultiply(If.conj());

        St = Vt.hadamardMultiply(It.conj());

    }

    private void computeDSbusDv() {

        ComplexMatrix Ibus;

        Vpf = powerSystem.getPowerFlow().getV();

        Ibus = powerSystem.getyMatrix().getYbus().multiply(Vpf);

        VpfNorm = computeVnorm(Vpf);

        VpfMatrix = expandVectorToDiagonalMatrix(Vpf);

        Ibus = expandVectorToDiagonalMatrix(Ibus);

        VpfNormMatrix = expandVectorToDiagonalMatrix(VpfNorm);

        dSbDvm = VpfMatrix.multiply(powerSystem.getyMatrix().getYbus().multiply(VpfNormMatrix).conj())
                .add(Ibus.conj().multiply(VpfNormMatrix));

        dSbDva = VpfMatrix.multiplyJ().multiply(Ibus.subtract(powerSystem.getyMatrix().getYbus().multiply(VpfMatrix)).conj());

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
    private ComplexMatrix getVft(int[] ijExternal, ComplexMatrix VMatrix, Map<Integer, Integer> TOI) {

        if (VMatrix.getCols() != 1) {

            logger.error("Not a vector!");

            return null;

        }

        int n = ijExternal.length;

        Matrix vftR = new Basic1DMatrix(n, 1);

        Matrix vftI = new Basic1DMatrix(n, 1);

        int internalIdx;

        for (int i = 0; i < n; i++) {

            internalIdx = TOI.get(ijExternal[i]) - 1;

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

    public ComplexMatrix getSf() {
        return Sf;
    }

    public ComplexMatrix getSt() {
        return St;
    }
}
