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

        for (int i = 0; i < cdfData.getBranchData().getN(); i++) {

            type = cdfData.getBranchData().getType()[i];

            if (!types.contains(type)) {

                types.add(type);

                System.out.print(type + "\n");

            }

        }

    }

    private static boolean isLinux() {

        return !(System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0);

    }

}
