package ic.app.se.simple.cdf;

/**
 * Created by Administrator on 2015/10/28.
 */
public class CDFData {

    private BranchData branchData;

    private BusData busData;

    public CDFData(String filepath) {

        branchData = new BranchData(filepath);

        busData = new BusData(filepath);

    }

    public BranchData getBranchData() {
        return branchData;
    }

    public BusData getBusData() {
        return busData;
    }
}


