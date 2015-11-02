package ic.app.se.mp.data;

import ic.app.se.simple.common.ComplexMatrix;
import org.la4j.Matrix;
import org.la4j.matrix.sparse.CRSMatrix;

/**
 * Created by Administrator on 2015/11/2.
 */
public class PowerFlow {

    private MPData mpData;

    private YMatrix yMatrix;

    private Matrix Vr;

    private Matrix Vi;

    private Matrix PF;

    private Matrix QF;

    private Matrix PT;

    private Matrix QT;

    private Matrix SbusP;

    private Matrix SbusQ;

    private ComplexMatrix V;

    public PowerFlow(MPData mpData, YMatrix yMatrix) {

        this.mpData = mpData;

        this.yMatrix = yMatrix;

        importV();

        importPQ();

        computeSbus();

//        print();

    }

    private void computeSbus() {

        SbusP = new CRSMatrix(mpData.getBusData().getN(), 1);

        SbusQ = new CRSMatrix(mpData.getBusData().getN(), 1);

        SbusP = yMatrix.getYG().multiply(Vr).add(yMatrix.getYB().multiply(-1).multiply(Vi));

        SbusQ = yMatrix.getYG().multiply(Vi).add(yMatrix.getYB().multiply(Vr));

        double pb, qb;

        for (int i = 0; i < mpData.getBusData().getN(); i++) {

            pb = SbusP.get(i, 0) * Vr.get(i, 0) + SbusQ.get(i, 0) * Vi.get(i, 0);

            qb = SbusP.get(i, 0) * Vi.get(i, 0) - SbusQ.get(i, 0) * Vr.get(i, 0);

            SbusP.set(i, 0, pb);

            SbusQ.set(i, 0, qb);

        }

    }

    private void importV() {

        Vr = new CRSMatrix(mpData.getBusData().getN(), 1);

        Vi = new CRSMatrix(mpData.getBusData().getN(), 1);

        int idx;

        double vm, va;

        for (int i = 0; i < mpData.getBusData().getN(); i++) {

            idx = mpData.getBusData().getTOA().get(mpData.getBusData().getTIO().get(i + 1));

            vm = mpData.getBusData().getVoltage()[idx];

            va = mpData.getBusData().getAngle()[idx];

            Vr.set(i, 0, vm * Math.cos(va));

            Vi.set(i, 0, vm * Math.sin(va));

        }

        V = new ComplexMatrix(Vr, Vi);

    }

    private void importPQ() {

        PF = new CRSMatrix(mpData.getBranchData().getN(), 1);

        QF = new CRSMatrix(mpData.getBranchData().getN(), 1);

        PT = new CRSMatrix(mpData.getBranchData().getN(), 1);

        QT = new CRSMatrix(mpData.getBranchData().getN(), 1);

        for (int i = 0; i < mpData.getBranchData().getN(); i++) {

            PF.set(i, 0, mpData.getBranchData().getPF()[i]);

            QF.set(i, 0, mpData.getBranchData().getQF()[i]);

            PT.set(i, 0, mpData.getBranchData().getPT()[i]);

            QT.set(i, 0, mpData.getBranchData().getQT()[i]);

        }

    }

    private void print() {

        System.out.print("Vr\n" + Vr.toString() + "\n");

        System.out.print("Vi\n" + Vi.toString() + "\n");

        System.out.print("SbusP\n" + SbusP.toString() + "\n");

        System.out.print("SbusQ\n" + SbusQ.toString() + "\n");

    }

    public ComplexMatrix getV() {
        return V;
    }
}
