package ic.app.se.simple.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hjh on 15-10-10.
 */
public class BusNumbers {

    public static Logger logger= LoggerFactory.getLogger(BusNumbers.class);

//    max internal bus number
    private int NOB;

//    max external bus number
    private int Imax;

//    internal bus number starts from one
    private HashMap<Integer,Integer> TIO;

    private HashMap<Integer,Integer> TOI;

    public BusNumbers(BranchTable branchTable, MeasurementTable measurementTable){

        int[] i=branchTable.getI();

        int[] j=branchTable.getJ();

        int[] type=measurementTable.getType();

        int[] location=measurementTable.getLocation();

        TIO=new HashMap<Integer, Integer>();

        TOI=new HashMap<Integer, Integer>();

        reorderBusNumbers(i, j, type, location);

    }

    private void reorderBusNumbers(int[] i, int[] j, int[] type, int[] location){

        if (i.length!=j.length||type.length!=location.length||i.length==0||type.length==0){

            return;

        }

        HashMap<Integer,Integer> busBranchNumberMap=new HashMap<Integer, Integer>();

        int ni,nj,nref,imax=Integer.MIN_VALUE;

        for (int k = 0; k < i.length; k++) {

            ni=i[k];

            if (!busBranchNumberMap.containsKey(ni)){

                busBranchNumberMap.put(ni, 1);

            }else {

                busBranchNumberMap.put(ni,busBranchNumberMap.get(ni)+1);

            }

            nj=j[k];

            if (!busBranchNumberMap.containsKey(nj)){

                busBranchNumberMap.put(nj,1);

            }else {

                busBranchNumberMap.put(nj,busBranchNumberMap.get(nj)+1);

            }

            if (imax<ni){

                imax=ni;

            }

            if (imax<nj){

                imax=nj;

            }

        }

        for (int k = 0; k < type.length; k++) {

            if (type[k]==0){

                nref=location[k];

                if (busBranchNumberMap.containsKey(nref)) {

                    TIO.put(busBranchNumberMap.size(), nref);

                    busBranchNumberMap.remove(nref);

                    break;

                }else {

                    logger.error("Bus {} does not exist in branch data!",nref);

                    return;

                }

            }

//            TODO: analyze PV node

        }

        int internalIdx=1;

        while (busBranchNumberMap.size()>0){

            Integer minKey=null;

            for (Map.Entry<Integer,Integer> e:busBranchNumberMap.entrySet()){

                if (minKey==null){

                    minKey=e.getKey();

                }

                if (busBranchNumberMap.get(minKey)>e.getValue()){

                    minKey=e.getKey();

                }

            }

            TIO.put(internalIdx,minKey);

            busBranchNumberMap.remove(minKey);

            internalIdx++;

        }

        for (Map.Entry<Integer,Integer> e:TIO.entrySet()){

            TOI.put(e.getValue(),e.getKey());

        }

        Imax=imax;

        NOB =TIO.size();

    }

    public HashMap<Integer, Integer> getTIO() {
        return TIO;
    }

    public HashMap<Integer, Integer> getTOI() {
        return TOI;
    }

    public int getNOB() {
        return NOB;
    }
}
