package com.kesari.tkfops.Utilities;

/**
 * Created by kesari on 28/04/17.
 */

public interface Constants {

    String SheetalIP = "192.168.1.10:8000";
    String StagingIP = "192.168.1.220:8000";
    String LIVEIP = "115.112.155.181:8000";

   //Send Location of driver
   String DriverLocationApi = "http://" + LIVEIP + "/api/vehicle_positions";
}
