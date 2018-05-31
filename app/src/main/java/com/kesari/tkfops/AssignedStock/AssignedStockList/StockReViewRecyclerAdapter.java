package com.kesari.tkfops.AssignedStock.AssignedStockList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kesari.tkfops.R;
import com.kesari.tkfops.Utilities.Constants;
import com.kesari.tkfops.Utilities.SharedPrefUtil;
import com.kesari.tkfops.network.IOUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by kesari on 13/06/17.
 */

public class StockReViewRecyclerAdapter extends RecyclerView.Adapter<StockReViewRecyclerAdapter.RecyclerViewHolder>{

    private List<AssignedStockListSubPojo> OrdersListReView;
    Context context;
    private String TAG = this.getClass().getSimpleName();

    public StockReViewRecyclerAdapter(List<AssignedStockListSubPojo> OrdersListReView, Context context)
    {
        this.OrdersListReView = OrdersListReView;
        this.context = context;
    }

    @Override
    public StockReViewRecyclerAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_list_accept_reject,parent,false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(StockReViewRecyclerAdapter.RecyclerViewHolder holder, final int position) {

        try {

            holder.product_name.setText(OrdersListReView.get(position).getProductName());
            holder.quantity.setText(OrdersListReView.get(position).getAssigned_quantity());
            holder.price.setText(OrdersListReView.get(position).getSelling_price());

            holder.images.setController(IOUtils.getFrescoImageController(context,OrdersListReView.get(position).getProductImage()));
            holder.images.setHierarchy(IOUtils.getFrescoImageHierarchy(context));

            holder.stock_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AcceptRejectStock(OrdersListReView.get(position).get_id(),"Accepted",OrdersListReView.get(position).getProductId());
                }
            });

            holder.stock_reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AcceptRejectStock(OrdersListReView.get(position).get_id(),"Rejected",OrdersListReView.get(position).getProductId());
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return OrdersListReView.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        TextView product_name,quantity,price;
        SimpleDraweeView images;
        FancyButton stock_accept,stock_reject;

        public RecyclerViewHolder(View view)
        {
            super(view);
            product_name = (TextView)view.findViewById(R.id.product_name);
            quantity = (TextView)view.findViewById(R.id.quantity);
            price = (TextView)view.findViewById(R.id.price);

            images = (SimpleDraweeView) view.findViewById(R.id.images);

            stock_reject = (FancyButton) view.findViewById(R.id.stock_reject);
            stock_accept = (FancyButton) view.findViewById(R.id.stock_accept);
        }
    }


    private void AcceptRejectStock(String ID, String productStatus,String productID) {
        try {

            String url = Constants.AcceptRejectStock;

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();

                postObject.put("id", ID);
                postObject.put("status",productStatus);
                postObject.put("productId",productID);
                jsonObject.put("post", postObject);

                Log.i("JSON CREATED", jsonObject.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            IOUtils ioUtils = new IOUtils();

            Map<String, String> params = new HashMap<String, String>();
            params.put("Authorization", "JWT " + SharedPrefUtil.getToken(context));

            ioUtils.sendJSONObjectPutRequestHeader(context, url, params, jsonObject, new IOUtils.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, result.toString());

                    AssignedStockListFragment.getStockList(context);

                }
            }, new IOUtils.VolleyFailureCallback() {
                @Override
                public void onFailure(String result) {

                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }
}
