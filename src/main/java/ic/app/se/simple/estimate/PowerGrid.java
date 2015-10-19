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

    private MatrixY matrixY;

    private MatrixH matrixH;

    private MatrixHTH matrixHTH;

    private EstimatedState state;

    public PowerGrid(){

        branchTable=new BranchTable();

        measurementTable=new MeasurementTable();

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean loadCDFDataFromFile(String filepath){

        List<String> content=readStringFromFile(filepath);

        branchTable.loadData(content);

        measurementTable.loadData(content);

        busNumbers=new BusNumbers(branchTable,measurementTable);

        matrixY =new MatrixY(branchTable,busNumbers);

        matrixH =new MatrixH(branchTable,busNumbers,measurementTable, matrixY);

        matrixHTH=new MatrixHTH(matrixH,busNumbers.getNOB());

        return true;

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

    public MatrixY getMatrixY() {
        return matrixY;
    }

    public BranchTable getBranchTable() {
        return branchTable;
    }
}
