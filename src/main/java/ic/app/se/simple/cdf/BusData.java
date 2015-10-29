package ic.app.se.simple.cdf;

import ic.app.se.simple.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ic.app.se.simple.common.Utils.loadSectionData;

/**
 * Created by Administrator on 2015/10/28.
 */
public class BusData {

    private static Logger logger = LoggerFactory.getLogger(BusData.class);

    private Map<Integer, Integer> TIO;

    private Map<Integer, Integer> TOI;

    private Map<Integer, Integer> TOA;

    private int[] number;

    private String[] name;

    private int[] area;

    private int[] zone;

    private int[] type;

    private double[] voltage;

    private double[] angle;

    private double[] PL;

    private double[] QL;

    private double[] PG;

    private double[] QG;

    private double[] VBase;

    private double[] VDesired;

    private double[] QVMax;

    private double[] QVMin;

    private double[] scG;

    private double[] scB;

    //    this parameter has different format in different files, so we use string to store the data
    private String[] cBus;

    private int paraNum;

    private int n;

    public BusData(String fpath) {

        TIO = new HashMap<Integer, Integer>();

        TOI = new HashMap<Integer, Integer>();

        TOA = new HashMap<Integer, Integer>();

        paraNum = 18;

        n = 0;

        loadData(fpath);

    }

    private void loadData(String fpath) {

        List<String> busDataContent = loadSectionData(fpath, Constants.CDF.BUS_SECTION);

        List<String> cols;

        int ntmp = busDataContent.size();

        int[] numbertmp = new int[ntmp];

        String[] nametmp = new String[ntmp];

        int[] areatmp = new int[ntmp];

        int[] zonetmp = new int[ntmp];

        int[] typetmp = new int[ntmp];

        double[] voltagetmp = new double[ntmp];

        double[] angletmp = new double[ntmp];

        double[] PLtmp = new double[ntmp];

        double[] QLtmp = new double[ntmp];

        double[] PGtmp = new double[ntmp];

        double[] QGtmp = new double[ntmp];

        double[] VBasetmp = new double[ntmp];

        double[] VDesiredtmp = new double[ntmp];

        double[] QVMaxtmp = new double[ntmp];

        double[] QVMintmp = new double[ntmp];

        double[] scGtmp = new double[ntmp];

        double[] scBtmp = new double[ntmp];

        String[] cBustmp = new String[ntmp];

        for (int i = 0; i < busDataContent.size(); i++) {

            cols = getDataStrings(busDataContent.get(i));

            if (cols.size() != paraNum) {

                logger.error("Incorrect data format!");

                return;

            }

            numbertmp[i] = Integer.parseInt(cols.get(0));

            nametmp[i] = cols.get(1);

            areatmp[i] = Integer.parseInt(cols.get(2));

            zonetmp[i] = Integer.parseInt(cols.get(3));

            typetmp[i] = Integer.parseInt(cols.get(4));

            voltagetmp[i] = Double.parseDouble(cols.get(5));

            angletmp[i] = Double.parseDouble(cols.get(6));

            PLtmp[i] = Double.parseDouble(cols.get(7));

            QLtmp[i] = Double.parseDouble(cols.get(8));

            PGtmp[i] = Double.parseDouble(cols.get(9));

            QGtmp[i] = Double.parseDouble(cols.get(10));

            VBasetmp[i] = Double.parseDouble(cols.get(11));

            VDesiredtmp[i] = Double.parseDouble(cols.get(12));

            QVMaxtmp[i] = Double.parseDouble(cols.get(13));

            QVMintmp[i] = Double.parseDouble(cols.get(14));

            scGtmp[i] = Double.parseDouble(cols.get(15));

            scBtmp[i] = Double.parseDouble(cols.get(16));

            cBustmp[i] = cols.get(17);

        }

        setNumber(numbertmp);

        setName(nametmp);

        setArea(areatmp);

        setZone(zonetmp);

        setType(typetmp);

        setVoltage(voltagetmp);

        setAngle(angletmp);

        setPL(PLtmp);

        setQL(QLtmp);

        setPG(PGtmp);

        setQG(QGtmp);

        setVBase(VBasetmp);

        setVDesired(VDesiredtmp);

        setQVMax(QVMaxtmp);

        setQVMin(QVMintmp);

        setScG(scGtmp);

        setScB(scBtmp);

        setcBus(cBustmp);

        setN(ntmp);

    }

    private List<String> getDataStrings(String data) {

        List<String> ret = new ArrayList<String>();

        ret.add(data.substring(0, 4).trim());

        ret.add(data.substring(4, 17).trim());

        ret.add(data.substring(17, 20).trim());

        ret.add(data.substring(20, 23).trim());

        ret.add(data.substring(23, 26).trim());

        ret.add(data.substring(26, 33).trim());

        ret.add(data.substring(33, 40).trim());

        ret.add(data.substring(40, 49).trim());

        ret.add(data.substring(49, 59).trim());

        ret.add(data.substring(59, 67).trim());

        ret.add(data.substring(67, 75).trim());

        ret.add(data.substring(75, 83).trim());

        ret.add(data.substring(83, 90).trim());

        ret.add(data.substring(90, 98).trim());

        ret.add(data.substring(98, 106).trim());

        ret.add(data.substring(106, 114).trim());

        ret.add(data.substring(114, 122).trim());

        ret.add(data.substring(122, data.length()).trim());

        return ret;

    }

    public void reorderBusNumbers(BranchData branchData) {

        int[] i = branchData.getI();

        int[] j = branchData.getJ();

        int[] type = getType();

        int[] buses = getNumber();

        Map<Integer, Integer> busBranchNumberMap = new HashMap<Integer, Integer>();

        int ni, nj, n23;

//        compute lines from each bus
        for (int k = 0; k < i.length; k++) {

            ni = i[k];

            nj = j[k];

            if (ni == nj) {

                continue;

            }

            if (!busBranchNumberMap.containsKey(ni)) {

                busBranchNumberMap.put(ni, 1);

            } else {

                busBranchNumberMap.put(ni, busBranchNumberMap.get(ni) + 1);

            }


            if (!busBranchNumberMap.containsKey(nj)) {

                busBranchNumberMap.put(nj, 1);

            } else {

                busBranchNumberMap.put(nj, busBranchNumberMap.get(nj) + 1);

            }

        }

        int idx = busBranchNumberMap.size();

        Map<Integer, Integer> pvbuses = new HashMap<Integer, Integer>();

        for (int k = 0; k < type.length; k++) {

            TOA.put(buses[k], k);

//            swing bus, reference bus
            if (type[k] == 3) {

                n23 = buses[k];

                if (busBranchNumberMap.containsKey(n23)) {

                    TIO.put(idx, n23);

                    busBranchNumberMap.remove(n23);

                    idx--;

                } else {

                    logger.error("Reference bus {} does not exist in branch data!", n23);

                    return;

                }

            } else if (type[k] == 2) {

//                PV bus
                n23 = buses[k];

                if (busBranchNumberMap.containsKey(n23)) {

                    pvbuses.put(n23, busBranchNumberMap.get(n23));

                    busBranchNumberMap.remove(n23);

                }

            }

        }

        idx = addBuses(idx, pvbuses);

        addBuses(idx, busBranchNumberMap);

        for (Map.Entry<Integer, Integer> e : TIO.entrySet()) {

            TOI.put(e.getValue(), e.getKey());

        }

    }

    private int addBuses(int currentIdx, Map<Integer, Integer> busBranchNoMap) {

//        insert from the end
        while (busBranchNoMap.size() > 0) {

            Integer maxKey = null;

            for (Map.Entry<Integer, Integer> e : busBranchNoMap.entrySet()) {

                if (maxKey == null) {

                    maxKey = e.getKey();

                }

                if (busBranchNoMap.get(maxKey) < e.getValue()) {

                    maxKey = e.getKey();

                }

            }

            TIO.put(currentIdx--, maxKey);

            busBranchNoMap.remove(maxKey);

        }

        return currentIdx;

    }

    public Map<Integer, Integer> getTIO() {
        return TIO;
    }

    public Map<Integer, Integer> getTOA() {
        return TOA;
    }

    public Map<Integer, Integer> getTOI() {
        return TOI;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getParaNum() {
        return paraNum;
    }

    public void setParaNum(int paraNum) {
        this.paraNum = paraNum;
    }

    public int[] getNumber() {
        return number;
    }

    public void setNumber(int[] number) {
        this.number = number;
    }

    public String[] getName() {
        return name;
    }

    public void setName(String[] name) {
        this.name = name;
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

    public int[] getType() {
        return type;
    }

    public void setType(int[] type) {
        this.type = type;
    }

    public double[] getVoltage() {
        return voltage;
    }

    public void setVoltage(double[] voltage) {
        this.voltage = voltage;
    }

    public double[] getAngle() {
        return angle;
    }

    public void setAngle(double[] angle) {
        this.angle = angle;
    }

    public double[] getPL() {
        return PL;
    }

    public void setPL(double[] PL) {
        this.PL = PL;
    }

    public double[] getQL() {
        return QL;
    }

    public void setQL(double[] QL) {
        this.QL = QL;
    }

    public double[] getPG() {
        return PG;
    }

    public void setPG(double[] PG) {
        this.PG = PG;
    }

    public double[] getQG() {
        return QG;
    }

    public void setQG(double[] QG) {
        this.QG = QG;
    }

    public double[] getVBase() {
        return VBase;
    }

    public void setVBase(double[] VBase) {
        this.VBase = VBase;
    }

    public double[] getVDesired() {
        return VDesired;
    }

    public void setVDesired(double[] VDesired) {
        this.VDesired = VDesired;
    }

    public double[] getQVMax() {
        return QVMax;
    }

    public void setQVMax(double[] QVMax) {
        this.QVMax = QVMax;
    }

    public double[] getQVMin() {
        return QVMin;
    }

    public void setQVMin(double[] QVMin) {
        this.QVMin = QVMin;
    }

    public double[] getScG() {
        return scG;
    }

    public void setScG(double[] scG) {
        this.scG = scG;
    }

    public double[] getScB() {
        return scB;
    }

    public void setScB(double[] scB) {
        this.scB = scB;
    }

    public String[] getcBus() {
        return cBus;
    }

    public void setcBus(String[] cBus) {
        this.cBus = cBus;
    }

}
