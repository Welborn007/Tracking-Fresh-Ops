package com.kesari.tkfops.VehicleStockList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kesari on 27/06/17.
 */

public class StockListMainPOJO {

    private List<StockListSubPOJO> data = new ArrayList<StockListSubPOJO>();

    public List<StockListSubPOJO> getData() {
        return data;
    }

    public void setData(List<StockListSubPOJO> data) {
        this.data = data;
    }
}
