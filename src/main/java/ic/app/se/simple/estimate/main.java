package ic.app.se.simple.estimate;

/**
 * Created by hjh on 15-10-21.
 */
public class main {

    public static void main(String[] args) {

        PowerGrid powerGrid=new PowerGrid();

        powerGrid.initData("/home/hjh/doc/powersystem/4bus/ieee4cdftest.txt");

        int i=0;

        while (i++<10){

            powerGrid.measure();

            powerGrid.estimate();

            System.out.print("Estimation "+i);

        }

    }

}
