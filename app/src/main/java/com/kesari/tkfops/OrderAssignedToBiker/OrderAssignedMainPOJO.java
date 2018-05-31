package com.kesari.tkfops.OrderAssignedToBiker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kesari on 25/07/17.
 */

public class OrderAssignedMainPOJO {

    private List<OrderAssignedDataPOJO> data = new ArrayList<OrderAssignedDataPOJO>();

    public List<OrderAssignedDataPOJO> getData() {
        return data;
    }

    public void setData(List<OrderAssignedDataPOJO> data) {
        this.data = data;
    }
}
