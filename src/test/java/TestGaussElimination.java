import ic.app.se.simple.common.ColumnAndValue;
import ic.app.se.simple.common.SparseMatrix;
import ic.app.se.simple.common.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hjh on 15-10-17.
 */
public class TestGaussElimination {

    public static void main(String[] args) {
        List<String> upTriMat=new ArrayList<String>();

        upTriMat.add("2 3 0 0 7");
        upTriMat.add("3 6 11 0 1");
        upTriMat.add("0 11 5 10 4");
        upTriMat.add("0 0 10 13 5");
        upTriMat.add("7 1 4 5 1");

        SparseMatrix matrix=new SparseMatrix();

        matrix.setC(5);

        matrix.setR(5);

        matrix.setSymmetry(true);

        int idx=0;

        for (int i = 0; i < upTriMat.size(); i++) {

            String[] strings=upTriMat.get(i).split(" ");

            matrix.getRowStartAddress().add(idx);

            for (int j = i; j < strings.length; j++) {
                Double value=Double.parseDouble(strings[j]);
                if (value!=0){
                    matrix.getColumnAndValues().add(new ColumnAndValue(j,value));
                    idx++;
                }
            }
        }

        matrix.getRowStartAddress().add(idx);

        matrix.print();

        List<ColumnAndValue> columnAndValueList=new ArrayList<ColumnAndValue>();

        Utils.gaussElimination(matrix,columnAndValueList);

        Utils.printGaussEliminationResult(columnAndValueList);

        System.out.print("\n\ndone");

    }



}
