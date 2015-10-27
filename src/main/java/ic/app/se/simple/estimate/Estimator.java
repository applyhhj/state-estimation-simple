package ic.app.se.simple.estimate;

import ic.app.se.simple.common.ColumnAndValue;
import ic.app.se.simple.common.Constants;
import ic.app.se.simple.common.SparseMatrix;
import ic.app.se.simple.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static ic.app.se.simple.common.Utils.gaussElimination;

/**
 * Created by hjh on 15-10-19.
 */
public class Estimator {

    public static Logger logger = LoggerFactory.getLogger(Estimator.class);

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

    private List<ColumnAndValue> ujuP;

    private List<ColumnAndValue> ujuQ;

    private List<ColumnAndValue> uju;

    private int kpq;

    public Estimator(PowerGrid powerGrid) {

        this.powerGrid = powerGrid;

        this.itLimit = 100;

        this.measurementTable = powerGrid.getMeasurementTable();

        this.state = powerGrid.getState();

        this.busNumbers = powerGrid.getBusNumbers();

        this.branchTable = powerGrid.getBranchTable();

        bdm = new ArrayList<Integer>();

        bd = new ArrayList<Double>();

        x = state.getX();

        jx = 0;

        jxb = 0;

        ujuP = new ArrayList<ColumnAndValue>();

        ujuQ = new ArrayList<ColumnAndValue>();

        computeUju();

    }

    private void computeUju() {

        powerGrid.setKPQ(0);

        powerGrid.getMatrixHTH().getMatrix().getUpperTriangularMatrix().print();

        gaussElimination(powerGrid.getMatrixHTH().getMatrix().getUpperTriangularMatrix(), ujuP);

        powerGrid.setKPQ(1);

        gaussElimination(powerGrid.getMatrixHTH().getMatrix().getUpperTriangularMatrix(), ujuQ);

    }

    private void getMatrix() {

        if (kpq != 0 && kpq != 1) {

            logger.error("KPQ {} input invalid!", kpq);

            return;

        }

        powerGrid.setKPQ(kpq);

        Y = powerGrid.getMatrixY();

        HTRI = powerGrid.getMatrixH().getHTRI();

        if (kpq == 0) {

            uju = ujuP;

        } else {

            uju = ujuQ;

        }

    }

    public void estimate() {

        it = 0;

        kp = false;

        kq = false;

        while (it++ < itLimit) {

            kpq = 0;

//            p
            getMatrix();

            computeState();

            if (absMax(x) < Constants.ESTIMATOR.ERR_THETA) {

                kp = true;

                if (kq) {

                    break;

                }

            } else {

                correctState(powerGrid.getKPQ());

            }

            kpq = 1;

//            q
            getMatrix();

            computeState();

            if (absMax(x) < Constants.ESTIMATOR.ERR_V) {

                kq = true;

                if (kp) {

                    break;

                }

            } else {

                correctState(powerGrid.getKPQ());

            }

            state.print();

        }

        System.out.print("Iteration " + it + "\n");

    }

    private void computeState() {

        computeResidual();

//        zeroResidualRecognition();

        computeX();

        solveLinearFunction(uju);

    }

    private double absMax(List<Double> list) {

        double ret = -1, absv;

        for (Double v : list) {

            absv = Math.abs(v);

            if (absv > ret) {

                ret = absv;

            }

        }

        return ret;

    }

    private void correctState(int isv) {

        List<Double> st;

        if (isv == 1) {

            st = state.getVe();

        } else {

            st = state.getAe();

        }

//        ignore reference bus
        for (int i = 0; i < x.size() - 1; i++) {

//            it seems we get the opposite operator pp. 80
            st.set(i, st.get(i) - x.get(i));

        }

    }

    private void computeResidual() {

        int type, loc, iint, jint;

        double G, B, vi, vj, thetaij, sinij, cosij, yk, h;

        for (int n = 0; n < measurementTable.getNOM(); n++) {

            h = 0;

            type = measurementTable.getType()[n];

            if (type == 0 || type % 2 != kpq) {

                state.getRes().set(n, 0.0);

                continue;

            }

            if (type >= 4) {

                loc = measurementTable.getLocation()[n];

//                branch use natural order, loc-1 is the array index
                iint = busNumbers.getTOI().get(branchTable.getI()[loc - 1]);

                jint = busNumbers.getTOI().get(branchTable.getJ()[loc - 1]);

                yk = branchTable.getYk()[loc - 1];

//                here iint jint are the bus numbers
                G = -Y.getGIJ(iint, jint);

                B = -Y.getBIJ(iint, jint);

                if (yk > 0) {

                    B = B * yk;

                }

                vi = state.getVe().get(iint - 1);

                vj = state.getVe().get(jint - 1);

                thetaij = state.getAe().get(iint - 1) - state.getAe().get(jint - 1);

                sinij = Math.sin(thetaij);

                cosij = Math.cos(thetaij);

                switch (type) {

                    case 4:

                        if (yk < 0) {

                            h = vi * (vi * G - vj * (G * cosij + B * sinij));

                        } else {

                            h = -vi * vj * B * sinij / yk;

                        }

                        break;

                    case 5:

                        if (yk < 0) {

                            h = -vi * (vi * (B - yk) - vj * (B * cosij - G * sinij));

                        } else {

                            h = (-vi / yk + vj * cosij) * B * vi / yk;

                        }

                        break;

                    case 6:

                        if (yk < 0) {

                            h = vj * (vj * G - vi * (G * cosij - B * sinij));

                        } else {

                            h = vi * vj * B * sinij / yk;

                        }

                        break;

                    case 7:

                        if (yk < 0) {

                            h = vj * (-vj * (B - yk) + vi * (G * sinij + B * cosij));

                        } else {

                            h = (vi * cosij / yk - vj) * B * vj;

                        }

                        break;

                }

            } else {

//                use index
                iint = busNumbers.getTOI().get(measurementTable.getLocation()[n]) - 1;

                if (type < 2) {

                    h = state.getVe().get(iint);

                } else {

                    List<Integer> rowidx = getYRow(iint);

                    boolean up = false;

                    int idx;

                    vi = state.getVe().get(iint);

                    for (int i = 0; i < rowidx.size(); i++) {

                        idx = rowidx.get(i);

                        if (idx == -1) {

                            jint = iint;

//                            here iint jint are indices
                            G = Y.getGIJ(iint + 1, iint + 1);

                            B = Y.getBIJ(iint + 1, iint + 1);

                            up = true;

                        } else {

                            if (up) {

                                jint = Y.getJjyList().get(idx).getJ();

                                G = Y.getJjyList().get(idx).getGb().getG();

                                B = Y.getJjyList().get(idx).getGb().getB();

                            } else {

                                jint = Y.getJylList().get(idx).getJ();

                                G = Y.getJylList().get(idx).getGb().getG();

                                B = Y.getJylList().get(idx).getGb().getB();

                            }

                        }

                        vj = state.getVe().get(jint);

                        thetaij = state.getAe().get(iint) - state.getAe().get(jint);

                        sinij = Math.sin(thetaij);

                        cosij = Math.cos(thetaij);

                        if (type == 3) {

                            h = h + vi * vj * (G * sinij - B * cosij);

                        } else {

                            h = h + vi * vj * (G * cosij + B * sinij);

                        }

                    }

                }

            }

            state.getRes().set(n, measurementTable.getZ()[n] - h);

        }

    }

    //    including reference bus
    private List<Integer> getYRow(int iint) {

        List<Integer> rowidx = new ArrayList<Integer>();

        for (int i = Y.getIYL().get(iint); i < Y.getIYL().get(iint + 1); i++) {

//            if (Y.getJylList().get(i).getJ()==Y.getOrder()-1){
//
//                continue;
//
//            }

            rowidx.add(i);

        }

//        diagonal element
        rowidx.add(-1);

        for (int i = Y.getIY().get(iint); i < Y.getIY().get(iint + 1); i++) {

//            if (Y.getJylList().get(i).getJ()==Y.getOrder()-1){
//
//                continue;
//
//            }

            rowidx.add(i);

        }

        return rowidx;

    }

    //    simple bad data recognition program
    private void zeroResidualRecognition() {

        if (it < 2) {

            return;

        }

        double r;

        bd.clear();

        bdm.clear();

        for (int i = 0; i < state.getRes().size(); i++) {

            r = state.getRes().get(i);

            jxb = jxb + r * r;

            if (Math.abs(r) < Constants.ESTIMATOR.ERR_REC) {

                jx = jx + r * r;

            } else {

                bdm.add(i);

                bd.add(r);

                state.getRes().set(i, 0.0);

            }

        }

    }

    private void computeX() {

        double xi;

        double v0 = measurementTable.getV0();

        x.clear();

        for (int m = 0; m < HTRI.getRowStartAddress().size() - 1; m++) {

            xi = 0;

            for (int n = HTRI.getRowStartAddress().get(m); n < HTRI.getRowStartAddress().get(m + 1); n++) {

                xi = xi + HTRI.getColumnAndValues().get(n).getValue() *
                        state.getRes().get(HTRI.getColumnAndValues().get(n).getColumn());


            }

            if (kpq == 0) {

                x.add(xi / v0 / v0);

            } else {

                x.add(xi / v0);

            }
        }

    }

    private void solveLinearFunction(List<ColumnAndValue> uju) {

        int ku = 0, j, i;

        double d;

//        ignore reference bus
        for (i = 0; i < x.size() - 1; i++) {

            d = uju.get(ku).getValue();

            while (++ku < uju.size() && uju.get(ku).getColumn() != 0) {

                j = uju.get(ku).getColumn();

                x.set(j, x.get(j) - uju.get(ku).getValue() * x.get(i));

            }

            x.set(i, x.get(i) / d);

        }

        while (--i >= 0) {

            while (--ku >= 0 && uju.get(ku).getColumn() != 0) {

                j = uju.get(ku).getColumn();

                x.set(i, x.get(i) - x.get(j) * uju.get(ku).getValue());

            }

        }

    }

}
