package ic.app.se.simple.estimate;

import ic.app.se.simple.data.*;

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

    public Estimator(PowerGrid powerGrid){

        this.powerGrid=powerGrid;

        this.itLimit=100;

        this.measurementTable=powerGrid.getMeasurementTable();

        this.state=powerGrid.getState();

        this.busNumbers=powerGrid.getBusNumbers();

        this.Y=powerGrid.getMatrixY();

        this.branchTable=powerGrid.getBranchTable();

    }

    public void estimate(){

        it=1;

        kp=false;

        kq=false;

        while (it++<itLimit){

            kpq=0;



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



                }

            }

        }

    }

}
