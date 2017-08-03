package com.kesari.tkfops.VehicleOrderReview;

import com.kesari.tkfops.OpenOrders.OrderBikerDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kesari on 04/07/17.
 */

public class VehicleOrderReviewDataPOJO {

    private String vehicleId;

    private String status;

    private String total_price;

    private String payment_Mode;

    private String addressId;

    private String editedAt;

    private String delivery_charge;

    private String _id;

    private String createdBy;

    private String bikerId;

    private VehicleOrderReviewAddressPOJO address;

    private String editedBy;

    private String createdAt;

    private String payment_Status;

    private String active;

    private String orderNo;

    private OrderBikerDetails biker;

    public OrderBikerDetails getBiker() {
        return biker;
    }

    public void setBiker(OrderBikerDetails biker) {
        this.biker = biker;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    private List<VehicleOrderReviewProductPOJO> orders = new ArrayList<VehicleOrderReviewProductPOJO>();

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public String getPayment_Mode() {
        return payment_Mode;
    }

    public void setPayment_Mode(String payment_Mode) {
        this.payment_Mode = payment_Mode;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(String editedAt) {
        this.editedAt = editedAt;
    }

    public String getDelivery_charge() {
        return delivery_charge;
    }

    public void setDelivery_charge(String delivery_charge) {
        this.delivery_charge = delivery_charge;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getBikerId() {
        return bikerId;
    }

    public void setBikerId(String bikerId) {
        this.bikerId = bikerId;
    }

    public VehicleOrderReviewAddressPOJO getAddress() {
        return address;
    }

    public void setAddress(VehicleOrderReviewAddressPOJO address) {
        this.address = address;
    }

    public String getEditedBy() {
        return editedBy;
    }

    public void setEditedBy(String editedBy) {
        this.editedBy = editedBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getPayment_Status() {
        return payment_Status;
    }

    public void setPayment_Status(String payment_Status) {
        this.payment_Status = payment_Status;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public List<VehicleOrderReviewProductPOJO> getOrders() {
        return orders;
    }

    public void setOrders(List<VehicleOrderReviewProductPOJO> orders) {
        this.orders = orders;
    }
}
