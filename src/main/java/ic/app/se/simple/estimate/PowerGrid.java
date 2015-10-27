package ic.app.se.simple.estimate;

import ic.app.se.simple.data.*;

import java.util.List;

import static ic.app.se.simple.common.Utils.readStringFromFile;

/**
 * Created by hjh on 15-10-9.
 */
public class PowerGrid {

    private String id;

    private BranchTable branchTable;

    private MeasurementTable measurementTable;

    private BusNumbers busNumbers;

    private MatrixY matrixYQ;

    private MatrixY matrixYP;

    private MatrixH matrixHQ;

    private MatrixH matrixHP;

    private MatrixHTH matrixHTHQ;

    private MatrixHTH matrixHTHP;

    private EstimatedState state;

    private Estimator estimator;

    private int KPQ;

    public PowerGrid() {

        KPQ = 0;

        branchTable = new BranchTable();

        measurementTable = new MeasurementTable();

    }

    public void measure() {

        measurementTable.generateMeasurement(true);

    }

    public void estimate() {

//        state.reset();

        estimator.estimate();

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void initData(String filepath) {

        List<String> content = readStringFromFile(filepath);

        branchTable.loadData(content);

        measurementTable.loadData(content);

        busNumbers = new BusNumbers(branchTable, measurementTable);

        matrixYP = new MatrixY(branchTable, busNumbers, 0);

        matrixYQ = new MatrixY(branchTable, busNumbers, 1);

        matrixHQ = new MatrixH(branchTable, busNumbers, measurementTable, matrixYQ);

        matrixHP = new MatrixH(branchTable, busNumbers, measurementTable, matrixYP);

        matrixHTHQ = new MatrixHTH(busNumbers, matrixHQ);

        matrixHTHP = new MatrixHTH(busNumbers, matrixHP);

        state = new EstimatedState(busNumbers.getNOB(), measurementTable.getNOM(), measurementTable.getV0(), 0);

        state.setTIO(busNumbers.getTIO());

        estimator = new Estimator(this);

    }

    public MeasurementTable getMeasurementTable() {
        return measurementTable;
    }

    public EstimatedState getState() {
        return state;
    }

    public BusNumbers getBusNumbers() {
        return busNumbers;
    }

    public BranchTable getBranchTable() {
        return branchTable;
    }

    public MatrixY getMatrixY() {

        if (KPQ == 1) {

            return matrixYQ;

        } else {

            return matrixYP;

        }

    }

    public MatrixH getMatrixH() {

        if (KPQ == 1) {

            return matrixHQ;

        } else {

            return matrixHP;

        }

    }

    public MatrixHTH getMatrixHTH() {

        if (KPQ == 1) {

            return matrixHTHQ;

        } else {

            return matrixHTHP;

        }
    }

    public void setKPQ(int KPQ) {
        this.KPQ = KPQ;
    }

    public int getKPQ() {
        return KPQ;
    }
}
