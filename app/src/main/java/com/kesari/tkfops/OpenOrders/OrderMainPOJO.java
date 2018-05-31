package com.kesari.tkfops.OpenOrders;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kesari on 07/06/17.
 */

public class OrderMainPOJO {

    private List<OrderSubPOJO> data = new ArrayList<OrderSubPOJO>();

    public List<OrderSubPOJO> getData() {
        return data;
    }

    public void setData(List<OrderSubPOJO> data) {
        this.data = data;
    }
}
