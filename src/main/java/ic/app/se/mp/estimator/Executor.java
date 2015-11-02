package ic.app.se.mp.estimator;

import static ic.app.se.simple.common.Utils.isLinux;

/**
 * Created by Administrator on 2015/11/2.
 */
public class Executor {

    public static void main(String[] args) {

        String fpath;

        if (isLinux()) {

            fpath = "/home/hjh/doc/powersystem/4bus/ieee4cdftest.txt";

        } else {

            fpath = "F:\\projects\\data\\matpower-data-process\\data\\case14.txt";

        }

        PowerSystem powerSystem = new PowerSystem(fpath);

        System.out.print("Done!\n");

    }

}
