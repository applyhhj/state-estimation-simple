package ic.app.se.simple.cdf;

import ic.app.se.simple.data.BranchTable;
import ic.app.se.simple.data.BusNumbers;
import ic.app.se.simple.data.MatrixY;

/**
 * Created by Administrator on 2015/10/29.
 */
public class MatrixYCdf extends MatrixY {

    private int test;

    public MatrixYCdf(BranchTable branchTable, BusNumbers busNumbers, int kpq) {

        super(branchTable, busNumbers, kpq);

    }
}
