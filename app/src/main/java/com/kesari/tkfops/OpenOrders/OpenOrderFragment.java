package com.kesari.tkfops.OpenOrders;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.kesari.tkfops.Map.JSON_POJO;
import com.kesari.tkfops.R;
import com.kesari.tkfops.Utilities.Constants;
import com.kesari.tkfops.Utilities.SharedPrefUtil;
import com.kesari.tkfops.network.IOUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kesari on 04/05/17.
 */

public class OpenOrderFragment extends Fragment {

    private static RecyclerView.Adapter adapterOrders;
    private static RecyclerView recListOrders;
    private LinearLayoutManager Orders;
    List<JSON_POJO> jsonIndiaModelList = new ArrayList<>();
    //private GPSTracker gps;
    private String TAG = this.getClass().getSimpleName();

    private static Gson gson;
    private static OrderMainPOJO orderMainPOJO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View V = inflater.inflate(R.layout.fragment_open_order, container, false);
        recListOrders = (RecyclerView) V.findViewById(R.id.recyclerView);

        return V;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        try
        {

            //gps = new GPSTracker(getActivity());

            gson = new Gson();

            recListOrders.setHasFixedSize(true);
            Orders = new LinearLayoutManager(getActivity());
            Orders.setOrientation(LinearLayoutManager.VERTICAL);
            recListOrders.setLayoutManager(Orders);


            //getData();
            getOrderList(getActivity());

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    public static void getOrderList(final Context context)
    {
        try
        {
            String url = Constants.OrderList;

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6IndlbGJvcm5tYWNoYWRvQGdtYWlsLmNvbSIsImlhdCI6MTQ5NzI1ODU0Miwic3ViIjoiY2oyZnUxOXZ1MDAwMm1uMXR0cGVjZ2hyeiJ9.LO3nS6c_Epckw5g7wquxatGyAGgrOlSrIbydbl6U02o");

            ioUtils.getGETStringRequestHeader(context, url , params , new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d("OPenOrder", result.toString());

                    getOrderListResponse(result,context);
                }
            });

        } catch (Exception e) {
            Log.i("OPenOrder", e.getMessage());
        }
    }

    private static void getOrderListResponse(String Response,Context context)
    {
        try
        {
            orderMainPOJO = gson.fromJson(Response, OrderMainPOJO.class);


            adapterOrders = new OrdersListRecycler_Adapter(orderMainPOJO.getData(),context);
            recListOrders.setAdapter(adapterOrders);

        } catch (Exception e) {
            Log.i("Openorder", e.getMessage());
        }
    }

    public void getData() {
        try {
            JSONArray jsonArray = new JSONArray(loadJSONFromAsset());

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jo_inside = jsonArray.getJSONObject(i);

                JSON_POJO js = new JSON_POJO();

                String location_name = jo_inside.getString("location_name");
                Double latitude = jo_inside.getDouble("latitude");
                Double longitude = jo_inside.getDouble("longitude");
                String id = jo_inside.getString("id");
                String customer_name = jo_inside.getString("customer_name");
                String payment_mode = jo_inside.getString("payment_mode");
                String payment_confirmation = jo_inside.getString("payment_confirmation");

                //getMapsApiDirectionsUrl(latitude,longitude);

                js.setId(id);
                js.setLatitude(latitude);
                js.setLongitude(longitude);
                js.setLocation_name(location_name);
                js.setCustomer_name(customer_name);
                js.setPayment_mode(payment_mode);
                js.setPayment_confirmation(payment_confirmation);
                /*js.setDistance(distance);
                js.setTime(duration);*/

                jsonIndiaModelList.add(js);

            }

            adapterOrders = new OpenOrdersRecycler_Adapter(jsonIndiaModelList, SharedPrefUtil.getLocation(getActivity()).getLatitude(),SharedPrefUtil.getLocation(getActivity()).getLongitude());
            recListOrders.setAdapter(adapterOrders);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open("mock_data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}