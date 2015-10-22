package ic.app.se.simple.data;

import ic.app.se.simple.common.JJY;
import ic.app.se.simple.common.JYL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hjh on 15-10-11.
 */
public class MatrixY {

    //    all get set method uses internal bus number as input!!!!!
    //    however sparse matrix stores index

    public Logger logger= LoggerFactory.getLogger(MatrixY.class);

    private int KPQ;

    private BranchTable branchTable;

    private BusNumbers busNumbers;

//    upper triangular matrix
//    start address
    private List<Integer> IY;

//    value and column, this is the index it is the internal bus number minus 1
    private List<JJY> jjyList;

//   lower triangular matrix
//    start address
    private List<Integer> IYL;

    private List<JYL> jylList;

    private double[] GII;

    private double[] BII;

//    Y is a NOB*NOB matrix, so matrix order equals NOB
    private int order;

    public MatrixY(BranchTable branchTable, BusNumbers busNumbers,int kpq){

        KPQ=kpq;

        this.branchTable=branchTable;

        this.busNumbers=busNumbers;

        setOrder();

        jjyList=new ArrayList<JJY>(branchTable.getNOE());

        IY=new ArrayList<Integer>();

        jylList=new ArrayList<JYL>();

        IYL=new ArrayList<Integer>();

        GII=new double[busNumbers.getNOB()];

        BII=new double[busNumbers.getNOB()];

        compute();

        printMatrix();

    }

    public void compute(){

        computeJJY();

        computeJYL();

        computeYMatrix();

    }

//    upper triangle matrix
    private void computeJJY(){

        int idx=0;

        boolean started;

//        use internal bus number. starts from 1
        for (int i = 1; i < busNumbers.getNOB() + 1; i++) {

            started=false;

            for (int j =i+ 1; j < busNumbers.getNOB() + 1; j++) {

                for (int k = 0; k < branchTable.getNOE(); k++) {

                    if ((busNumbers.getTIO().get(i)==branchTable.getI()[k]&&
                            busNumbers.getTIO().get(j)==branchTable.getJ()[k])||
                            (busNumbers.getTIO().get(j)==branchTable.getI()[k]&&
                                    busNumbers.getTIO().get(i)==branchTable.getJ()[k])){

                        JJY jjy=new JJY();

//                        stores index
                        jjy.setJ(j-1);

                        jjyList.add(idx++,jjy);

                        if (!started){

                            started=true;

                            IY.add(idx-1);

                        }

                    }

                }

            }

//            no element, assign the former index
            if (!started){

                IY.add(idx==0?0:idx-1);

            }

        }

        IY.add(idx==0?0:idx-1);

    }

    private void computeJYL(){

        int idx=0;

        boolean started;

//        use internal bus number. starts from 1
        for (int i = 1; i < busNumbers.getNOB() + 1; i++) {

            started=false;

            for (int j =1; j < i; j++) {

                for (int k = 0; k < branchTable.getNOE(); k++) {

                    if ((busNumbers.getTIO().get(i)==branchTable.getI()[k]&&
                            busNumbers.getTIO().get(j)==branchTable.getJ()[k])||
                            (busNumbers.getTIO().get(j)==branchTable.getI()[k]&&
                                    busNumbers.getTIO().get(i)==branchTable.getJ()[k])){

                        JYL jyl=new JYL();

//                        stores index, not bus number
                        jyl.setJ(j-1);

                        jylList.add(idx++,jyl);

                        if (!started){

                            started=true;

                            IYL.add(idx-1);

                        }

                    }

                }

            }

//            no element, assign the former index
            if (!started){

                IYL.add(idx==0?0:idx-1);

            }

        }

        IYL.add(idx==0?0:idx-1);

    }

    private void computeYMatrix(){

        double R,X,K,G,B,Z2;

        int I,J;

        for (int i = 0; i < branchTable.getNOE(); i++) {

            R=branchTable.getRij()[i];

            X=branchTable.getXij()[i];

            I=busNumbers.getTOI().get(branchTable.getI()[i]);

            J=busNumbers.getTOI().get(branchTable.getJ()[i]);

            if (KPQ==1){

                Z2=R*R+X*X;

                G=R/Z2;

                B=-X/Z2;

                K=branchTable.getYk()[i];

            }else {

                G=K=0;

                B=-1/X;

            }

            if (I==J){

                setGIJ(I,I,getGIJ(I,I)+G);

                setBIJ(I,I,getBIJ(I,I)+B-K);

            }else {

                if (K>0){

                    setGIJ(J,J,getGIJ(J,J)+G);

                    setBIJ(J,J,getBIJ(J,J)+B);

                    setGIJ(I,I,getGIJ(I,I)+G/K/K);

                    setBIJ(I,I,getBIJ(I,I)+B/K/K);

                    setGIJ(I,J,getGIJ(I,J)-G/K);

                    setBIJ(I,J,getBIJ(I,J)-B/K);

                }else {

                    setGIJ(I,J,getGIJ(I,J)-G);

                    setBIJ(I,J,getBIJ(I,J)-B);

                    setGIJ(J,J,getGIJ(J,J)+G);

                    setBIJ(J,J,getBIJ(J,J)+B-K);

                    setGIJ(I,I,getGIJ(I,I)+G);

                    setBIJ(I,I,getBIJ(I,I)+B-K);

                }

            }

        }

    }

    private void setOrder(){

        order=busNumbers.getNOB();

    }

    private boolean numberValid(int i,int j){

        if (i<1||i>order||j<1||j>order){

            return false;

        }

        return true;

    }

    public Double getGIJ(int i,int j){

        if (!numberValid(i,j)){

            logger.error("Input bus number invalid!!");

            return null;

        }

        if (i==j){

            return GII[i-1];

        }else {

            JJY jjytmp= getNonDigGB(i, j);

            if (jjytmp==null){

                return null;

            }else {

                return jjytmp.getGIJ();

            }

        }

    }

    public Double getBIJ(int i,int j){

        if (!numberValid(i,j)){

            logger.error("Input bus number invalid!!");

            return null;

        }

        if (i==j){

            return BII[i-1];

        }else {

            JJY jjytmp= getNonDigGB(i, j);

            if (jjytmp==null){

                return null;

            }else {

                return jjytmp.getBIJ();

            }

        }

    }

    public void setGIJ(int i,int j,double value){

        if (!numberValid(i,j)){

            logger.error("Input bus number invalid!!");

            return;

        }

        if (i==j){

            GII[i-1]=value;

        }else {

            JJY jjytmp= getNonDigGB(i, j);

            if (jjytmp!=null) {

                jjytmp.setGIJ(value);

            }

        }

    }

    public void setBIJ(int i,int j,double value){

        if (!numberValid(i,j)){

            logger.error("Input bus number invalid!!");

            return;

        }

        if (i==j){

            BII[i-1]=value;

        }else {

            JJY jjytmp= getNonDigGB(i, j);

            if (jjytmp!=null) {

                jjytmp.setBIJ(value);

            }

        }

    }

    private JJY getNonDigGB(int i, int j){

        if (i==j){

            return null;

        }else if (i>j){

            int tmp=i;

            i=j;

            j=tmp;

        }

        for (int k = IY.get(i-1); k <= IY.get(Math.min(i,busNumbers.getNOB()-1)); k++) {

            if(jjyList.get(k).getJ()==j-1){

                return jjyList.get(k);

            }

        }

        return null;

    }

    public void printMatrix(){

        for (int i = 0; i < busNumbers.getNOB(); i++) {

            for (int j = 0; j < busNumbers.getNOB(); j++) {

                if (getGIJ(i + 1, j + 1)!=null||getBIJ(i + 1, j + 1)!=null) {

                    System.out.printf("%7.4f + j%7.4f,",getGIJ(i + 1, j + 1),getBIJ(i + 1, j + 1));

                }else {

                        System.out.print("                  ,");

                }

            }

            System.out.print("\n");

        }

        System.out.print("\n-------------------------------\n");

    }

    public int getKPQ() {
        return KPQ;
    }

    public int getOrder() {
        return order;
    }

    public List<Integer> getIY() {
        return IY;
    }

    public List<Integer> getIYL() {
        return IYL;
    }

    public List<JJY> getJjyList() {
        return jjyList;
    }

    public List<JYL> getJylList() {
        return jylList;
    }

}
