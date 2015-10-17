package ic.app.se.simple.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hjh on 15-10-12.
 */
public class MatrixH {

    public static Logger logger= LoggerFactory.getLogger(MatrixH.class);

    private List<Integer> HI;

    private List<Double> H;

    private List<Integer> MH;

    private List<Integer> IHT;

    private List<Integer> MIHT;

    private List<Double> MHT;

    private BranchTable branchTable;

    private BusNumbers busNumbers;

    private MeasurementTable measurementTable;

    private MatrixY matrixY;

    private int c;

    private int r;

    public MatrixH(BranchTable branchTable, BusNumbers busNumbers, MeasurementTable measurementTable, MatrixY matrixY) {

        this.branchTable = branchTable;

        this.busNumbers=busNumbers;

        this.measurementTable = measurementTable;

        this.matrixY = matrixY;

        HI = new ArrayList<Integer>();

        H = new ArrayList<Double>();

        MH = new ArrayList<Integer>();

        IHT=new ArrayList<Integer>();

        MIHT=new ArrayList<Integer>();

        MHT=new ArrayList<Double>();

        computeHMatrix();

        computeHTMatrix();

        printHTMatrix();

        System.out.print("----------------\n\n");

        printHMatrix();

        System.out.print("-----------------\n\n");

    }

    public void computeHMatrix() {

        MH.add(0);

         for (int i = 0; i < measurementTable.getNOM(); i++) {

            int type=measurementTable.getType()[i];

            if (matrixY.getKPQ()==1){

                switch (type){

                    case 0:

                    case 1:{

                        int I= getBusOutNumber(i);

                        HI.add(I);

                        H.add(1/measurementTable.getV0());

                        break;

                    }

                    case 3:{

                        int I= getBusOutNumber(i);

                        List<B> bb=getBFromYMatrixForRowI(I);

                        for (int j = 0; j < bb.size(); j++) {

                            HI.add(bb.get(j).getJ());

                            H.add(bb.get(j).getB());

                        }

                        break;

                    }

                    case 2:

                    case 4:

                    case 6:{

                        break;

                    }

                    case 5:

                    case 7:{

//                        branch number
                        int L=measurementTable.getLocation()[i];

                        int I=busNumbers.getTOI().get(branchTable.getI()[L-1]);

                        int J=busNumbers.getTOI().get(branchTable.getJ()[L-1]);

                        double R=branchTable.getRij()[L-1];

                        double X=branchTable.getXij()[L-1];

                        double YK=branchTable.getYk()[L-1];

                        double B=-X/(R*R+X*X);

                        if (YK>0){

                            if (type==5){

                                H.add(-(2*B/YK-B)/YK);

                                H.add(B/YK);

                            }else {

                                H.add(B/YK);

                                H.add(B/YK-2*B);

                            }

                        }else {

                            if (type==5){

                                H.add(B-2*(B-YK));

                                H.add(B);

                            }else {

                                H.add(B);

                                H.add(B-2*YK);

                            }

                        }

                        HI.add(I);

                        HI.add(J);

                        break;

                    }

                    default:{

                        logger.error("Unrecognized type {}!!",type);

                    }

                }

            }else {

                switch (type){

                    case 0:

                    case 1:

                    case 3:

                    case 5:

                    case 7:{

                        break;

                    }

                    case 2:{

                        int I= getBusOutNumber(i);

                        List<B> bb=getBFromYMatrixForRowI(I);

                        for (int j = 0; j < bb.size(); j++) {

                            HI.add(bb.get(j).getJ());

                            H.add(bb.get(j).getB());

                        }

                        break;

                    }

                    case 4:

                    case 6:{

                        int L=measurementTable.getLocation()[i];

                        int I=busNumbers.getTOI().get(branchTable.getI()[L-1]);

                        int J=busNumbers.getTOI().get(branchTable.getJ()[L-1]);

                        double X=branchTable.getXij()[L-1];

                        double B=-1/X;

                        if (type==4){

                            H.add(-B);

                            H.add(B);

                        }else {

                            H.add(B);

                            H.add(-B);

                        }

                        HI.add(I);

                        HI.add(J);

                    }

                }

            }

            MH.add(H.size());

        }

    }

    public void computeHTMatrix(){

        IHT.clear();

        int former=0;

        for (int i = 0; i <busNumbers.getNOB(); i++) {

            IHT.add(0);

            if (i!=0) {

                IHT.set(i,IHT.get(i-1)+former);

            }

            former=0;

            for (int j = 0; j < HI.size(); j++) {

                if (HI.get(j)==i+1) {

                    former++;

                }

            }

            for (int m = 0; m < measurementTable.getNOM(); m++) {

                Double data=getHmi(m+1,i+1);

                if (data!=null){

                    MIHT.add(m+1);

                    MHT.add(data);

                }

            }

        }

        IHT.add(busNumbers.getNOB(),IHT.get(busNumbers.getNOB()-1)+former);

    }

    private int getBusOutNumber(int i){

        return busNumbers.getTOI().get(measurementTable.getLocation()[i]);

    }

    private List<B> getBFromYMatrixForRowI(int I){

        List<B> ret=new ArrayList<B>();

        for (int j = 1; j <=busNumbers.getNOB(); j++) {

            Double Bvalue= matrixY.getBIJ(I,j);

            if (Bvalue!=null) {

                B b = new B();

                b.setJ(j);

                b.setB(Bvalue);

                ret.add(b);

            }

        }

        return ret;

    }

//  use number not array index as input
    public Double getHmi(int m,int i){

        for (int j = MH.get(m-1); j <MH.get(m); j++) {

//            this array stores number
            if (HI.get(j)==i){

                return H.get(j);

            }

        }

        return null;

    }

    //  use number not array index as input
    public Double getHTim(int i,int m){

        for (int j = IHT.get(i-1); j <IHT.get(i); j++) {

//            this array stores number
            if (MIHT.get(j)==m){

                return MHT.get(j);

            }

        }

        return null;

    }

    private class B{

        private int J;

        private double B;

        public int getJ() {
            return J;
        }

        public double getB() {
            return B;
        }

        public void setJ(int j) {
            J = j;
        }

        public void setB(double b) {
            B = b;
        }
    }

    public void printHMatrix(){

        for (int m = 0; m < measurementTable.getNOM(); m++) {

            for (int i = 0; i < busNumbers.getNOB(); i++) {

                Double data=getHmi(m+1,i+1);

                if (data!=null){

                    System.out.printf("%9.2f,",data);

                }else {

                    System.out.print("         ,");

                }

            }

            System.out.print("\n");

        }

    }

    public void printHTMatrix(){

        for (int i = 0; i < busNumbers.getNOB(); i++) {

            for (int m = 0; m < measurementTable.getNOM(); m++) {

                Double data=getHTim(i + 1, m + 1);

                if (data!=null){

                    System.out.printf("%9.2f,", data);

                }else {

                    System.out.print("         ,");

                }

            }

            System.out.print("\n");

        }

    }

    public List<Double> getH() {
        return H;
    }

    public List<Double> getMHT() {
        return MHT;
    }

    public List<Integer> getIHT() {
        return IHT;
    }

    public List<Integer> getHI() {
        return HI;
    }

    public List<Integer> getMIHT() {
        return MIHT;
    }

    public List<Integer> getMH() {
        return MH;
    }

    public int getHR() {
        return MH.size()-1;
    }

    public int getHC() {
        return IHT.size()-1;
    }

    public int getHTR(){
        return IHT.size()-1;
    }

    public int getHTC(){
        return MH.size()-1;
    }
}
