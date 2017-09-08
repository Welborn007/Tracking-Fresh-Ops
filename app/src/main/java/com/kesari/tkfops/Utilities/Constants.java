package com.kesari.tkfops.Utilities;

/**
 * Created by kesari on 28/04/17.
 */

public interface Constants {

    String SheetalIP = "192.168.1.10:8000";
    String StagingIP = "192.168.1.220:8000";
    String VaibhavIP = "192.168.1.27:8000";
    String GaneshIP = "192.168.1.112:8000";
    String AlnoorIP = "192.168.1.191:8000";
    String MarutiIP = "192.168.1.148:8000";
    String LIVEIP = "52.66.75.55:8000";

    //Vehicle Login API
    String LoginVehicle = "http://" + GaneshIP + "/api/vehicle/login";

    //Biker Login API
    String LoginBiker = "http://" + GaneshIP + "/api/biker/login";

   //Send Location of driver
   String DriverLocationApi = "http://" + GaneshIP + "/api/vehicle_positions";

    //Order List
    String OrderList = "http://" + GaneshIP + "/api/order/vehicle/";

    //Order List Filter
    String OrderListFilter = "http://" + GaneshIP + "/api/order/vehicle/?status=";

    //Update Order after payment Put Request
    String UpdateOrder = "http://" + GaneshIP + "/api/order/vehicle/update";

    //get Biker Order details from order id
    String BikerOrderDetails = "http://"+ GaneshIP +"/api/bikerOrder/order/";

    //get Vehicle Order details from order id
    String VehicleOrderDetails = "http://"+ GaneshIP +"/api/vehicle/order/";

    //Vehicle Profile
    String VehicleProfile = "http://" + GaneshIP + "/api/vehicle/profile";

    //Biker Profile
    String BikerProfile = "http://" + GaneshIP + "/api/biker/profile";

    //Assigned Stock List
    String AssignedStockList = "http://" + GaneshIP + "/api/stock/vehicle/products?status=Pending";

    //Stock Accepted List
    String StockAcceptedList = "http://" + GaneshIP + "/api/stock/vehicle/products?status=Accepted";

    //Stock Rejected List
    String StockRejectedList = "http://" + GaneshIP + "/api/stock/vehicle/products?status=Rejected";

    //Accept / Reject Stock
    String AcceptRejectStock = "http://" + GaneshIP + "/api/stock/vehicle";

    //Vehicle Stock List
    String StockList = "http://" + GaneshIP + "/api/vehicleStock/";

    //Biker List
    String BikerList = "http://" + GaneshIP + "/api/vehicle/biker/list";

    //Single Biker Details
    String BikerDetails = "http://" + GaneshIP +"/api/vehicle/biker/";

    //Assign Order to Biker
    String AssignOrderBiker = "http://" + GaneshIP + "/api/vehicleStock/biker ";

    //Vehicle Route
    String VehicleRoute = "http://" + GaneshIP + "/api/vehicleTimeTable/byVehicle";

    //Biker Stock List
    String BikerStockList = "http://" + GaneshIP + "/api/bikerOrder/";

    //Biker Stock List
    String BikerOrderListFilter = "http://" + GaneshIP + "/api/bikerOrder/?status=";

    //Update Delivery Status Biker
    String BikerDeliveryStatus = "http://" + GaneshIP + "/api/bikerOrder/update";

    //Visible / InVisble Mode
    String VisibleInVisibleMode = "http://" + GaneshIP + "/api/vehicle/status";

    //Assigned Order List to Biker
    String BikerAssignedOrderList = "http://" + GaneshIP + "/api/vehicleStock/bikerOrderList?status=";

    //Cancellation / Rejection reasons
    String Reasons = "http://"+ GaneshIP + "/api/order/reason?reason=";

    //Biker Vehicle Route
    String Vehicle_BikerRoute = "http://"+ GaneshIP + "/api/biker_positions/getVehicleRoutes";

    //Biker Location
    String BikerLocation = "http://"+ GaneshIP + "/api/biker_positions/";

    //Vehicle Socket Location

    String VehicleLiveLocation = "http://" + GaneshIP;

    //Biker Socket Location

    String BikerLiveLocation = "http://" + GaneshIP;

    //Send Vehicle Nearby Push

    String NearbyVehiclePush = "http://"+ GaneshIP + "/api/vehicle/nearByPushNotification";

 // Milliseconds per second
    public static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 10;
    // Update frequency in milliseconds
    public static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    public static final int FASTEST_INTERVAL_IN_SECONDS = 3;
    // A fast frequency ceiling in milliseconds
    public static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
}
