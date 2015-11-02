package ic.app.se.mp.data;

import ic.app.se.simple.common.ComplexMatrix;
import ic.app.se.simple.common.GB;
import org.la4j.Matrix;
import org.la4j.matrix.sparse.CRSMatrix;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/11/1.
 */
public class YMatrix {

    private List<GB> Ytt;

    private List<GB> Yff;

    private List<GB> Ytf;

    private List<GB> Yft;

    private Matrix cf;

    private Matrix ct;

    private Matrix YShG;

    private Matrix YShB;

    private Matrix YfG;

    private Matrix YfB;

    private Matrix YtG;

    private Matrix YtB;

    private Matrix YG;

    private Matrix YB;

    private ComplexMatrix Ybus;

    private MPData mpData;

    public YMatrix(MPData mpData) {

        this.mpData = mpData;

        Ytt = new ArrayList<GB>();

        Yff = new ArrayList<GB>();

        Ytf = new ArrayList<GB>();

        Yft = new ArrayList<GB>();

        computeYMatrix();

//        print();

    }

    private void computeYMatrix() {

        double Gtt, Btt, Gff, Bff, Gft, Bft, Gtf, Btf, Gs, Bs, r, x, zm2, Bc, t, tsh;

        for (int i = 0; i < mpData.getBranchData().getN(); i++) {

            r = mpData.getBranchData().getR()[i];

            x = mpData.getBranchData().getX()[i];

            Bc = mpData.getBranchData().getB()[i];

            t = mpData.getBranchData().getRatio()[i];

            tsh = mpData.getBranchData().getAngle()[i];

            if (t <= 0) {

                t = 1;

            }

            zm2 = r * r + x * x;

            Gs = r / zm2;

            Bs = -x / zm2;

            Gtt = Gs;

            Btt = Bc / 2 + Bs;

            Gff = Gtt / t / t;

            Bff = Btt / t / t;

            if (tsh == 0) {

                Gtf = Gft = -Gs / t;

                Btf = Bft = -Bs / t;

            } else {

                Gtf = -(Gs * Math.cos(tsh) + Bs * Math.sin(tsh)) / t;

                Btf = (Gs * Math.sin(tsh) - Bs * Math.cos(tsh)) / t;

                Gft = (Bs * Math.sin(tsh) - Gs * Math.cos(tsh)) / t;

                Bft = -(Gs * Math.sin(tsh) + Bs * Math.cos(tsh)) / t;

            }

            Ytt.add(new GB(Gtt, Btt));

            Yff.add(new GB(Gff, Bff));

            Yft.add(new GB(Gft, Bft));

            Ytf.add(new GB(Gtf, Btf));

        }

        getConnectionMatrix();

        getYfYt();

        getYSparseSh();

        YG = cf.transpose().multiply(YfG).add(ct.transpose().multiply(YtG)).add(YShG);

        YB = cf.transpose().multiply(YfB).add(ct.transpose().multiply(YtB)).add(YShB);

        Ybus = new ComplexMatrix(YG, YB);

    }

    private void print() {

        System.out.print("cf\n" + cf.toString() + "\n");

        System.out.print("ct\n" + ct.toString() + "\n");

        System.out.print("YShG\n" + YShG.toString() + "\n");

        System.out.print("YShB\n" + YShB.toString() + "\n");

        System.out.print("YfG\n" + YfG.toString() + "\n");

        System.out.print("YfB\n" + YfB.toString() + "\n");

        System.out.print("YtG\n" + YtG.toString() + "\n");

        System.out.print("YtB\n" + YtB.toString() + "\n");

        System.out.print("YG\n" + YG.toString() + "\n");

        System.out.print("YB\n" + YB.toString() + "\n");

    }

    private void getYSparseSh() {

        int nbu = mpData.getBusData().getN();

        int idx;

        YShG = new CRSMatrix(nbu, nbu);

        YShB = new CRSMatrix(nbu, nbu);

        for (int i = 0; i < nbu; i++) {

//            this is the index, however i is index should convert to internal bus number
            idx = mpData.getBusData().getTOA().get(mpData.getBusData().getTIO().get(i + 1));

            YShG.set(i, i, mpData.getBusData().getGs()[idx] / mpData.getSbase());

            YShB.set(i, i, mpData.getBusData().getBs()[idx] / mpData.getSbase());

        }

    }

    private void getYfYt() {

        int nbr = mpData.getBranchData().getN();

        int nbu = mpData.getBusData().getN();

        int idx;

        YfG = new CRSMatrix(nbr, nbu);

        YfB = new CRSMatrix(nbr, nbu);

        YtG = new CRSMatrix(nbr, nbu);

        YtB = new CRSMatrix(nbr, nbu);

        for (int i = 0; i < nbr; i++) {

//            convert to internal number
            idx = mpData.getBusData().getTOI().get(mpData.getBranchData().getI()[i]) - 1;

            YfG.set(i, idx, Yff.get(i).getG());

            YfB.set(i, idx, Yff.get(i).getB());

            YtG.set(i, idx, Ytf.get(i).getG());

            YtB.set(i, idx, Ytf.get(i).getB());

        }

        for (int i = 0; i < nbr; i++) {

            idx = mpData.getBusData().getTOI().get(mpData.getBranchData().getJ()[i]) - 1;

            YfG.set(i, idx, Yft.get(i).getG());

            YfB.set(i, idx, Yft.get(i).getB());

            YtG.set(i, idx, Ytt.get(i).getG());

            YtB.set(i, idx, Ytt.get(i).getB());

        }

    }

    private void getConnectionMatrix() {

        int nbr = mpData.getBranchData().getN();

        int nbu = mpData.getBusData().getN();

        cf = new CRSMatrix(nbr, nbu);

        ct = new CRSMatrix(nbr, nbu);

        for (int i = 0; i < nbr; i++) {

//            convert external bus number to internal bus number, convert to index
            cf.set(i, mpData.getBusData().getTOI().get(mpData.getBranchData().getI()[i]) - 1, 1);

            ct.set(i, mpData.getBusData().getTOI().get(mpData.getBranchData().getJ()[i]) - 1, 1);

        }

    }

    public Matrix getYB() {
        return YB;
    }

    public Matrix getYG() {
        return YG;
    }

    public ComplexMatrix getYbus() {
        return Ybus;
    }
}
