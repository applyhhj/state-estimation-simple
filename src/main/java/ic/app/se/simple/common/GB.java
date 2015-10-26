package ic.app.se.simple.common;

/**
 * Created by hjh on 15-10-26.
 */
public class GB {

    private double G;

    private double B;

    public GB(double g,double b){

        this.G=g;

        this.B=b;

    }

    public GB(){

    }

    public double getB() {
        return B;
    }

    public double getG() {
        return G;
    }

    public void setB(double b) {
        B = b;
    }

    public void setG(double g) {
        G = g;
    }
}
