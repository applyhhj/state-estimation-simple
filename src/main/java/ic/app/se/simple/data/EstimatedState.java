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

    public EstimatedState(int NOB,int NOM,double initV,double initA){

        ae=new ArrayList<Double>();

        ve=new ArrayList<Double>();

        x=new ArrayList<Double>();

        res=new ArrayList<Double>();

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
}
