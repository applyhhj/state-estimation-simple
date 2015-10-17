package ic.app.se.simple.data;

import ic.app.se.simple.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static ic.app.se.simple.common.Utils.loadSectionData;

/**
 * Created by hjh on 15-10-9.
 */
public class MeasurementTable {

    public static Logger logger= LoggerFactory.getLogger(MeasurementTable.class);

    private int NOM;

    private int[] type;

    private int[] location;

    private double[] z;

    private double[] sigma;

    private double V0;

    public void loadData(List<String> data){

        List<String> mData=loadSectionData(data, Constants.CDF.MEASUREMENT_SECTION_START_KEY);

        int[] typetmp=new int[mData.size()];

        int[] locationtmp=new int[mData.size()];

        double[] ztmp=new double[mData.size()];

        double[] sigmatmp=new double[mData.size()];

        for (int k = 0; k < mData.size(); k++){

            String entry=mData.get(k);

            String col[]=entry.trim().split(" +");

            if (col.length!=Constants.CDF.MEASUREMENT_SECTION_NUMBER_OF_COLUMN) {

                logger.error("Input data error: {}", entry);

                return;

            }

            locationtmp[k]=Integer.parseInt(col[2]);

            ztmp[k]=Double.parseDouble(col[3]);

            sigmatmp[k]=Double.parseDouble(col[4]);

            typetmp[k]=Integer.parseInt(col[1]);

            if (typetmp[k]==0){

                V0=ztmp[k];

            }

        }

        setLocation(locationtmp);

        setNOM(mData.size());

        setSigma(sigmatmp);

        setType(typetmp);

        setZ(ztmp);

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

    public double getV0() {
        return V0;
    }
}
