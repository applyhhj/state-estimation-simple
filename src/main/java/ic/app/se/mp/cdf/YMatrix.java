package ic.app.se.mp.cdf;

import ic.app.se.simple.common.GB;
import org.la4j.Matrix;
import org.la4j.matrix.sparse.CRSMatrix;

import java.util.List;

/**
 * Created by Administrator on 2015/11/1.
 */
public class YMatrix {

    private List<GB> Ysh;

    private List<GB> Ytt;

    private List<GB> Yff;

    private List<GB> Ytf;

    private List<GB> Yft;

    private List<GB> Yf;

    private List<GB> Yt;

    private List<GB> YBus;

    private Matrix cf;

    private Matrix ct;

    private Matrix YfG;

    private Matrix YfB;

    private Matrix YtG;

    private Matrix YtB;

    private Matrix YSparseShG;

    private Matrix YSparseShB;

    private Matrix YG;

    private Matrix YB;

    private MPData mpData;

    public YMatrix(MPData mpData) {

        this.mpData = mpData;

    }

    private void computeYMatrix() {

        double Gtt, Btt, Gff, Bff, Gft, Bft, Gtf, Btf, Gs, Bs, r, x, zm2, Bc, t, tsh, gs, bs;

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

//                convert to radius
                tsh = tsh / 180 * Math.PI;

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

        YG = cf.transpose().multiply(YfG).add(ct.transpose().multiply(YtG)).add(YSparseShG);

        YB = cf.transpose().multiply(YfB).add(ct.transpose().multiply(YtB)).add(YSparseShB);

    }

    private void getYSparseSh() {

        int nbu = mpData.getBusData().getN();

        int idx;

        YSparseShG = new CRSMatrix(nbu, nbu);

        YSparseShB = new CRSMatrix(nbu, nbu);

        for (int i = 0; i < nbu; i++) {

            idx = mpData.getBusData().getTOA().get(mpData.getBusData().getTIO().get(i));

            YSparseShG.set(i, i, mpData.getBusData().getGs()[idx]);

            YSparseShB.set(i, i, mpData.getBusData().getBs()[idx]);

        }

    }

    private void getYfYt() {

        int nbr = mpData.getBranchData().getN();

        int nbu = mpData.getBusData().getN();

        YfG = new CRSMatrix(nbr, nbu);

        YfB = new CRSMatrix(nbr, nbu);

        YtG = new CRSMatrix(nbr, nbu);

        YtB = new CRSMatrix(nbr, nbu);

        for (int i = 0; i < nbr; i++) {

            YfG.set(i, mpData.getBranchData().getI()[i], Yff.get(i).getG());

            YfB.set(i, mpData.getBranchData().getI()[i], Yff.get(i).getB());

            YtG.set(i, mpData.getBranchData().getI()[i], Ytf.get(i).getG());

            YtB.set(i, mpData.getBranchData().getI()[i], Ytf.get(i).getB());

        }

        for (int i = 0; i < nbr; i++) {

            YfG.set(i, mpData.getBranchData().getJ()[i], Yft.get(i).getG());

            YfB.set(i, mpData.getBranchData().getJ()[i], Yft.get(i).getB());

            YtG.set(i, mpData.getBranchData().getJ()[i], Ytt.get(i).getG());

            YtB.set(i, mpData.getBranchData().getJ()[i], Ytt.get(i).getB());

        }

    }

    private void getConnectionMatrix() {

        int nbr = mpData.getBranchData().getN();

        int nbu = mpData.getBusData().getN();

        cf = new CRSMatrix(nbr, nbu);

        ct = new CRSMatrix(nbr, nbu);

        for (int i = 0; i < nbr; i++) {

//            convert external bus number to internal bus number
            cf.set(i, mpData.getBusData().getTOI().get(mpData.getBranchData().getI()[i]), 1);

            ct.set(i, mpData.getBusData().getTOI().get(mpData.getBranchData().getJ()[i]), 1);

        }

    }

}
