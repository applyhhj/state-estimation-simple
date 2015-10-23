package ic.app.se.simple.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hjh on 15-10-9.
 */
public class Utils {

    public static Logger logger = LoggerFactory.getLogger(Utils.class);

    public static List<String> readStringFromFile(String FILE_IN) {

        List<String> ret = new ArrayList<String>();

        File file = new File(FILE_IN);

        try {

            FileInputStream is = new FileInputStream(file);

            InputStreamReader isr = new InputStreamReader(is);

            BufferedReader in = new BufferedReader(isr);

            String line = null;

            while ((line = in.readLine()) != null) {
                ret.add(line.toString());

            }

            in.close();

            is.close();

        } catch (Exception e) {

            // TODO Auto-generated catch block

            e.printStackTrace();

        }

        return ret;

    }

    public static HashMap<String, String> getStartEndMap() {

        HashMap<String, String> ret = new HashMap<String, String>();

        ret.put(Constants.CDF.BRANCH_SECTION_START_KEY, Constants.CDF.BRANCH_SECTION_END_KEY);

        ret.put(Constants.CDF.MEASUREMENT_SECTION_START_KEY, Constants.CDF.MEASUREMENT_SECTION_END_KEY);

        return ret;

    }


    public static List<String> loadSectionData(List<String> dataContent, String sectionStartKey) {

        HashMap<String, String> keyMap = getStartEndMap();

        List<String> sectionData = new ArrayList<String>();

        boolean found = false;

        int endCheck;

        for (int k = 0; k < dataContent.size(); k++) {

            String entry = dataContent.get(k);

            if (!found && entry.contains(sectionStartKey)) {

                found = true;

                sectionData.clear();

                continue;

            }

            if (found) {

                endCheck = checkSectionEnd(entry, sectionStartKey, keyMap);

                if (endCheck > 0) {

                    break;

                } else if (endCheck == 0) {

                    sectionData.add(entry);

                } else {

                    logger.error("Section {} has incorrect end!", sectionStartKey);

                    sectionData.clear();

                    break;

                }

            }

        }

        return sectionData;

    }

    private String checkSectionStartKey(String entry, HashMap keyMap) {

        for (Object key : keyMap.keySet()) {

            String keystr = key.toString();

            if (entry.contains(keystr)) {

                return keystr;

            }

        }

        return null;
    }

    private static int checkSectionEnd(String entry, String sectionStartKey, HashMap keyMap) {

        if (keyMap.values().contains(entry)) {

            if (keyMap.get(sectionStartKey).equals(entry)) {

                return 1;

            } else {

                return -1;

            }

        } else {

            return 0;

        }

    }

    //    a11 should not be zero, input matrix should be upper triangular matrix
    public static void gaussElimination(SparseMatrix matrix, List<ColumnAndValue> retColumnAndValues) {

        if (retColumnAndValues == null) {

            logger.error("Return maxtrix null!!");

            return;

        }

        int ka, ku, jrow, jret;

        for (int i = 0; i < matrix.getR(); i++) {

            List<ColumnAndValue> row = matrix.getRowCopy(i);

            if (i != 0) {

                double D = retColumnAndValues.get(0).getValue();

                ku = 1;

                while (ku < retColumnAndValues.size()) {

                    jret = retColumnAndValues.get(ku).getColumn();

//                    next row
                    if (jret == 0) {

                        D = retColumnAndValues.get(ku++).getValue();

                    } else if (i == jret) {

                        double H = D * retColumnAndValues.get(ku).getValue();

                        ka = 0;

                        while (ka < row.size()) {

                            jret = retColumnAndValues.get(ku).getColumn();

                            jrow = row.get(ka).getColumn();

                            if (jret == 0) {

                                D = retColumnAndValues.get(ku++).getValue();

                                break;

                            }

                            if (jret == jrow) {

                                row.get(ka).setValue(row.get(ka).getValue() - H * retColumnAndValues.get(ku).getValue());

                                ka++;

                                ku++;

                            } else if (jret > jrow) {

                                ka++;

                                if (ka >= row.size()) {

                                    row.add(new ColumnAndValue(jret, -H * retColumnAndValues.get(ku).getValue()));

                                    ka++;

                                    ku++;

                                }

                            } else {

                                row.add(ka++, new ColumnAndValue(jret, -H * retColumnAndValues.get(ku).getValue()));

                                ku++;

                            }

                        }

                    } else {

                        ku++;

                    }

                }

            }

            retColumnAndValues.add(new ColumnAndValue(0, row.get(0).getValue()));

//            normalize rest elements
            for (int j = 1; j < row.size(); j++) {

                retColumnAndValues.add(new ColumnAndValue(row.get(j).getColumn(),
                        row.get(j).getValue() / row.get(0).getValue()));

            }

        }

    }

    //  we can not get row number from result, so if there is any sparse element it will be ignored.
    public static void printGaussEliminationResult(List<ColumnAndValue> result) {

        int i=0;

        for (int j = 0; j < result.size(); j++) {

            if (result.get(j).getColumn() == 0) {

                System.out.print("\n");

                System.out.printf("%9.2f,", result.get(j).getValue());

                i=1;

            }else {

                while (result.get(j).getColumn() > i) {

                    System.out.print("         ,");

                    i++;

                }

                System.out.printf("%9.2f,", result.get(j).getValue());

                i++;

            }

        }

    }

    public static String getPQType(int type){

        String pq;

        if (type==0){

            pq="P";

        }else {

            pq="Q";

        }

        return pq;

    }

}
