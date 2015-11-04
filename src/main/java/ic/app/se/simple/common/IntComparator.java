package ic.app.se.simple.common;

import java.util.Comparator;

/**
 * Created by Administrator on 2015/11/4.
 */
public class IntComparator implements Comparator<Integer> {

    public int compare(Integer o1, Integer o2) {
        if (o1 == o2) {

            return 0;

        } else if (o1 > o2) {

            return 1;

        } else {

            return -1;

        }
    }
}