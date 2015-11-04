package ic.app.se.simple.common;

/**
 * Created by Administrator on 2015/11/4.
 */
public class EstimationOption {

    private boolean verbose;

    public EstimationOption() {

        verbose = true;

    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
}
