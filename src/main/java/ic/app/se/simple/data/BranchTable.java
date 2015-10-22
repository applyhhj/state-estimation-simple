package ic.app.se.simple.data;

import ic.app.se.simple.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static ic.app.se.simple.common.Utils.loadSectionData;

/**
 * Created by hjh on 15-10-9.
 *
 * Page 227 ELE
 */
public class BranchTable {

    public static Logger logger= LoggerFactory.getLogger(BranchTable.class);

    private int NOE;

    private int[] i;

    private int[] j;

    private double[] rij;

    private double[] xij;

    //  if yk is below zero, it means susceptance which is -y0/2 otherwise yk is transformer ratio kij
    private double[] yk;

    public double[] getRij() {
        return rij;
    }

    public double[] getXij() {
        return xij;
    }

    public double[] getYk() {
        return yk;
    }

    public int getNOE() {
        return NOE;
    }

    public int[] getI() {
        return i;
    }

    public int[] getJ() {
        return j;
    }

    public void setI(int[] i) {
        this.i = i;
    }

    public void setJ(int[] j) {
        this.j = j;
    }

    public void setNOE(int NOE) {
        this.NOE = NOE;
    }

    public void setRij(double[] rij) {
        this.rij = rij;
    }

    public void setXij(double[] xij) {
        this.xij = xij;
    }

    public void setYk(double[] yk) {
        this.yk = yk;
    }

    public void loadData(List<String> data){

        List<String> branchData=loadSectionData(data, Constants.CDF.BRANCH_SECTION_START_KEY);

        int[] itmp=new int[branchData.size()];

        int[] jtmp=new int[branchData.size()];

        double[] rijtmp=new double[branchData.size()];

        double[] xijtmp=new double[branchData.size()];

        double[] yktmp=new double[branchData.size()];

        for (int k = 0; k < branchData.size(); k++){

            String entry=branchData.get(k);

            String col[]=entry.trim().split(" +");

            if (col.length!=Constants.CDF.BRANCH_SECTION_NUMBER_OF_COLUMN) {

                logger.error("Input data error: {}", entry);

                return;

            }

            itmp[k]=Integer.parseInt(col[0]);

            jtmp[k]=Integer.parseInt(col[1]);

            rijtmp[k]=Double.parseDouble(col[6]);

            xijtmp[k]=Double.parseDouble(col[7]);

            double y=Double.parseDouble(col[8]);

            double tk=Double.parseDouble(col[14]);

            if (tk>0){

                yktmp[k]=tk;

            }else if (y>0){

                yktmp[k]=-y;

            }else {

                yktmp[k]=0.0;

            }

        }

//        these are numbers
        setI(itmp);

        setJ(jtmp);

        setRij(rijtmp);

        setXij(xijtmp);

        setYk(yktmp);

        setNOE(branchData.size());

    }

}
