package ic.app.se.mp.estimator;

import ic.app.se.simple.common.ComplexMatrix;
import ic.app.se.simple.common.Utils;
import org.la4j.Matrix;
import org.la4j.matrix.sparse.CRSMatrix;

import java.util.*;

import static ic.app.se.simple.common.Utils.MatrixExtend.insertMatrix;
import static ic.app.se.simple.common.Utils.MatrixExtend.toMeasurementVector;

/**
 * Created by Administrator on 2015/11/2.
 */
public class MeasureSystem {

    private PowerSystem powerSystem;

    private ComplexMatrix sf;

    private ComplexMatrix st;

    private ComplexMatrix sbus;

    private Matrix Vpfm;

    private Matrix Vpfa;

    private Matrix zreal;

    private Matrix zm;

    private Matrix sigma;

    private Matrix WInv;

    private double fullscale;

    private int nbr;

    private int nb;

    private int nz;

    private List<Integer> excludeIdxSf;

    private List<Integer> excludeIdxSt;

    private List<Integer> VbusExcludeIds;

    private List<Integer> zExcludeIds;

    private List<Integer> stateExcludeIds;

    private Random random;

    public MeasureSystem(PowerSystem powerSystem) {

        this.powerSystem = powerSystem;

        fullscale = 30;

        random = new Random();

        excludeIdxSf = new ArrayList<Integer>();

        excludeIdxSt = new ArrayList<Integer>();

        VbusExcludeIds = new ArrayList<Integer>();

        zExcludeIds = new ArrayList<Integer>();

        stateExcludeIds = new ArrayList<Integer>();

        sf = powerSystem.getEstimator().getSf();

        st = powerSystem.getEstimator().getSt();

        sbus = powerSystem.getPowerFlow().getSbus();

        Vpfm = powerSystem.getPowerFlow().getVm();

        Vpfa = powerSystem.getPowerFlow().getVa();

        nb = powerSystem.getMpData().getBusData().getN();

        nbr = powerSystem.getMpData().getBranchData().getN();

        nz = 4 * nbr + 4 * nb;

        zm = Matrix.zero(nz, 1);

        importTrueMeasurement();

        generateSigma();

        computeWInv();

        computeExcludeIndices();

//        print();

    }

    public void print() {

        if (powerSystem.getOption().isVerbose()) {

            System.out.print("\nReal measurement:\n" + zreal.toString());

        }

    }

    private void importTrueMeasurement() {

        zreal = toMeasurementVector(nz, nbr, nb, sf, st, sbus, Vpfa, Vpfm);

    }

    private void generateSigma() {

        sigma = Matrix.zero(nz, 1);

        sigma = insertMatrix(sigma, sf.abs().multiply(0.02).add(Matrix.unit(nbr, 1).multiply(0.0052).multiply(fullscale)));

        sigma = insertMatrix(sigma, st.abs().multiply(0.02).add(Matrix.unit(nbr, 1).multiply(0.0052).multiply(fullscale)), nbr, 0);

        sigma = insertMatrix(sigma, sbus.abs().multiply(0.02).add(Matrix.unit(nb, 1).multiply(0.0052).multiply(fullscale)), 2 * nbr, 0);

        sigma = insertMatrix(sigma, Matrix.unit(nb, 1).multiply(0.2 * Math.PI / 180 * 3), 2 * nbr + nb, 0);

        sigma = insertMatrix(sigma, sf.abs().multiply(0.02).add(Matrix.unit(nbr, 1).multiply(0.0052).multiply(fullscale)), 2 * (nbr + nb), 0);

        sigma = insertMatrix(sigma, st.abs().multiply(0.02).add(Matrix.unit(nbr, 1).multiply(0.0052).multiply(fullscale)), 3 * nbr + 2 * nb, 0);

        sigma = insertMatrix(sigma, sbus.abs().multiply(0.02).add(Matrix.unit(nb, 1).multiply(0.0052).multiply(fullscale)), 4 * nbr + 2 * nb, 0);

        sigma = insertMatrix(sigma, Vpfm.multiply(0.02).add(Matrix.unit(nb, 1).multiply(1.1 * 0.0052)), 4 * nbr + 3 * nb, 0);

        sigma = sigma.multiply(1 / 3.0);

    }

    public void measure() {

        for (int i = 0; i < nz; i++) {

            zm.set(i, 0, getMeasureI(i));

        }

    }

    private double getMeasureI(int i) {

        if (powerSystem.getOption().isDebug()) {

            return zreal.get(i, 0);

        }

        return random.nextGaussian() * sigma.get(i, 0) + zreal.get(i, 0);

    }

    private void computeWInv() {

        WInv = new CRSMatrix(nz, nz);

        double sig;

        for (int i = 0; i < nz; i++) {

            sig = sigma.get(i, 0);

            WInv.set(i, i, 1 / sig / sig);

        }

    }

    private void computeExcludeIndices() {

        int refNumI = powerSystem.getMpData().getBusData().getNrefI();

        int[] I = powerSystem.getMpData().getBranchData().getI();

        int[] J = powerSystem.getMpData().getBranchData().getJ();

        Map<Integer, Integer> TOI = powerSystem.getMpData().getBusData().getTOI();

        for (int i = 0; i < nbr; i++) {

            if (TOI.get(I[i]) == refNumI) {

                excludeIdxSf.add(i);

            }

            if (TOI.get(J[i]) == refNumI) {

                excludeIdxSt.add(i);

            }

        }

        VbusExcludeIds.add(refNumI - 1);

        zExcludeIds.clear();

        stateExcludeIds.clear();

        int exIdx;

        for (int i = 0; i < excludeIdxSf.size(); i++) {

            exIdx = excludeIdxSf.get(i);

//            Pf
            zExcludeIds.add(exIdx);

//            Qf
            zExcludeIds.add(exIdx + 2 * (nbr + nb));

        }

        for (int i = 0; i < excludeIdxSt.size(); i++) {

            exIdx = excludeIdxSt.get(i);

//            Pt
            zExcludeIds.add(exIdx + nbr);

//            Qt
            zExcludeIds.add(exIdx + 3 * nbr + 2 * nb);

        }

        for (int i = 0; i < VbusExcludeIds.size(); i++) {

            exIdx = VbusExcludeIds.get(i);

//            Pbus
            zExcludeIds.add(exIdx + 2 * nbr);

//            Qbus
            zExcludeIds.add(exIdx + 4 * nbr + 2 * nb);

//            Va
            zExcludeIds.add(exIdx + 2 * nbr + nb);

//            Vm
            zExcludeIds.add(exIdx + 4 * nbr + 3 * nb);

            stateExcludeIds.add(exIdx);

            stateExcludeIds.add(nb + exIdx);

        }

        Collections.sort(zExcludeIds, Utils.intComparator);

        Collections.sort(stateExcludeIds, Utils.intComparator);

        Collections.sort(VbusExcludeIds, Utils.intComparator);

    }

    public int getNz() {
        return nz;
    }

    public int getNb() {
        return nb;
    }

    public int getNbr() {
        return nbr;
    }

    public List<Integer> getzExcludeIds() {
        return zExcludeIds;
    }

    public List<Integer> getStateExcludeIds() {
        return stateExcludeIds;
    }

    public Matrix getWInv() {
        return WInv;
    }

//    public Matrix getZreal() {
//        return zreal;
//    }

    public List<Integer> getVbusExcludeIds() {
        return VbusExcludeIds;
    }

    public Matrix getZm() {
        return zm;
    }
}
