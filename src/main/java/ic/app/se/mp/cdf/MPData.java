package ic.app.se.mp.cdf;

import ic.app.se.simple.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static ic.app.se.simple.common.Utils.readStringFromFile;

/**
 * Created by Administrator on 2015/10/28.
 *
 * Currently we consider all generator and buses are in service and there is no
 * isolated buses and all buses are in a connected network.
 *
 */
public class MPData {

    private static Logger logger = LoggerFactory.getLogger(MPData.class);

    private BranchData branchData;

    private BusData busData;

    private GeneratorData genData;

    private double sbase;

    private String version;

    public MPData(String filepath) {

        branchData = new BranchData();

        busData = new BusData();

        genData = new GeneratorData();

        importData(filepath);

        busData.reorderBusNumbers(branchData);

    }

    private void importData(String filepath) {

        List<String> fileContent = readStringFromFile(filepath);

        String entry;

        List<String> data = new ArrayList<String>();

        boolean end;

        int i = 0;

        while (i < fileContent.size()) {

            entry = fileContent.get(i);

            if (entry.startsWith("#")) {

                i++;

                continue;

            }

            end = false;

            if (entry.contains(Constants.MPC.MPC_VERSION)) {

                version = fileContent.get(++i);

                if (fileContent.get(++i).contains(Constants.MPC.MPC_VERSION_END)) {

                    end = true;

                }

            } else if (entry.contains(Constants.MPC.MPC_BASEMVA)) {

                sbase = Double.parseDouble(fileContent.get(++i));

                if (fileContent.get(++i).contains(Constants.MPC.MPC_BASEMVA_END)) {

                    end = true;

                }

            } else if (entry.contains(Constants.MPC.MPC_BUS)) {

                data.clear();

                while (++i < fileContent.size()) {

                    if (fileContent.get(i).contains(Constants.MPC.MPC_BUS_END)) {

                        end = true;

                        break;

                    } else {

                        data.add(fileContent.get(i));

                    }

                }

                if (end) {

                    if (!busData.loadData(data)) {

                        break;

                    }

                }

            } else if (entry.contains(Constants.MPC.MPC_GEN)) {

                data.clear();

                while (++i < fileContent.size()) {

                    if (fileContent.get(i).contains(Constants.MPC.MPC_GEN_END)) {

                        end = true;

                        break;

                    } else {

                        data.add(fileContent.get(i));

                    }

                }

                if (end) {

                    if (!genData.loadData(data)) {

                        break;

                    }

                }

            } else if (entry.contains(Constants.MPC.MPC_BRANCH)) {

                data.clear();

                while (++i < fileContent.size()) {

                    if (fileContent.get(i).contains(Constants.MPC.MPC_BRANCH_END)) {

                        end = true;

                        break;

                    } else {

                        data.add(fileContent.get(i));

                    }

                }

                if (end) {

                    if (!branchData.loadData(data)) {

                        break;

                    }

                }

            } else {

                end = true;

            }

            if (!end) {

                logger.error("Section {} has no end", entry);

                return;

            }

            i++;

        }

    }

    public double getSbase() {
        return sbase;
    }

    public BranchData getBranchData() {
        return branchData;
    }

    public BusData getBusData() {
        return busData;
    }

    public String getVersion() {
        return version;
    }
}


