package ic.app.se.simple.common;

/**
 * Created by hjh on 15-10-11.
 */

public class JJY{

    //        this is the column index in the matrix, it starts from zero
    private int J;

    private double GIJ;

    private double BIJ;


    public double getBIJ() {
        return BIJ;
    }

    public double getGIJ() {
        return GIJ;
    }

    public int getJ() {
        return J;
    }

    public void setBIJ(double BIJ) {
        this.BIJ = BIJ;
    }

    public void setGIJ(double GIJ) {
        this.GIJ = GIJ;
    }

    public void setJ(int j) {
        J = j;
    }

}
