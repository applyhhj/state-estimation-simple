package ic.app.se.simple.common;

import org.la4j.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hjh on 15-10-9.
 */
public class Utils {

    public static IntComparator intComparator = new IntComparator();

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

        ret.put(Constants.CDF.BUS_SECTION,Constants.CDF.BUS_SECTION_END);

        ret.put(Constants.CDF.BRANCH_SECTION, Constants.CDF.BRANCH_SECTION_END);

        ret.put(Constants.CDF.MEASUREMENT_SECTION, Constants.CDF.MEASUREMENT_SECTION_END);

        return ret;

    }

    public static HashMap<String, String> getMPDataSectionStartEndMap() {

        HashMap<String, String> ret = new HashMap<String, String>();

        ret.put(Constants.MPC.MPC_VERSION, Constants.MPC.MPC_VERSION_END);

        ret.put(Constants.MPC.MPC_BASEMVA, Constants.MPC.MPC_BASEMVA_END);

        ret.put(Constants.MPC.MPC_BRANCH, Constants.MPC.MPC_BRANCH_END);

        ret.put(Constants.MPC.MPC_BUS, Constants.MPC.MPC_BUS_END);

        ret.put(Constants.MPC.MPC_GEN, Constants.MPC.MPC_GEN_END);

        return ret;

    }

    public static List<String> loadSectionData(String filepath, String sectionStartKey) {

        return loadSectionData(readStringFromFile(filepath),sectionStartKey);

    }

    public static List<String> loadSectionData(List<String> dataContent, String sectionStartKey) {

        HashMap<String, String> keyMap = getStartEndMap();

        List<String> sectionData = new ArrayList<String>();

        boolean found = false;

        boolean endCheck;

        for (int k = 0; k < dataContent.size(); k++) {

            String entry = dataContent.get(k);

            if (!found && entry.contains(sectionStartKey)) {

                found = true;

                sectionData.clear();

                continue;

            }

            if (found) {

                endCheck = checkSectionEnd(entry, sectionStartKey, keyMap);

                if (endCheck) {

                    break;

                } else {

                    sectionData.add(entry);

                }

            }

        }

        return sectionData;

    }

    private static boolean checkSectionEnd(String entry, String sectionStartKey, HashMap<String, String> keyMap) {

        return entry.startsWith(keyMap.get(sectionStartKey));

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

    public static String getPQType(int type) {

        String pq;

        if (type == 0) {

            pq = "P";

        } else {

            pq = "Q";

        }

        return pq;

    }

    public static boolean isLinux() {

        return System.getProperty("os.name").toLowerCase().indexOf("linux") >= 0;

    }

    public static void printInExternalNumberSequence(ComplexMatrix matrix, Map<Integer, Integer> TOI) {

        System.out.print("Real\n");
        for (int i = 0; i < matrix.getRows(); i++) {

            System.out.printf("%8.4f\n", matrix.getR().get(TOI.get(i + 1) - 1, 0));

        }

        System.out.print("Imag\n");
        for (int i = 0; i < matrix.getRows(); i++) {

            System.out.printf("%8.4f\n", matrix.getI().get(TOI.get(i + 1) - 1, 0));

        }

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

    public static class MatrixExtend {

        public static Matrix toMeasurementVector(int nz_, int nbr_, int nb_,
                                                 ComplexMatrix sf_, ComplexMatrix st_, ComplexMatrix sbus, Matrix Va_, Matrix Vm_) {

            Matrix ret = Matrix.zero(nz_, 1);

            ret = insertMatrix(ret, sf_.getR());

            ret = insertMatrix(ret, st_.getR(), nbr_, 0);

            ret = insertMatrix(ret, sbus.getR(), 2 * nbr_, 0);

            ret = insertMatrix(ret, Va_, 2 * nbr_ + nb_, 0);

            ret = insertMatrix(ret, sf_.getI(), 2 * (nbr_ + nb_), 0);

            ret = insertMatrix(ret, st_.getI(), 3 * nbr_ + 2 * nb_, 0);

            ret = insertMatrix(ret, sbus.getI(), 4 * nbr_ + 2 * nb_, 0);

            ret = insertMatrix(ret, Vm_, 4 * nbr_ + 3 * nb_, 0);

            return ret;

        }

        public static Matrix excludeMatrix(Matrix thisMatrix, List<Integer> excRows, List<Integer> excCols) {

            Matrix ret = thisMatrix.copy();

            int ern, ecn;

            if (excRows == null) {

                ern = 0;

            } else {

                ern = excRows.size();

            }

            if (excCols == null) {

                ecn = 0;

            } else {

                ecn = excCols.size();

            }

            for (int i = 0; i < ern; i++) {

                ret.removeRow(excRows.get(i));

            }

            for (int i = 0; i < ecn; i++) {

                ret.removeColumn(excCols.get(i));

            }

            return ret;

        }

        public static Matrix insertMatrix(Matrix thisMatrix, Matrix that) {

            return insertMatrix(thisMatrix, that, 0, 0, 0, 0, that.rows(), that.columns());

        }

        public static Matrix insertMatrix(Matrix thisMatrix, Matrix that,
                                          int destRow, int destColumn) {

            return insertMatrix(thisMatrix, that, 0, 0, destRow, destColumn, that.rows(), that.columns());

        }

        public static Matrix insertMatrix(Matrix thisMatrix, Matrix that,
                                          int destRow, int destColumn, int rows, int columns) {

            return insertMatrix(thisMatrix, that, 0, 0, destRow, destColumn, rows, columns);

        }

        public static Matrix insertMatrix(Matrix thisMatrix, Matrix that,
                                          int srcRow, int srcColumn, int destRow, int destColumn, int rows, int columns) {

            if (rows < 0 || columns < 0) {
                logger.error("Cannot have negative rows or columns: " + rows + "x" + columns);
            }

            if (destRow < 0 || destColumn < 0) {
                logger.error("Cannot have negative destination position: " + destRow + ", " + destColumn);
            }

            if (srcRow < 0 || srcColumn < 0) {
                logger.error("Cannot have negative source position: " + destRow + ", " + destColumn);
            }

            if (srcRow > that.rows() || srcColumn > that.columns()) {
                logger.error("Source position out of bounds: " + srcRow + ", " + srcColumn);
            }

            if (destRow > thisMatrix.rows() || destColumn > thisMatrix.columns()) {
                logger.error("Destination position out of bounds: " + srcRow + ", " + srcColumn);
            }

            if (destRow + rows > thisMatrix.rows() || destColumn + columns > thisMatrix.columns()) {
                logger.error("Out of bounds: Cannot get " + rows + " rows and " + columns + " cols at " + srcRow + ", " + srcColumn + " from a " + that.rows() + "x" + that.columns() + " matrix.");
            }

            if (srcRow + rows > that.rows() || srcColumn + columns > that.columns()) {
                logger.error("Out of bounds: Cannot get " + rows + " rows and " + columns + " cols at " + srcRow + ", " + srcColumn + " from a " + that.rows() + "x" + that.columns() + " matrix.");
            }

            Matrix result = thisMatrix.copy();

            for (int i = 0; i < rows; ++i) {
                for (int j = 0; j < columns; ++j) {
                    result.set(i + destRow, j + destColumn, that.get(i + srcRow, j + srcColumn));
                }
            }

            return result;

        }
    }

}
