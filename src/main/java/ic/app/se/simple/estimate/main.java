package ic.app.se.simple.estimate;

/**
 * Created by hjh on 15-10-21.
 */
public class main {

    public static void main(String[] args) {

        PowerGrid powerGrid = new PowerGrid();

        String fname;

        if (isLinux()){

            fname="/home/hjh/doc/powersystem/4bus/ieee4cdftest.txt";

        }else {

            fname="";

        }

        System.exit(0);

        powerGrid.initData(fname);

        int i = 0;

        while (i++ < 10) {

            powerGrid.measure();

            System.out.print("\nEstimation " + i + "\n");

            powerGrid.estimate();

        }

    }

    private static boolean isLinux() {

        return !(System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0);

    }

}
