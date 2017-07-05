package com.kesari.tkfops.VehicleLogin;

/**
 * Created by kesari on 23/06/17.
 */

public class VehicleProfileMain {
    private String status;
    private String message;
    private VehicleData data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public VehicleData getVehicleData() {
        return data;
    }

    public void setVehicleData(VehicleData vehicleData) {
        this.data = vehicleData;
    }
}
