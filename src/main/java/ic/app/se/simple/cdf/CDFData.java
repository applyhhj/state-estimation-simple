package ic.app.se.simple.cdf;

import java.util.List;

import static ic.app.se.simple.common.Utils.readStringFromFile;

/**
 * Created by Administrator on 2015/10/28.
 */
public class CDFData {

    private BranchData branchData;

    private BusData busData;

    private double sbase;

    public CDFData(String filepath) {

        sbase = importSbase(filepath);

        branchData = new BranchData(filepath);

        busData = new BusData(filepath);

        busData.reorderBusNumbers(branchData);

    }

    private double importSbase(String filepath) {

        List<String> fileContent = readStringFromFile(filepath);

        String entry;

//        for 300 bus system
        if (fileContent.get(0).indexOf("TAPE") >= 0) {

            entry = fileContent.get(1);

        } else {

            entry = fileContent.get(0);

        }

        return Double.parseDouble(entry.substring(31, 37));

    }

    public double getSbase() {
        return sbase;
    }

    public BranchData getBranchData() {
        return branchData;
    }

    public BusData getBusData() {
        return busData;
    }
}


