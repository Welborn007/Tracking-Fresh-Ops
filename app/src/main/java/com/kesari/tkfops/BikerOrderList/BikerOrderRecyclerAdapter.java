package com.kesari.tkfops.BikerOrderList;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kesari.tkfops.BikerOrderReview.BikerOrderReviewActivity;
import com.kesari.tkfops.R;
import com.kesari.tkfops.Utilities.Constants;
import com.kesari.tkfops.Utilities.SharedPrefUtil;
import com.kesari.tkfops.network.IOUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by kesari on 03/07/17.
 */

public class BikerOrderRecyclerAdapter extends RecyclerView.Adapter<BikerOrderRecyclerAdapter.RecyclerViewHolder>{

    private List<BikerOrderSubPOJO> OrdersListReView;
    Context context;
    private String TAG = this.getClass().getSimpleName();

    public BikerOrderRecyclerAdapter(List<BikerOrderSubPOJO> OrdersListReView,Context context)
    {
        this.OrdersListReView = OrdersListReView;
        this.context = context;
    }

    @Override
    public BikerOrderRecyclerAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.biker_order_rowlayout,parent,false);

        BikerOrderRecyclerAdapter.RecyclerViewHolder recyclerViewHolder = new BikerOrderRecyclerAdapter.RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(BikerOrderRecyclerAdapter.RecyclerViewHolder holder, final int position) {

        try {

            holder.order_number.setText(String.valueOf(position + 1));
            holder.customer_name.setText(OrdersListReView.get(position).getOrder().getCreatedBy());
            holder.orderNo.setText(OrdersListReView.get(position).getOrder().getOrderNo());

            SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat sdfOutput = new SimpleDateFormat("dd-MM-yyyy");
            Date d = sdfInput.parse(OrdersListReView.get(position).getOrder().getCreatedAt());
            String orderDateFormatted = sdfOutput.format(d);
            holder.orderDate.setText(orderDateFormatted);

            if(OrdersListReView.get(position).getOrder().getPayment_Status() == null)
            {
                holder.payment_confirmHolder.setVisibility(View.GONE);
            }
            else
            {
                holder.payment_confirmHolder.setVisibility(View.VISIBLE);
                holder.payment_confirm.setText(OrdersListReView.get(position).getOrder().getPayment_Status());
            }

            if(OrdersListReView.get(position).getOrder().getPayment_Mode() == null)
            {
                holder.payment_modeHolder.setVisibility(View.GONE);
            }
            else
            {
                holder.payment_modeHolder.setVisibility(View.VISIBLE);
                holder.payment_mode.setText(OrdersListReView.get(position).getOrder().getPayment_Mode());
            }

            holder.total_price.setText(OrdersListReView.get(position).getOrder().getTotal_price());

            if(OrdersListReView.get(position).getStatus().equalsIgnoreCase("Rejected"))
            {
                holder.order_status.setImageResource(R.drawable.rejected);
            }
            else if(OrdersListReView.get(position).getStatus().equalsIgnoreCase("Accepted"))
            {
                holder.order_status.setImageResource(R.drawable.accepted);
            }
            else if(OrdersListReView.get(position).getStatus().equalsIgnoreCase("Pending"))
            {
                holder.order_status.setImageResource(R.drawable.pending);
            }
            else if(OrdersListReView.get(position).getStatus().equalsIgnoreCase("Cancelled"))
            {
                holder.order_status.setImageResource(R.drawable.cancel);
            }
            else if(OrdersListReView.get(position).getStatus().equalsIgnoreCase("Delivered"))
            {
                holder.order_status.setImageResource(R.drawable.delivered);
            }

            holder.delivered.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateOrderDetails(OrdersListReView.get(position).getOrder().get_id(),"Delivered","Received");
                }
            });

            holder.subItemCard_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String orderID = OrdersListReView.get(position).getOrder().get_id();

                    Intent intent = new Intent(context, BikerOrderReviewActivity.class);
                    intent.putExtra("orderID",orderID);
                    context.startActivity(intent);
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
        TextView order_number,customer_name,payment_confirm,payment_mode,total_price,orderDate,orderNo;
        CardView subItemCard_view;
        ImageView order_status;
        LinearLayout payment_confirmHolder,payment_modeHolder;

        FancyButton delivered;

        public RecyclerViewHolder(View view)
        {
            super(view);
            order_number = (TextView)view.findViewById(R.id.order_number);
            customer_name = (TextView)view.findViewById(R.id.customer_name);
            payment_confirm = (TextView)view.findViewById(R.id.payment_confirm);
            payment_mode = (TextView)view.findViewById(R.id.payment_mode);
            subItemCard_view = (CardView) view.findViewById(R.id.subItemCard_view);
            total_price = (TextView) view.findViewById(R.id.total_price);
            orderDate = (TextView) view.findViewById(R.id.orderDate);
            orderNo = (TextView) view.findViewById(R.id.orderNo);

            payment_confirmHolder = (LinearLayout) view.findViewById(R.id.payment_confirmHolder);
            payment_modeHolder = (LinearLayout) view.findViewById(R.id.payment_modeHolder);

            order_status = (ImageView) view.findViewById(R.id.order_status);

            delivered = (FancyButton) view.findViewById(R.id.delivered);

        }
    }

    private void updateOrderDetails(String orderID, String OrderStatus,String PaymentStatus) {
        try {

            String url = Constants.BikerDeliveryStatus;

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();

                postObject.put("id", orderID);
                postObject.put("status",OrderStatus);

                if(OrderStatus.equalsIgnoreCase("Delivered"))
                {
                    postObject.put("payment_Status",PaymentStatus);
                }

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

                    UpdateResponse(result);
                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void UpdateResponse(String Response)
    {
        try
        {

            JSONObject jsonObject = new JSONObject(Response);

            String message = jsonObject.getString("message");

            if(message.equalsIgnoreCase("Updated Successfull!!"))
            {
                BikerOpenOrderFragment.getOrderList(context);
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

}