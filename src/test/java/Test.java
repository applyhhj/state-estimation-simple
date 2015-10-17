import ic.app.se.simple.estimate.PowerGrid;

/**
 * Created by hjh on 15-10-9.
 */
public class Test {

    public static void main(String[] args) {

        PowerGrid powerGrid=new PowerGrid();

        powerGrid.loadCDFDataFromFile("/home/hjh/doc/powersystem/4bus/ieee4cdftest.txt");
    }

}
