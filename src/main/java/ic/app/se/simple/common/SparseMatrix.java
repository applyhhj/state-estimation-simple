package ic.app.se.simple.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hjh on 15-10-14.
 */
public class SparseMatrix {

//    if the matrix is symmetrical, can just store(only) the upper triangular matrix part
    private boolean symmetry;

    private List<Integer> rowStartAddress;

    private List<ColumnAndValue> columnAndValues;

    private int r;

    private int c;

    public SparseMatrix(){

        rowStartAddress=new ArrayList<Integer>();

        columnAndValues=new ArrayList<ColumnAndValue>();

        symmetry=false;

    }

//    uses array index convention not bus number convention, return null if this element is sparse element
    public Double getValue(int i,int j){

        ColumnAndValue ret=getElement(i,j);

        if (null!=ret){

            return ret.getValue();

        }

        return null;

    }

    public ColumnAndValue getElement(int i,int j) {

        if (symmetry){

            if (i>j){

                int tmp;

                tmp=i;

                i=j;

                j=tmp;

            }

        }

        ColumnAndValue colVal;

        for (int k = rowStartAddress.get(i); k < rowStartAddress.get(i + 1); k++) {

            colVal = columnAndValues.get(k);

            if (colVal.getColumn() == j) {

                return colVal;

            }

        }

        return null;

    }

//    input i is the index of the row
    public List<ColumnAndValue> getRowCopy(int i){

        List<ColumnAndValue> ret=new ArrayList<ColumnAndValue>();

        for (int j = rowStartAddress.get(i); j < rowStartAddress.get(i+1); j++) {

            ret.add(columnAndValues.get(j).copy());

        }

        return ret;

    }

    public void setValue(final int i,final int j,final double value){

//        this is a sparse element, do not need to process
        if (value==0){

            return;

        }

        ColumnAndValue element=getElement(i,j);

        if (element!=null){

            element.setValue(value);

        }else {
//            insert element
            int k=rowStartAddress.get(i);

            while (k<rowStartAddress.get(i+1)){

                if (columnAndValues.get(k).getColumn()>j){

                    break;

                }

                k++;

            }

            columnAndValues.add(k,new ColumnAndValue(j,value));

            for (k = i + 1; k < rowStartAddress.size(); k++) {

                rowStartAddress.set(k,rowStartAddress.get(k)+1);

            }

        }

    }

    public int getC() {
        return c;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public void setC(int c) {
        this.c = c;
    }

    public List<ColumnAndValue> getColumnAndValues() {
        return columnAndValues;
    }

    public List<Integer> getRowStartAddress() {
        return rowStartAddress;
    }

    public void print(String title){

        System.out.print("\n************"+title+"************\n\n");

        print();

    }

    public void print(){

        for (int i = 0; i < r; i++) {

            for (int m = 0; m < c; m++) {

                Double data=getValue(i, m);

                if (data!=null){

                    System.out.printf("%9.4f,", data);

                }else {

                    System.out.print("         ,");

                }

            }

            System.out.print("\n");

        }

        System.out.print("\n-------------------------------\n");

    }

    public SparseMatrix getUpperTriangularMatrix(){

        SparseMatrix ret=new SparseMatrix();

        int idx=0,jc;

        ret.getRowStartAddress().add(0);

//        ignore reference bus
        int cn=this.getRowStartAddress().size()-2;

        for (int i = 0; i < this.getRowStartAddress().size()-2; i++) {

            for (int j = this.getRowStartAddress().get(i); j < this.getRowStartAddress().get(i+1); j++) {

                jc=this.getColumnAndValues().get(j).getColumn();

                if (jc==cn){

                    continue;

                }

                if (jc>=i){

                    ret.getColumnAndValues().add(this.getColumnAndValues().get(j).copy());

                    idx++;

                }

            }

            ret.getRowStartAddress().add(idx);

        }

        ret.getRowStartAddress().add(idx);

        ret.setC(this.getC()-1);

        ret.setR(this.getR()-1);

        return ret;

    }

    public void setSymmetry(boolean symmetry) {
        this.symmetry = symmetry;
    }

    public void setColumnAndValues(List<ColumnAndValue> columnAndValues) {
        this.columnAndValues = columnAndValues;
    }

    public void setRowStartAddress(List<Integer> rowStartAddress) {
        this.rowStartAddress = rowStartAddress;
    }
}
