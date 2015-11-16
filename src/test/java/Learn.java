import java.util.ArrayList;
import java.util.List;

/**
 * Created by hjh on 15-10-11.
 */
public class Learn {

    public static void main(String[] args) {

        // TODO Auto-generated method stub
        Test tt = new Test();
        tt.add(2);//和为2,可变参数长度可以为0个

        List<Integer> tst = new ArrayList<Integer>();

        tst.add(1);

        tst.add(3);

        tst.add(5);

        int[] data = new int[3];

        data[0] = 1;

        data[1] = 3;

        data[2] = 5;

        tt.add(data);



        System.out.print(tst.size());

        updateList(tst);

        System.out.print(tst.size());
    }

    public static void updateList(List<Integer> in) {

        in.add(100);

    }

    public static class Test {
        //求传入参数的和的方法
        public void add(int... arr)//可变参数
        {
            int sum = 0;//先把第一个参数的值复制给sum
            for (int i = 0; i < arr.length; ++i) {
                sum += arr[i];
            }
            System.out.println(sum);
        }

    }
}
