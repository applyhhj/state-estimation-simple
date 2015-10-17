package ic.app.se.simple.common;

/**
 * Created by hjh on 15-10-14.
 */

public class ColumnAndValue{

    private int column;

    private double value;

    public ColumnAndValue(int col,double val){

        column=col;

        value=val;

    }

    public ColumnAndValue(){

    }

    public double getValue() {
        return value;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public ColumnAndValue copy(){

        return new ColumnAndValue(column,value);

    }
}
