package com.kesari.tkfops.BikerList.BikerAssign;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kesari on 30/06/17.
 */

public class BikerListMainPOJO {
    private List<BikerListSubPOJO> data = new ArrayList<BikerListSubPOJO>();

    public List<BikerListSubPOJO> getData() {
        return data;
    }

    public void setData(List<BikerListSubPOJO> data) {
        this.data = data;
    }
}
