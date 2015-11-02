package ic.app.se.mp.estimator;

import ic.app.se.simple.common.ComplexMatrix;

/**
 * Created by Administrator on 2015/11/2.
 */
public class Estimator {

    private ComplexMatrix dsbDvm;

    private ComplexMatrix dsbDva;

    private PowerSystem powerSystem;

    public Estimator(PowerSystem powerSystem) {

        this.powerSystem = powerSystem;

    }

    private void ComputeDsbus() {

        ComplexMatrix Vcp, Ibus;

        Vcp = powerSystem.getPowerFlow().getV();

        Ibus = powerSystem.getyMatrix().getYbus().multiply(Vcp);

    }

    private ComplexMatrix expandVectorToDiagonalMatrix() {

        return new ComplexMatrix(null, null);

    }

}
