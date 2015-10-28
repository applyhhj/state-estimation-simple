package ic.app.se.simple.data;

import ic.app.se.simple.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

import static ic.app.se.simple.common.Utils.loadSectionData;

/**
 * Created by hjh on 15-10-9.
 *
 * Uses natural order
 *
 */
public class MeasurementTable {

//    measurement table uses natural order, measurement number starts from zero, uses index to access
    public static Logger logger= LoggerFactory.getLogger(MeasurementTable.class);

    private int NOM;

    private int[] type;

    private int[] location;

    private double[] z;

    private double[] sigma;

    private double[] real;

    private Random random;

    private double V0;

    public MeasurementTable(){

        random=new Random();

    }

    public void loadData(List<String> data){

        List<String> mData=loadSectionData(data, Constants.CDF.MEASUREMENT_SECTION);

        int[] typetmp=new int[mData.size()];

        int[] locationtmp=new int[mData.size()];

        double[] ztmp=new double[mData.size()];

        double[] sigmatmp=new double[mData.size()];

        double[] realtmp=new double[mData.size()];

        for (int k = 0; k < mData.size(); k++){

            String entry=mData.get(k);

            String col[]=entry.trim().split(" +");

            if (col.length!=Constants.CDF.MEASUREMENT_SECTION_NUMBER_OF_COLUMN) {

                logger.error("Input data error: {}", entry);

                return;

            }

            typetmp[k]=Integer.parseInt(col[1]);

//            this is the number not index
            locationtmp[k]=Integer.parseInt(col[2]);

            ztmp[k]=Double.parseDouble(col[3]);

            sigmatmp[k]=Double.parseDouble(col[4]);

            realtmp[k]=Double.parseDouble(col[5]);

            if (typetmp[k]==0){

                V0=ztmp[k];

            }

        }

        setLocation(locationtmp);

        setNOM(mData.size());

        setSigma(sigmatmp);

        setType(typetmp);

        setZ(ztmp);

        setReal(realtmp);

    }

    public void generateMeasurement(boolean rand){

        if (!rand){

            z[0]=18.71;
            z[1]=34.05;
            z[2]=41.79;
            z[3]=37.96;
            z[4]=-19.10;
            z[5]=-2.24;
            z[6]=17.55;
            z[7]=-10.74;
            z[8]=132.57;
            z[9]=42.61;
            z[10]=-49.49;
            z[11]=-41.93;
            z[12]=-193.22;
            z[13]=-71.08;
            z[14]=112.15;
            z[15]=110.99;

            return;

        }

        for (int i = 0; i < z.length; i++) {

            z[i]=normalRandom(real[i],sigma[i]);

        }

    }

    private double normalRandom(double zreal, double sigma) {

        return random.nextGaussian()*sigma+zreal;

    }

    public double[] getSigma() {
        return sigma;
    }

    public double[] getZ() {
        return z;
    }

    public int getNOM() {
        return NOM;
    }

    public int[] getLocation() {
        return location;
    }

    public int[] getType() {
        return type;
    }

    public void setLocation(int[] location) {
        this.location = location;
    }

    public static void setLogger(Logger logger) {
        MeasurementTable.logger = logger;
    }

    public void setNOM(int NOM) {
        this.NOM = NOM;
    }

    public void setSigma(double[] sigma) {
        this.sigma = sigma;
    }

    public void setType(int[] type) {
        this.type = type;
    }

    public void setZ(double[] z) {
        this.z = z;
    }

    public void setReal(double[] real) {
        this.real = real;
    }

    public double getV0() {
        return V0;
    }
}
