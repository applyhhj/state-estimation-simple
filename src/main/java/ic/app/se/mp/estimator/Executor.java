package ic.app.se.mp.estimator;

import ic.app.se.simple.common.EstimationOption;

import static ic.app.se.simple.common.Utils.isLinux;

/**
 * Created by Administrator on 2015/11/2.
 */
public class Executor {

    public static void main(String[] args) throws InterruptedException {

        String fpath;

        if (isLinux()) {

            fpath = "/home/hjh/doc/powersystem/4bus/ieee4cdftest.txt";

        } else {

            fpath = "F:\\projects\\data\\matpower-data-process\\data\\case14.txt";

        }

        EstimationOption option = new EstimationOption();

        option.setVerbose(false);

        option.setDebug(true);

        PowerSystem powerSystem = new PowerSystem(fpath, option);

        int i = 0;

        long start;

        while (i++ < 3) {

            start = System.currentTimeMillis();

            powerSystem.run();

            System.out.printf("\nEstimate %d duration: %d ms", i, System.currentTimeMillis() - start);

            powerSystem.printStateInExternalInPolarDegree();

            Thread.sleep(500);

        }

        System.out.print("Done!\n");

    }

}
