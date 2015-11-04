package ic.app.se.mp.estimator;

import ic.app.se.mp.data.MPData;
import ic.app.se.mp.data.PowerFlow;
import ic.app.se.mp.data.YMatrix;
import ic.app.se.simple.common.ComplexMatrix;
import ic.app.se.simple.common.EstimationOption;
import org.la4j.Matrix;

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

    public PowerSystem(String mpCaseDataPath) {

        this.mpCaseDataPath = mpCaseDataPath;

        option = new EstimationOption();

        initData();

        this.estimator = new Estimator(this);

        this.measureSystem = new MeasureSystem(this);

//        flat start, include reference bus
        state = new ComplexMatrix(Matrix.unit(mpData.getBusData().getN(), 1), Matrix.zero(mpData.getBusData().getN(), 1));

    }

    private void initData() {

        mpData = new MPData(mpCaseDataPath);

        yMatrix = new YMatrix(mpData);

        powerFlow = new PowerFlow(mpData, yMatrix);

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
}
