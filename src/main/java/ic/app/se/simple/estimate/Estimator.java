package ic.app.se.simple.estimate;

import ic.app.se.simple.common.ColumnAndValue;
import ic.app.se.simple.common.Constants;
import ic.app.se.simple.common.SparseMatrix;
import ic.app.se.simple.data.*;

import java.util.ArrayList;
import java.util.List;

import static ic.app.se.simple.common.Utils.gaussElimination;

/**
 * Created by hjh on 15-10-19.
 */
public class Estimator {

    private int kpq;

    private boolean kp;

    private boolean kq;

    private int it;

    private int itLimit;

    private PowerGrid powerGrid;

    private MeasurementTable measurementTable;

    private EstimatedState state;

    private BusNumbers busNumbers;

    private MatrixY Y;

    private BranchTable branchTable;

    private List<Integer> bdm;

    private List<Double> bd;

    private double jx;

    private double jxb;

    private List<Double> x;

    private SparseMatrix HTRI;


    public Estimator(PowerGrid powerGrid){

        this.powerGrid=powerGrid;

        this.itLimit=100;

        this.measurementTable=powerGrid.getMeasurementTable();

        this.state=powerGrid.getState();

        this.busNumbers=powerGrid.getBusNumbers();

        this.Y=powerGrid.getMatrixY();

        this.branchTable=powerGrid.getBranchTable();

        bdm=new ArrayList<Integer>();

        bd=new ArrayList<Double>();

        jx=0;

        jxb=0;

        x=new ArrayList<Double>();

        this.HTRI=powerGrid.getMatrixH().getHTRI();

    }

    public void estimate(){

        it=0;

        kp=false;

        kq=false;

        while (it++<itLimit){

            powerGrid.getMatrixY().setKPQ(0);

            computeState();

            if (absMax(x)<Constants.ESTIMATOR.ERR_THETA){

                kp=true;

                if (kq){

                    break;

                }

            }else {

                correctState(0);

            }

            powerGrid.getMatrixY().setKPQ(1);

            computeState();

            if (absMax(x)<Constants.ESTIMATOR.ERR_V){

                kq=true;

                if (kp){

                    break;

                }

            }else {

                correctState(1);

            }

        }

    }

    private void computeState(){

        List<ColumnAndValue> uju=new ArrayList<ColumnAndValue>();

        computeResidual();

        zeroResidualRecognition();

        computeX();

        gaussElimination(powerGrid.getMatrixHTH().getMatrix(),uju);

        solveLinearFunction(uju);

    }

    private double absMax(List<Double> list){

        double ret=-1,absv;

        for (Double v:list){

            absv=Math.abs(v);

            if (absv>ret){

                ret=absv;

            }

        }

        return ret;

    }

    private void correctState(int isv){

        List<Double> st;

        if (isv==1){

            st=state.getVe();

        }else {

            st=state.getAe();

        }

        for (int i = 0; i < x.size(); i++) {

            st.set(i,st.get(i)+x.get(i));

        }

    }

    private void computeResidual(){

        int type,loc,iint,jint;

        double G,B,vi,vj,thetaij,sinij,cosij,yk,h;

        state.getRes().clear();

        for (int n = 0; n <measurementTable.getNOM(); n++) {

            h=0;

            type=measurementTable.getType()[n];

            if (type>=4){

                loc=measurementTable.getLocation()[n];

//                branch use natural order, loc-1 is the array index, here we get the index
                iint=busNumbers.getTOI().get(branchTable.getI()[loc-1])-1;

                jint=busNumbers.getTOI().get(branchTable.getJ()[loc-1])-1;

                yk=branchTable.getYk()[loc-1];

                G=Y.getGIJ(iint,jint);

                B=Y.getBIJ(iint,jint);

                vi=state.getVe().get(iint);

                vj=state.getVe().get(jint);

                thetaij=state.getAe().get(iint)-state.getAe().get(jint);

                sinij=Math.sin(thetaij);

                cosij=Math.cos(thetaij);

                switch (type){

                    case 4:

                        if (yk<0){

                            h=vi*(vi*G-vj*(G*cosij+B*sinij));

                        }else {

                            h=-vi*vj*B*sinij/yk;

                        }

                        break;

                    case 5:

                        if (yk<0){

                            h=-vi*(vi*(B+yk)-vj*(B*cosij-G*sinij));

                        }else {

                            h=(-vi/yk+vj*cosij)*B*vj/yk;

                        }

                        break;

                    case 6:

                        if (yk<0){

                            h=vj*(vj*G-vi*(G*cosij-B*sinij));

                        }else {

                            h=vi*vj*B*sinij/yk;

                        }

                        break;

                    case 7:

                        if (yk<0){

                            h=vj*(-vj*(B+yk)+vi*(G*sinij+B*cosij));

                        }else {

                            h=(vi*cosij/yk-vj)*B*vj;

                        }

                        break;

                }

            }else {

                iint=busNumbers.getTOI().get(measurementTable.getLocation()[n]);

                if (type<2){

                    h=state.getVe().get(iint);

                }else {

                    for (int i = Y.getIY().get(iint); i < Y.getIY().get(iint+1); i++) {

                        jint=Y.getJjyList().get(i).getJ();

                        vi=state.getVe().get(iint);

                        vj=state.getVe().get(jint);

                        thetaij=state.getAe().get(iint)-state.getAe().get(jint);

                        sinij=Math.sin(thetaij);

                        cosij=Math.cos(thetaij);

                        G=Y.getJjyList().get(i).getGIJ();

                        B=Y.getJjyList().get(i).getBIJ();

                        if (type==3){

                            h=h+vi*vj*(G*sinij-B*cosij);

                        }else {

                            h=h+vi*vj*(G*cosij+B*sinij);

                        }

                    }

                }

            }

            state.getRes().set(n,measurementTable.getZ()[n]-h);

        }

    }

//    simple bad data recognition program
    private void zeroResidualRecognition(){

        double r;

        bd.clear();

        bdm.clear();

        for (int i = 0; i < state.getRes().size(); i++) {

            r=state.getRes().get(i);

            jxb=jxb+r*r;

            if (Math.abs(r)< Constants.ESTIMATOR.ERR_REC){

                jx=jx+r*r;

            }else {

                bdm.add(i);

                bd.add(r);

                state.getRes().set(i,0.0);

            }

        }

    }

    private void computeX(){

        double xi;

        double v0=measurementTable.getV0();

        x.clear();

        for (int m = 0; m <HTRI.getRowStartAddress().size()-1; m++) {

            xi=0;

            for (int n = HTRI.getRowStartAddress().get(m); n < HTRI.getRowStartAddress().get(m+1); n++) {

                xi=xi+HTRI.getColumnAndValues().get(n).getValue()*
                        state.getRes().get(HTRI.getColumnAndValues().get(n).getColumn());


            }

            x.add(xi/v0/v0);

        }

    }

    private void solveLinearFunction(List<ColumnAndValue> uju){

        int ku=0,j,i;

        double d;

        for (i = 0; i < x.size(); i++) {

            d=uju.get(ku).getValue();

            while (uju.get(++ku).getColumn()!=0){

                j=uju.get(ku).getColumn();

                x.set(j,x.get(j)-uju.get(ku).getValue()*x.get(i));

            }

            x.set(i,x.get(i)/d);

        }

        while (--i>=0){

            while (uju.get(--ku).getColumn()!=0){

                j=uju.get(ku).getColumn();

                x.set(i,x.get(i)-x.get(j)*uju.get(ku).getValue());

            }

        }

    }

}
