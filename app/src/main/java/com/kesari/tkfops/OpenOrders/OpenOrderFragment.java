package com.kesari.tkfops.OpenOrders;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kesari.tkfops.Map.GPSTracker;
import com.kesari.tkfops.Map.JSON_POJO;
import com.kesari.tkfops.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kesari on 04/05/17.
 */

public class OpenOrderFragment extends Fragment {

    private RecyclerView.Adapter adapterOrders;
    private RecyclerView recListOrders;
    private LinearLayoutManager Orders;
    List<JSON_POJO> jsonIndiaModelList = new ArrayList<>();
    private GPSTracker gps;


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

        recListOrders.setHasFixedSize(true);
        Orders = new LinearLayoutManager(getActivity());
        Orders.setOrientation(LinearLayoutManager.VERTICAL);
        recListOrders.setLayoutManager(Orders);

        gps = new GPSTracker(getActivity());

        getData();
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

            adapterOrders = new OpenOrdersRecycler_Adapter(jsonIndiaModelList,gps.getLatitude(),gps.getLongitude());
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