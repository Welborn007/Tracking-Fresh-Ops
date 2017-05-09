package com.kesari.tkfops.DeliveredOrders;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.kesari.tkfops.OpenOrders.Order_POJO;
import com.kesari.tkfops.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DeliveredOrderActivity extends AppCompatActivity {

    private RecyclerView.Adapter adapterOrders;
    private RecyclerView recListOrders;
    private LinearLayoutManager Orders;
    List<Order_POJO> jsonIndiaModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivered_order);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Order Delivered");

        recListOrders = (RecyclerView) findViewById(R.id.recyclerView);
        recListOrders.setHasFixedSize(true);
        Orders = new LinearLayoutManager(this);
        Orders.setOrientation(LinearLayoutManager.VERTICAL);
        recListOrders.setLayoutManager(Orders);

        getData();
    }

    public void getData()
    {
        try {
            JSONArray jsonArray = new JSONArray(loadJSONFromAsset());

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jo_inside = jsonArray.getJSONObject(i);

                Order_POJO js = new Order_POJO();

                String order_id = jo_inside.getString("order_id");
                String id = jo_inside.getString("id");

                js.setId(id);
                js.setOrder_id(order_id);

                jsonIndiaModelList.add(js);

            }

            adapterOrders = new DeliveredOrdersRecycler_Adapter(jsonIndiaModelList);
            recListOrders.setAdapter(adapterOrders);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("orders");
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
