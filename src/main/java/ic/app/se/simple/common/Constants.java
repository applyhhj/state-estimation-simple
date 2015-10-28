package ic.app.se.simple.common;

/**
 * Created by hjh on 15-10-9.
 */
public abstract class Constants {
    public abstract class CDF{
        public static final String BUS_SECTION ="BUS DATA FOLLOWS";
        public static final String BRANCH_SECTION ="BRANCH DATA FOLLOWS";
        public static final String MEASUREMENT_SECTION ="MEASUREMENT SYSTEM DATA FOLLOWS";
        public static final String BUS_SECTION_END ="-999";
        public static final String BRANCH_SECTION_END ="-999";
        public static final String MEASUREMENT_SECTION_END ="-999";
        public static final int BRANCH_SECTION_NUMBER_OF_COLUMN=21;
        public static final int MEASUREMENT_SECTION_NUMBER_OF_COLUMN=6;
    }

    public abstract class ESTIMATOR{

        public static final double ERR_REC =10;
        public static final double ERR_THETA=1e-3;
        public static final double ERR_V=1e-2;

    }
}
