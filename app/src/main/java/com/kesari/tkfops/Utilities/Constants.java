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
    String LoginVehicle = "http://" + LIVEIP + "/api/vehicle/login";

    //Biker Login API
    String LoginBiker = "http://" + LIVEIP + "/api/biker/login";

   //Send Location of driver
   String DriverLocationApi = "http://" + LIVEIP + "/api/vehicle_positions";

    //Order List
    String OrderList = "http://" + LIVEIP + "/api/order/vehicle/";

    //Order List Filter
    String OrderListFilter = "http://" + LIVEIP + "/api/order/vehicle/?status=";

    //Update Order after payment Put Request
    String UpdateOrder = "http://" + LIVEIP + "/api/order/vehicle/update";

    //get Biker Order details from order id
    String BikerOrderDetails = "http://"+ LIVEIP +"/api/bikerOrder/order/";

    //get Vehicle Order details from order id
    String VehicleOrderDetails = "http://"+ LIVEIP +"/api/vehicle/order/";

    //Vehicle Profile
    String VehicleProfile = "http://" + LIVEIP + "/api/vehicle/profile";

    //Biker Profile
    String BikerProfile = "http://" + LIVEIP + "/api/biker/profile";

    //Assigned Stock List
    String AssignedStockList = "http://" + LIVEIP + "/api/stock/vehicle/products?status=Pending";

    //Stock Accepted List
    String StockAcceptedList = "http://" + LIVEIP + "/api/stock/vehicle/products?status=Accepted";

    //Stock Rejected List
    String StockRejectedList = "http://" + LIVEIP + "/api/stock/vehicle/products?status=Rejected";

    //Accept / Reject Stock
    String AcceptRejectStock = "http://" + LIVEIP + "/api/stock/vehicle";

    //Vehicle Stock List
    String StockList = "http://" + LIVEIP + "/api/vehicleStock/";

    //Biker List
    String BikerList = "http://" + LIVEIP + "/api/vehicle/biker/list";

    //Single Biker Details
    String BikerDetails = "http://" + LIVEIP +"/api/vehicle/biker/";

    //Assign Order to Biker
    String AssignOrderBiker = "http://" + LIVEIP + "/api/vehicleStock/biker ";

    //Vehicle Route
    String VehicleRoute = "http://" + LIVEIP + "/api/vehicleTimeTable/byVehicle";

    //Biker Stock List
    String BikerStockList = "http://" + LIVEIP + "/api/bikerOrder/";

    //Biker Stock List
    String BikerOrderListFilter = "http://" + LIVEIP + "/api/bikerOrder/?status=";

    //Update Delivery Status Biker
    String BikerDeliveryStatus = "http://" + LIVEIP + "/api/bikerOrder/update";

    //Visible / InVisble Mode
    String VisibleInVisibleMode = "http://" + LIVEIP + "/api/vehicle/status";

    //Assigned Order List to Biker
    String BikerAssignedOrderList = "http://" + LIVEIP + "/api/vehicleStock/bikerOrderList?status=";

    //Cancellation / Rejection reasons
    String Reasons = "http://"+ LIVEIP + "/api/order/reason?reason=";

    //Biker Vehicle Route
    String Vehicle_BikerRoute = "http://"+ LIVEIP + "/api/biker_positions/getVehicleRoutes";

    //Biker Location
    String BikerLocation = "http://"+ LIVEIP + "/api/biker_positions/";

    //Vehicle Socket Location

    String VehicleLiveLocation = "http://" + LIVEIP;

    //Biker Socket Location

    String BikerLiveLocation = "http://" + LIVEIP;

    //Send Vehicle Nearby Push

    String NearbyVehiclePush = "http://"+ LIVEIP + "/api/vehicle/nearByPushNotification";

    //Send Vehicle Firebase Token

    String VehicleFBT = "http://"+ LIVEIP + "/api/vehicle/fbt";

    //Send Biker Firebase Token

    String BikerFBT = "http://"+ LIVEIP + "/api/biker/fbt";

    //Biker Push Notifications

    String BikerNotificationList = "http://"+ LIVEIP + "/api/biker/pushNotifications";

    //Vehicle Push Notifications

    String VehicleNotificationList = "http://"+ LIVEIP + "/api/vehicle/pushNotifications";

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
