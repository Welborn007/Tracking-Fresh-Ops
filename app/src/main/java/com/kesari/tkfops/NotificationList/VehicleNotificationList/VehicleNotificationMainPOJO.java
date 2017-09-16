package com.kesari.tkfops.NotificationList.VehicleNotificationList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kesari on 14/09/17.
 */

public class VehicleNotificationMainPOJO {

    private List<VehicleNotificationSubPOJO> data = new ArrayList<VehicleNotificationSubPOJO>();

    public List<VehicleNotificationSubPOJO> getData() {
        return data;
    }

    public void setData(List<VehicleNotificationSubPOJO> data) {
        this.data = data;
    }
}
