package ic.app.se.simple.cdf;

import ic.app.se.simple.common.Constants;

import java.util.List;

import static ic.app.se.simple.common.Utils.loadSectionData;

/**
 * Created by Administrator on 2015/10/28.
 */
public class CDFDataConverter {

    private String filepath;

    public CDFDataConverter(String filepath){

        this.filepath=filepath;

    }

    private void loadData(){

        List<String> busDataContent=loadSectionData(filepath, Constants.CDF.BUS_SECTION);

        List<String> branchDataContent=loadSectionData(filepath, Constants.CDF.BRANCH_SECTION);

        for (int i = 0; i < busDataContent.size(); i++) {



        }

    }

}


