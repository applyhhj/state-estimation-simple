import ic.app.se.simple.cdf.CDFData;

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

//            fpath="F:\\projects\\data\\powersystem\\14bus\\ieee14cdf.txt";

        }

        CDFData cdfData = new CDFData(fpath);

    }


    private static boolean isLinux() {

        return !(System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0);

    }

}
