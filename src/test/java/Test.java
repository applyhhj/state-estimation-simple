import ic.app.se.simple.estimate.PowerGrid;

/**
 * Created by hjh on 15-10-9.
 */
public class Test {

    public static void main(String[] args) {

        PowerGrid powerGrid=new PowerGrid();

        powerGrid.initData("/home/hjh/doc/powersystem/4bus/ieee4cdftest.txt");

        int i=0;

        while (i++<10){

            powerGrid.measure();

            powerGrid.estimate();

        }

    }

}
