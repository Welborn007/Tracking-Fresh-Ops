package com.kesari.tkfops.OpenOrders;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kesari on 26/07/17.
 */

public class RejectReasonMainPOJO {

    private List<RejectReasonDataPOJO> data = new ArrayList<RejectReasonDataPOJO>();

    public List<RejectReasonDataPOJO> getData() {
        return data;
    }

    public void setData(List<RejectReasonDataPOJO> data) {
        this.data = data;
    }
}
