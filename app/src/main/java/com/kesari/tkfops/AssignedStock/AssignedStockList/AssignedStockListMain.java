package com.kesari.tkfops.AssignedStock.AssignedStockList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kesari on 27/06/17.
 */

public class AssignedStockListMain {

    private List<AssignedStockListSubPojo> data = new ArrayList<AssignedStockListSubPojo>();

    public List<AssignedStockListSubPojo> getData() {
        return data;
    }

    public void setData(List<AssignedStockListSubPojo> data) {
        this.data = data;
    }
}
