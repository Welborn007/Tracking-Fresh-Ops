package com.kesari.tkfops.BikerOrderReview;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kesari on 13/06/17.
 */

public class BikerOrderReviewDataPOJO {

    private String status;

    private String total_price;

    private String payment_Mode;

    private String addressId;

    private String editedAt;

    private String _id;

    private String createdBy;

    private BikerOrderReviewAddress address;

    private String editedBy;

    private String createdAt;

    private String payment_Status;

    private String active;

    private String payment_Id;

    private String delivery_charge;

    private String orderNo;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getDelivery_charge() {
        return delivery_charge;
    }

    public void setDelivery_charge(String delivery_charge) {
        this.delivery_charge = delivery_charge;
    }

    private List<BikerOrderReviewProductPOJO> orders = new ArrayList<BikerOrderReviewProductPOJO>();

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

    public BikerOrderReviewAddress getAddress() {
        return address;
    }

    public void setAddress(BikerOrderReviewAddress address) {
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

    public String getPayment_Id() {
        return payment_Id;
    }

    public void setPayment_Id(String payment_Id) {
        this.payment_Id = payment_Id;
    }

    public List<BikerOrderReviewProductPOJO> getOrder() {
        return orders;
    }

    public void setOrder(List<BikerOrderReviewProductPOJO> order) {
        this.orders = order;
    }
}
