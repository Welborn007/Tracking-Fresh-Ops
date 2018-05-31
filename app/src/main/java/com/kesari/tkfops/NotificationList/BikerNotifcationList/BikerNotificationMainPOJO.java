package com.kesari.tkfops.NotificationList.BikerNotifcationList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kesari on 14/09/17.
 */

public class BikerNotificationMainPOJO {

    private List<BikerNotificationSubPOJO> data = new ArrayList<BikerNotificationSubPOJO>();

    public List<BikerNotificationSubPOJO> getData() {
        return data;
    }

    public void setData(List<BikerNotificationSubPOJO> data) {
        this.data = data;
    }
}
