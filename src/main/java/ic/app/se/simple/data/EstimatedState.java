package ic.app.se.simple.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hjh on 15-10-18.
 */
public class EstimatedState {

    private List<Double> ae;

    private List<Double> ve;

    private List<Double> x;

    private List<Double> res;

    private double initV;

    private double initA;

    public EstimatedState(int NOB,int NOM,double initV,double initA){

        ae=new ArrayList<Double>();

        ve=new ArrayList<Double>();

        x=new ArrayList<Double>();

        res=new ArrayList<Double>();

        this.initA=initA;

        this.initV=initV;

        for (int i = 0; i < NOB; i++) {

            ae.add(initA);

            ve.add(initV);

            x.add(0.0);

        }

        for (int i = 0; i < NOM; i++) {

            res.add(0.0);

        }

    }

    public List<Double> getAe() {
        return ae;
    }

    public List<Double> getVe() {
        return ve;
    }

    public List<Double> getX() {
        return x;
    }

    public List<Double> getRes() {
        return res;
    }

    public void print(){

        System.out.print("Voltage:\n");

        for (int i = 0; i < ve.size(); i++) {

            System.out.printf("%7.2f",ve.get(i));

        }

        System.out.print("\nAngle:\n");

        for (int i = 0; i < ae.size(); i++) {

            System.out.printf("%7.2f",ae.get(i));

        }

    }

    public void reset(){

        for (int i = 0; i < ve.size(); i++) {

            ve.set(i,initV);

            ae.set(i,initA);

            x.set(i,0.0);

        }

        for (int i = 0; i < res.size(); i++) {

            res.set(i,0.0);

        }

    }

}
