package com.kesari.tkfops.OrderAssignedToBiker;

/**
 * Created by kesari on 25/07/17.
 */

public class OrderAssignedDataPOJO {

    private String _id;

    private String bikerId;

    private String orderId;

    private String vehicleId;

    private String createdBy;

    private String createdAt;

    private String active;

    private String status;

    private String __v;

    private OrderAssignedOrderDetailPOJO Order;

    private OrderAssignedBikerDataPOJO biker;

    public OrderAssignedBikerDataPOJO getBiker() {
        return biker;
    }

    public void setBiker(OrderAssignedBikerDataPOJO biker) {
        this.biker = biker;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getBikerId() {
        return bikerId;
    }

    public void setBikerId(String bikerId) {
        this.bikerId = bikerId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String get__v() {
        return __v;
    }

    public void set__v(String __v) {
        this.__v = __v;
    }

    public OrderAssignedOrderDetailPOJO getOrder() {
        return Order;
    }

    public void setOrder(OrderAssignedOrderDetailPOJO order) {
        Order = order;
    }
}
