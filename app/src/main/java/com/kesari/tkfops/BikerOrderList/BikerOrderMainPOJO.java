package com.kesari.tkfops.BikerOrderList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kesari on 03/07/17.
 */

public class BikerOrderMainPOJO {

    private List<BikerOrderSubPOJO> data = new ArrayList<BikerOrderSubPOJO>();

    public List<BikerOrderSubPOJO> getData() {
        return data;
    }

    public void setData(List<BikerOrderSubPOJO> data) {
        this.data = data;
    }
}
