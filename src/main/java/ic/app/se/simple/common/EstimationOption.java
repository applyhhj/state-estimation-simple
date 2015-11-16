package ic.app.se.simple.common;

/**
 * Created by Administrator on 2015/11/4.
 */
public class EstimationOption {

    private boolean verbose;

    private boolean debug;

    public EstimationOption() {

        verbose = true;

        debug = false;


    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
}
