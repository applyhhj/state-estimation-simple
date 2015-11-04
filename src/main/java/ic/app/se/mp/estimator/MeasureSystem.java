package ic.app.se.mp.estimator;

import ic.app.se.simple.common.ComplexMatrix;
import org.la4j.Matrix;
import org.la4j.matrix.sparse.CRSMatrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static ic.app.se.simple.common.Utils.MatrixExtend.insertMatrix;

/**
 * Created by Administrator on 2015/11/2.
 */
public class MeasureSystem {

    private PowerSystem powerSystem;

    private ComplexMatrix sf;

    private ComplexMatrix st;

    private ComplexMatrix sbus;

    private Matrix Vm;

    private Matrix Va;

    private Matrix z;

    private Matrix zm;

    private Matrix sigma;

    private Matrix WInv;

    private double fullscale;

    private int nbr;

    private int nb;

    private int nz;

    private List<Integer> excludeIdxSf;

    private List<Integer> excludeIdxSt;

    private List<Integer> excludeIdxV;

    private List<Integer> zExclude;

    private List<Integer> stateExclude;

    private Random random;

    public MeasureSystem(PowerSystem powerSystem) {

        this.powerSystem = powerSystem;

        fullscale = 30;

        random = new Random();

        excludeIdxSf = new ArrayList<Integer>();

        excludeIdxSt = new ArrayList<Integer>();

        excludeIdxV = new ArrayList<Integer>();

        zExclude = new ArrayList<Integer>();

        stateExclude = new ArrayList<Integer>();

        sf = powerSystem.getEstimator().getSf();

        st = powerSystem.getEstimator().getSt();

        sbus = powerSystem.getPowerFlow().getSbus();

        Vm = powerSystem.getPowerFlow().getVm();

        Va = powerSystem.getPowerFlow().getVa();

        nb = powerSystem.getMpData().getBusData().getN();

        nbr = powerSystem.getMpData().getBranchData().getN();

        nz = 4 * nbr + 4 * nb;

        zm = Matrix.zero(nz, 1);

        importTrueMeasurement();

        computeWInv();

        print();

    }

    public void print() {

        System.out.print("\nReal measurement:\n" + z.toString());

    }

    private void importTrueMeasurement() {

        z = Matrix.zero(nz, 1);

        z = insertMatrix(z, sf.getR());

        z = insertMatrix(z, st.getR(), nbr, 0);

        z = insertMatrix(z, sbus.getR(), 2 * nbr, 0);

        z = insertMatrix(z, Va, 2 * nbr + nb, 0);

        z = insertMatrix(z, sf.getI(), 2 * (nbr + nb), 0);

        z = insertMatrix(z, st.getI(), 3 * nbr + 2 * nb, 0);

        z = insertMatrix(z, sbus.getI(), 4 * nbr + 2 * nb, 0);

        z = insertMatrix(z, Vm, 4 * nbr + 3 * nb, 0);

    }

    private void generateSigma() {

        sigma = Matrix.zero(nz, 1);

        sigma.insert(sf.abs().multiply(0.02).add(Matrix.unit(nbr, 1).multiply(0.0052).multiply(fullscale)));

        sigma.insert(st.abs().multiply(0.02).add(Matrix.unit(nbr, 1).multiply(0.0052).multiply(fullscale)), nbr, 0);

        sigma.insert(sbus.abs().multiply(0.02).add(Matrix.unit(nb, 1).multiply(0.0052).multiply(fullscale)), 2 * nbr, 0);

        sigma.insert(Matrix.unit(nb, 1).multiply(0.2 * Math.PI / 180 * 3), 2 * nbr + nb, 0);

        sigma.insert(sf.abs().multiply(0.02).add(Matrix.unit(nbr, 1).multiply(0.0052).multiply(fullscale)), 2 * (nbr + nb), 0);

        sigma.insert(st.abs().multiply(0.02).add(Matrix.unit(nbr, 1).multiply(0.0052).multiply(fullscale)), 3 * nbr + 2 * nb, 0);

        sigma.insert(sbus.abs().multiply(0.02).add(Matrix.unit(nb, 1).multiply(0.0052).multiply(fullscale)), 4 * nbr + 2 * nb, 0);

        sigma.insert(Vm.multiply(0.02).add(Matrix.unit(nb, 1).multiply(1.1 * 0.0052)), 4 * nbr + 3 * nb, 0);

        sigma.multiply(1 / 3.0);

    }

    public void measure() {

        for (int i = 0; i < nz; i++) {

            zm.set(i, 0, getMeasureI(i));

        }

    }

    private double getMeasureI(int i) {

        return random.nextGaussian() * sigma.get(i, 0) + z.get(i, 0);

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

        excludeIdxV.add(refNumI - 1);

        zExclude.clear();

        stateExclude.clear();

        int exIdx;

        for (int i = 0; i < excludeIdxSf.size(); i++) {

            exIdx = excludeIdxSf.get(i);

//            Pf
            zExclude.add(exIdx);

//            Qf
            zExclude.add(exIdx + 2 * (nbr + nb));

        }

        for (int i = 0; i < excludeIdxSt.size(); i++) {

            exIdx = excludeIdxSt.get(i);

//            Pt
            zExclude.add(exIdx + nbr);

//            Qt
            zExclude.add(exIdx + 3 * nbr + 2 * nb);

        }

        for (int i = 0; i < excludeIdxV.size(); i++) {

            exIdx = excludeIdxV.get(i);

//            Pbus
            zExclude.add(exIdx + 2 * nbr);

//            Qbus
            zExclude.add(exIdx + 4 * nbr + 2 * nb);

//            Va
            zExclude.add(exIdx + 2 * nbr + nb);

//            Vm
            zExclude.add(exIdx + 4 * nbr + 3 * nb);

            stateExclude.add(exIdx);

        }

    }

}
