package ic.app.se.mp.estimator;

import ic.app.se.mp.data.MPData;
import ic.app.se.mp.data.PowerFlow;
import ic.app.se.mp.data.YMatrix;

/**
 * Created by Administrator on 2015/11/2.
 */
public class PowerSystem {

    private String mpCaseDataPath;

    private MPData mpData;

    private YMatrix yMatrix;

    private PowerFlow powerFlow;

    public PowerSystem(String mpCaseDataPath) {

        this.mpCaseDataPath = mpCaseDataPath;

        initData();

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
}
