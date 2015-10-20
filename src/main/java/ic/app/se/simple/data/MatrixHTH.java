package ic.app.se.simple.data;

import ic.app.se.simple.common.ColumnAndValue;
import ic.app.se.simple.common.SparseMatrix;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hjh on 15-10-14.
 */
public class MatrixHTH {

    private SparseMatrix matrix;

    private MatrixH matrixH;

    private SparseMatrix HTRI;

    private int NOB;

    public MatrixHTH(MatrixH matrixH, int NOB){

        this.matrixH=matrixH;

        this.NOB=NOB;

        this.HTRI=matrixH.getHTRI();

        matrix=new SparseMatrix();

        matrix.setC(matrixH.getHC());

        matrix.setR(matrixH.getHC());

        computeHTH();

        matrix.print();

    }

    private void computeHTH(){

        int mi,me,ji,je,m,j;

        List<Integer> IHTH=matrix.getRowStartAddress();

        List<ColumnAndValue> colVals=matrix.getColumnAndValues();

        IHTH.add(0);

        for (int i = 0; i < NOB; i++) {

            List<Integer> tmp=new ArrayList<Integer>();

//            these are the measurement number not index
            mi=matrixH.getIHT().get(i);

            me=matrixH.getIHT().get(i+1)-1;

            while (mi<=me) {

                m = matrixH.getMIHT().get(mi);

                ji = matrixH.getMH().get(m-1);

                je = matrixH.getMH().get(m) - 1;

                while (ji <= je) {

//                    this is the number, should convert to index
                    j = matrixH.getHI().get(ji)-1;

                    insertToSortedArray(j, tmp);

                    ji++;

                }

                mi++;

            }

            for (int k = 0; k < tmp.size(); k++) {

                colVals.add(new ColumnAndValue(tmp.get(k),0));

            }

            IHTH.add(IHTH.get(i)+tmp.size());

        }
        
    }

    private void computeHTRIH(){

        for (int i = 0; i < matrix.getRowStartAddress().size() - 1; i++) {

            for (int j = matrix.getRowStartAddress().get(i); j < matrix.getRowStartAddress().get(i + 1); j++) {

                matrix.getColumnAndValues().get(j).setValue(getHTRIHElement(i,j));

            }

        }

    }

    private double getHTRIHElement(int i,int j){

        int ks=HTRI.getRowStartAddress().get(i);

        int ke=HTRI.getRowStartAddress().get(i+1);

        int ls=matrixH.

        for (int k = ; k <HTRI.getRowStartAddress().get(i+1) ; k++) {



        }

    }

    private void insertToSortedArray(int e,List<Integer> arr){

        int curr,k=-1;

        for (int i = 0; i < arr.size(); i++) {

            curr=arr.get(i);

            if (curr==e){

                return;

            }else if (curr>e){

                k=i;

                break;

            }

        }

        if (k==-1){

            arr.add(e);

            return;

        }else {

            arr.add(0);

            for (int i = arr.size() - 1; i > k; i--) {

                arr.set(i,arr.get(i-1));

            }

            arr.set(k,e);

        }

    }

}
