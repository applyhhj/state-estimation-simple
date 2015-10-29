import java.util.List;

import static ic.app.se.simple.common.Utils.readStringFromFile;

/**
 * Created by Administrator on 2015/10/29.
 */
public class TestPTI {

    public static void main(String[] args) {

        List<String> strings = readStringFromFile("F:\\projects\\data\\powersystem\\300bus\\ieee300pti.txt");

        for (int i = 0; i < strings.size(); i++) {

            System.out.print(strings.get(i) + "\n");

        }

    }

}
