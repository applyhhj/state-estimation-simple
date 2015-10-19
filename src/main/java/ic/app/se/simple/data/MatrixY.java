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

    public Logger logger= LoggerFactory.getLogger(MatrixY.class);

    private int KPQ;

    private BranchTable branchTable;

    private BusNumbers busNumbers;

    private List<JJY> jjyList;

//    start address
    private List<Integer> IY;

    private List<JYL> jylList;

//    start address
    private List<Integer> IYL;

    private double[] GII;

    private double[] BII;

    public MatrixY(BranchTable branchTable, BusNumbers busNumbers){

        KPQ=1;

        this.branchTable=branchTable;

        this.busNumbers=busNumbers;

        jjyList=new ArrayList<JJY>(branchTable.getNOE());

        IY=new ArrayList<Integer>();

        jylList=new ArrayList<JYL>();

        IYL=new ArrayList<Integer>();

        GII=new double[busNumbers.getNOB()];

        BII=new double[busNumbers.getNOB()];

        computeJJY();

        computeJYL();

        computeYMatrix();

//        printMatrix();

    }

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

//    to get the element use the internal number-1
    public Double getGIJ(int i,int j){

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

        if (i==j){

            BII[i-1]=value;

        }else {

            JJY jjytmp= getNonDigGB(i, j);

            if (jjytmp!=null) {

                jjytmp.setBIJ(value);

            }

        }

    }

    public JJY getNonDigGB(int i, int j){

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

//        logger.error("Y{}{} not found!",i,j);

        return null;

    }

//    test
    public void printMatrix(){

        for (int i = 0; i < busNumbers.getNOB(); i++) {

            for (int j = 0; j < busNumbers.getNOB(); j++) {

                if (getNonDigGB(i + 1, j + 1)!=null) {

                    System.out.print(getGIJ(i + 1, j + 1) + " " + getBIJ(i + 1, j + 1) + "j,    ");

                }else {

                    if (i==j){

                        System.out.print(getGIJ(i + 1, j + 1) + " " + getBIJ(i + 1, j + 1) + "j,    ");

                    }else {

                        System.out.print("   ,    ");

                    }

                }

            }

            System.out.print("\n");

        }

    }

    public int getKPQ() {
        return KPQ;
    }
}
