package ic.app.se.mp.estimator;

import ic.app.se.simple.common.ComplexMatrix;
import org.la4j.matrix.sparse.CRSMatrix;

/**
 * Created by Administrator on 2015/11/2.
 */
public class Estimator {

    private ComplexMatrix dsbDvm;

    private ComplexMatrix dsbDva;

    private PowerSystem powerSystem;

    public Estimator(PowerSystem powerSystem) {

        this.powerSystem = powerSystem;

        computeDsbus();

//        print();

    }

    public void print() {

        dsbDvm.print("dSbus_dVm");

        dsbDva.print("dSbus_dVa");

    }

    private void computeDsbus() {

        ComplexMatrix Vcp, Ibus, Vnorm;

        Vcp = powerSystem.getPowerFlow().getV();

        Ibus = powerSystem.getyMatrix().getYbus().multiply(Vcp);

        Vnorm = computeVnorm(Vcp);

        Vcp = expandVectorToDiagonalMatrix(Vcp);

        Ibus = expandVectorToDiagonalMatrix(Ibus);

        Vnorm = expandVectorToDiagonalMatrix(Vnorm);

        dsbDvm = Vcp.multiply(powerSystem.getyMatrix().getYbus().multiply(Vnorm).conj()).add(Ibus.conj().multiply(Vnorm));

        ComplexMatrix Vcp1j = new ComplexMatrix(Vcp.getI().multiply(-1), Vcp.getR());

        dsbDva = Vcp1j.multiply(Ibus.minus(powerSystem.getyMatrix().getYbus().multiply(Vcp)).conj());

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

    public ComplexMatrix getDsbDva() {
        return dsbDva;
    }

    public ComplexMatrix getDsbDvm() {
        return dsbDvm;
    }
}
