package ic.app.se.mp.estimator;

import ic.app.se.mp.data.MPData;
import ic.app.se.mp.data.PowerFlow;
import ic.app.se.mp.data.YMatrix;
import ic.app.se.simple.common.ComplexMatrix;
import ic.app.se.simple.common.EstimationOption;
import ic.app.se.simple.common.Utils;
import org.la4j.Matrix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2015/11/2.
 */
public class PowerSystem {

    private String mpCaseDataPath;

    private MPData mpData;

    private YMatrix yMatrix;

    private PowerFlow powerFlow;

    private Estimator estimator;

    private MeasureSystem measureSystem;

    private ComplexMatrix state;

    private EstimationOption option;

    public PowerSystem(String mpCaseDataPath, EstimationOption option) {

        if (option != null) {

            this.option = option;

        } else {

            this.option = new EstimationOption();

        }

        this.mpCaseDataPath = mpCaseDataPath;

        initData();

        this.estimator = new Estimator(this);

        this.measureSystem = new MeasureSystem(this);

    }

    public PowerSystem(String mpCaseDataPath) {

        this(mpCaseDataPath, null);

    }

    private void initData() {

        mpData = new MPData(mpCaseDataPath);

        yMatrix = new YMatrix(mpData);

        powerFlow = new PowerFlow(mpData, yMatrix);

//        flat start, include reference bus
        state = new ComplexMatrix(Matrix.unit(mpData.getBusData().getN(), 1), Matrix.zero(mpData.getBusData().getN(), 1));

    }

    public void run() {

        measureSystem.measure();

        estimator.estimate();

    }

    public MPData getMpData() {
        return mpData;
    }

    public YMatrix getyMatrix() {
        return yMatrix;
    }

    public PowerFlow getPowerFlow() {
        return powerFlow;
    }

    public Estimator getEstimator() {
        return estimator;
    }

    public MeasureSystem getMeasureSystem() {
        return measureSystem;
    }

    public ComplexMatrix getState() {
        return state;
    }

    public EstimationOption getOption() {
        return option;
    }

    public ComplexMatrix printStateInExternalInPolarDegree() {

        if (!estimator.isConverged()) {

            System.out.print("Not converged!!");

            return null;

        }

        System.out.print("\nBusNum       Vm(p.u.)        Va(degree)\n");

        ComplexMatrix stateExtPolarDeg = new ComplexMatrix(state.abs(), state.angle().multiply(180 / Math.PI));

        List<Integer> sortExternalBusNum = new ArrayList<Integer>();

        for (Integer i : mpData.getBusData().getTOI().keySet()) {

            sortExternalBusNum.add(i);

        }

        Collections.sort(sortExternalBusNum, Utils.intComparator);

        int internalNum;

        for (int i = 0; i < sortExternalBusNum.size(); i++) {

            internalNum = mpData.getBusData().getTOI().get(sortExternalBusNum.get(i));

            System.out.printf("%5d %8.4f   %8.4f\n", sortExternalBusNum.get(i),
                    stateExtPolarDeg.getR().get(internalNum - 1, 0),
                    stateExtPolarDeg.getI().get(internalNum - 1, 0));

        }

        return stateExtPolarDeg;

    }
}
