import ic.app.se.simple.cdf.BranchData;
import ic.app.se.simple.cdf.BusData;

/**
 * Created by hjh on 15-10-9.
 */
public class Test {

    public static void main(String[] args) {

        String fname;

        if (isLinux()) {

            fname = "/home/hjh/doc/powersystem/4bus/ieee4cdftest.txt";

        } else {

            fname = "F:\\projects\\data\\powersystem\\300bus\\ieee300cdf.txt";

//            fname="F:\\projects\\data\\powersystem\\14bus\\ieee14cdf.txt";

        }

        BusData busData = new BusData(fname);

        BranchData branchData = new BranchData(fname);

    }


    private static boolean isLinux() {

        return !(System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0);

    }

}
