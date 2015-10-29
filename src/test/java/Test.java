import ic.app.se.simple.cdf.CDFData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hjh on 15-10-9.
 */
public class Test {

    public static void main(String[] args) {

        String fpath;

        if (isLinux()) {

            fpath = "/home/hjh/doc/powersystem/4bus/ieee4cdftest.txt";

        } else {

            fpath = "F:\\projects\\data\\powersystem\\300bus\\ieee300cdf.txt";

        }

        CDFData cdfData = new CDFData(fpath);

        List<Integer> types = new ArrayList<Integer>();

        int type;

        for (int i = 0; i < cdfData.getBusData().getN(); i++) {

            type = cdfData.getBusData().getType()[i];

            if (type == 3) {

//                types.add(type);

                System.out.print(cdfData.getBusData().getNumber()[i] + "\n");

            }

        }

        for (int i = 0; i < cdfData.getBranchData().getN(); i++) {

            int i1 = cdfData.getBranchData().getI()[i];

            int j1 = cdfData.getBranchData().getJ()[i];

            if ((cdfData.getBusData().getVBase()[cdfData.getBusData().getTOA().get(i1)] !=
                    cdfData.getBusData().getVBase()[cdfData.getBusData().getTOA().get(j1)])
                    && cdfData.getBranchData().getType()[i] == 0) {

                System.out.print(i1 + "," + j1 + " not equal\n");

            }

        }

    }

    private static boolean isLinux() {

        return !(System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0);

    }

}
