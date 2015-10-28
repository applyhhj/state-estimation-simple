package ic.app.se.simple.cdf;

import ic.app.se.simple.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static ic.app.se.simple.common.Utils.loadSectionData;

/**
 * Created by Administrator on 2015/10/28.
 * <p>
 * cdf data files of different systems do not obey the cdf data format, this program is ONLY used
 * to process 300 bus cdf file.
 */
public class BranchData {

    private static Logger logger = LoggerFactory.getLogger(BranchData.class);

    private int[] i;

    private int[] j;

    private int[] area;

    private int[] zone;

    private int[] circuit;

    private int[] type;

    private double[] r;

    private double[] x;

    private double[] b;

    private int[] sr1;

    private int[] sr2;

    private int[] sr3;

    private int[] cbus;

    private int[] side;

    private double[] tratio;

    private double[] tangle;

    private double[] tpmin;

    private double[] tpmax;

    private double[] step;

    private double[] limitMin;

    private double[] limitMax;

    private int[] unknown;

    private int paraNum;

    private int n;

    public BranchData(String fpath) {

        paraNum = 22;

        n = 0;

        loadData(fpath);

    }

    private void loadData(String fpath) {

        List<String> branchDataContent = loadSectionData(fpath, Constants.CDF.BRANCH_SECTION);

        List<String> cols;

        int ntmp = branchDataContent.size();

        int[] itmp = new int[ntmp];

        int[] jtmp = new int[ntmp];

        int[] areatmp = new int[ntmp];

        int[] zonetmp = new int[ntmp];

        int[] circuittmp = new int[ntmp];

        int[] typetmp = new int[ntmp];

        double[] rtmp = new double[ntmp];

        double[] xtmp = new double[ntmp];

        double[] btmp = new double[ntmp];

        int[] sr1tmp = new int[ntmp];

        int[] sr2tmp = new int[ntmp];

        int[] sr3tmp = new int[ntmp];

        int[] cbustmp = new int[ntmp];

        int[] sidetmp = new int[ntmp];

        double[] tratiotmp = new double[ntmp];

        double[] tangletmp = new double[ntmp];

        double[] tpmintmp = new double[ntmp];

        double[] tpmaxtmp = new double[ntmp];

        double[] steptmp = new double[ntmp];

        double[] limitMintmp = new double[ntmp];

        double[] limitMaxtmp = new double[ntmp];

        int[] unknowntmp = new int[ntmp];

        for (int k = 0; k < branchDataContent.size(); k++) {

            cols = getBranchDataStrings(branchDataContent.get(k));

            if (cols.size() != paraNum) {

                logger.error("Incorrect data format!");

                return;

            }

            itmp[k] = Integer.parseInt(cols.get(0));

            jtmp[k] = Integer.parseInt(cols.get(1));

            areatmp[k] = Integer.parseInt(cols.get(2));

            zonetmp[k] = Integer.parseInt(cols.get(3));

            circuittmp[k] = Integer.parseInt(cols.get(4));

            typetmp[k] = Integer.parseInt(cols.get(5));

            rtmp[k] = Double.parseDouble(cols.get(6));

            xtmp[k] = Double.parseDouble(cols.get(7));

            btmp[k] = Double.parseDouble(cols.get(8));

            sr1tmp[k] = Integer.parseInt(cols.get(9));

            sr2tmp[k] = Integer.parseInt(cols.get(10));

            sr3tmp[k] = Integer.parseInt(cols.get(11));

            cbustmp[k] = Integer.parseInt(cols.get(12));

            sidetmp[k] = Integer.parseInt(cols.get(13));

            tratiotmp[k] = Double.parseDouble(cols.get(14));

            tangletmp[k] = Double.parseDouble(cols.get(15));

            tpmintmp[k] = Double.parseDouble(cols.get(16));

            tpmaxtmp[k] = Double.parseDouble(cols.get(17));

            steptmp[k] = Double.parseDouble(cols.get(18));

            limitMintmp[k] = Double.parseDouble(cols.get(19));

            limitMaxtmp[k] = Double.parseDouble(cols.get(20));

            unknowntmp[k] = Integer.parseInt(cols.get(21));

        }

        setI(itmp);

        setJ(jtmp);

        setArea(areatmp);

        setZone(zonetmp);

        setCircuit(circuittmp);

        setType(typetmp);

        setR(rtmp);

        setX(xtmp);

        setB(btmp);

        setSr1(sr1tmp);

        setSr2(sr2tmp);

        setSr3(sr3tmp);

        setCbus(cbustmp);

        setSide(sidetmp);

        setTratio(tratiotmp);

        setTangle(tangletmp);

        setTpmin(tpmintmp);

        setTpmax(tpmaxtmp);

        setStep(steptmp);

        setLimitMin(limitMintmp);

        setLimitMax(limitMaxtmp);

        setUnknown(unknowntmp);

        setN(ntmp);

    }

    private List<String> getBranchDataStrings(String data) {

        List<String> ret = new ArrayList<String>();

        ret.add(data.substring(0, 4).trim());

        ret.add(data.substring(4, 9).trim());

        ret.add(data.substring(9, 12).trim());

        ret.add(data.substring(12, 15).trim());

        ret.add(data.substring(15, 17).trim());

        ret.add(data.substring(17, 19).trim());

        ret.add(data.substring(19, 29).trim());

        ret.add(data.substring(29, 40).trim());

        ret.add(data.substring(40, 50).trim());

        ret.add(data.substring(50, 55).trim());

        ret.add(data.substring(55, 61).trim());

        ret.add(data.substring(61, 67).trim());

        ret.add(data.substring(67, 72).trim());

        ret.add(data.substring(72, 74).trim());

        ret.add(data.substring(74, 82).trim());

        ret.add(data.substring(82, 90).trim());

        ret.add(data.substring(90, 97).trim());

        ret.add(data.substring(97, 104).trim());

        ret.add(data.substring(104, 111).trim());

        ret.add(data.substring(111, 119).trim());

        ret.add(data.substring(119, 126).trim());

        ret.add(data.substring(126, data.length()).trim());

        for (int k = 0; k < ret.size(); k++) {

            if (ret.get(k).equals("")) {

//                in case there is empty data we fill it with zero
                ret.set(k, "0");

            }

        }

        return ret;

    }

    public int[] getUnknown() {
        return unknown;
    }

    public void setUnknown(int[] unknown) {
        this.unknown = unknown;
    }

    public int getParaNum() {
        return paraNum;
    }

    public void setParaNum(int paraNum) {
        this.paraNum = paraNum;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int[] getI() {
        return i;
    }

    public void setI(int[] i) {
        this.i = i;
    }

    public int[] getJ() {
        return j;
    }

    public void setJ(int[] j) {
        this.j = j;
    }

    public int[] getArea() {
        return area;
    }

    public void setArea(int[] area) {
        this.area = area;
    }

    public int[] getZone() {
        return zone;
    }

    public void setZone(int[] zone) {
        this.zone = zone;
    }

    public int[] getCircuit() {
        return circuit;
    }

    public void setCircuit(int[] circuit) {
        this.circuit = circuit;
    }

    public int[] getType() {
        return type;
    }

    public void setType(int[] type) {
        this.type = type;
    }

    public double[] getR() {
        return r;
    }

    public void setR(double[] r) {
        this.r = r;
    }

    public double[] getX() {
        return x;
    }

    public void setX(double[] x) {
        this.x = x;
    }

    public double[] getB() {
        return b;
    }

    public void setB(double[] b) {
        this.b = b;
    }

    public int[] getSr1() {
        return sr1;
    }

    public void setSr1(int[] sr1) {
        this.sr1 = sr1;
    }

    public int[] getSr2() {
        return sr2;
    }

    public void setSr2(int[] sr2) {
        this.sr2 = sr2;
    }

    public int[] getSr3() {
        return sr3;
    }

    public void setSr3(int[] sr3) {
        this.sr3 = sr3;
    }

    public int[] getCbus() {
        return cbus;
    }

    public void setCbus(int[] cbus) {
        this.cbus = cbus;
    }

    public int[] getSide() {
        return side;
    }

    public void setSide(int[] side) {
        this.side = side;
    }

    public double[] getTratio() {
        return tratio;
    }

    public void setTratio(double[] tratio) {
        this.tratio = tratio;
    }

    public double[] getTangle() {
        return tangle;
    }

    public void setTangle(double[] tangle) {
        this.tangle = tangle;
    }

    public double[] getTpmin() {
        return tpmin;
    }

    public void setTpmin(double[] tpmin) {
        this.tpmin = tpmin;
    }

    public double[] getTpmax() {
        return tpmax;
    }

    public void setTpmax(double[] tpmax) {
        this.tpmax = tpmax;
    }

    public double[] getStep() {
        return step;
    }

    public void setStep(double[] step) {
        this.step = step;
    }

    public double[] getLimitMin() {
        return limitMin;
    }

    public void setLimitMin(double[] limitMin) {
        this.limitMin = limitMin;
    }

    public double[] getLimitMax() {
        return limitMax;
    }

    public void setLimitMax(double[] limitMax) {
        this.limitMax = limitMax;
    }
}
