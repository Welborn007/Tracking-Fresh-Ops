package com.kesari.tkfops.OpenOrders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kesari.tkfops.BikerList.DriverListActivity;
import com.kesari.tkfops.Customer.CustomerMapActivity;
import com.kesari.tkfops.R;
import com.kesari.tkfops.Utilities.Constants;
import com.kesari.tkfops.Utilities.SharedPrefUtil;
import com.kesari.tkfops.VehicleOrderReview.VehicleOrderReviewActivity;
import com.kesari.tkfops.network.IOUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kesari-Aniket on 8/1/16.
 */

public class OrdersListRecycler_Adapter extends RecyclerView.Adapter<OrdersListRecycler_Adapter.RecyclerViewHolder>{

    private List<OrderSubPOJO> OrdersListReView;
    Context context;
    private String TAG = this.getClass().getSimpleName();

    public OrdersListRecycler_Adapter(List<OrderSubPOJO> OrdersListReView,Context context)
    {
        this.OrdersListReView = OrdersListReView;
        this.context = context;
    }

    @Override
    public OrdersListRecycler_Adapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_list_rowlayout,parent,false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(OrdersListRecycler_Adapter.RecyclerViewHolder holder, final int position) {

        try {

            holder.order_number.setText(String.valueOf(position + 1));
            holder.customer_name.setText(OrdersListReView.get(position).getCreatedBy());


            if(OrdersListReView.get(position).getPayment_Status() == null)
            {
                holder.payment_confirmHolder.setVisibility(View.GONE);
            }
            else
            {
                holder.payment_confirmHolder.setVisibility(View.VISIBLE);
                holder.payment_confirm.setText(OrdersListReView.get(position).getPayment_Status());
            }

            if(OrdersListReView.get(position).getPayment_Mode() == null)
            {
                holder.payment_modeHolder.setVisibility(View.GONE);
            }
            else
            {
                holder.payment_modeHolder.setVisibility(View.VISIBLE);
                holder.payment_mode.setText(OrdersListReView.get(position).getPayment_Mode());
            }

            holder.total_price.setText(OrdersListReView.get(position).getTotal_price());

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

            if(OrdersListReView.get(position).getStatus().equalsIgnoreCase("Rejected"))
            {
                holder.assign.setVisibility(View.GONE);
                holder.accept.setVisibility(View.VISIBLE);
                holder.reject.setVisibility(View.GONE);
            }
            else if(OrdersListReView.get(position).getStatus().equalsIgnoreCase("Accepted"))
            {
                holder.assign.setVisibility(View.VISIBLE);
                holder.reject.setVisibility(View.VISIBLE);
                holder.accept.setVisibility(View.GONE);
            }
            else if(OrdersListReView.get(position).getStatus().equalsIgnoreCase("Pending"))
            {
                holder.accept.setVisibility(View.VISIBLE);
                holder.reject.setVisibility(View.VISIBLE);
                holder.assign.setVisibility(View.GONE);
            }
            else if(OrdersListReView.get(position).getStatus().equalsIgnoreCase("Cancelled"))
            {
                holder.accept.setVisibility(View.GONE);
                holder.reject.setVisibility(View.GONE);
                holder.assign.setVisibility(View.GONE);
            }
            else if(OrdersListReView.get(position).getStatus().equalsIgnoreCase("Delivered"))
            {
                holder.order_status.setImageResource(R.drawable.delivered);
                holder.accept.setVisibility(View.GONE);
                holder.reject.setVisibility(View.GONE);
                holder.assign.setVisibility(View.GONE);
                holder.path.setVisibility(View.GONE);
            }

            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateOrderDetails(OrdersListReView.get(position).get_id(),"Accepted");
                }
            });

            holder.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateOrderDetails(OrdersListReView.get(position).get_id(),"Rejected");
                }
            });

            holder.assign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String orderID = OrdersListReView.get(position).get_id();

                    Intent intent = new Intent(context, DriverListActivity.class);
                    intent.putExtra("orderID",orderID);
                    context.startActivity(intent);
                }
            });

            holder.path.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CustomerMapActivity.class);
                    intent.putExtra("place",OrdersListReView.get(position).getAddress().getFullName());
                    intent.putExtra("id",OrdersListReView.get(position).getAddressId());
                    intent.putExtra("Lat", Double.parseDouble(OrdersListReView.get(position).getAddress().getLatitude()));
                    intent.putExtra("Lon", Double.parseDouble(OrdersListReView.get(position).getAddress().getLongitude()));
                    context.startActivity(intent);
                }
            });

            /*holder.pending.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateOrderDetails(OrdersListReView.get(position).get_id(),"Pending",position);
                }
            });

            holder.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateOrderDetails(OrdersListReView.get(position).get_id(),"Cancelled",position);
                }
            });*/

            holder.subItemCard_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String orderID = OrdersListReView.get(position).get_id();

                    Intent intent = new Intent(context, VehicleOrderReviewActivity.class);
                    intent.putExtra("orderID",orderID);
                    context.startActivity(intent);
                }
            });

            //holder.distance_txt.setText(OrdersListReView.get(position).getDistance());
            //holder.time_txt.setText(OrdersListReView.get(position).getTime());
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
        TextView order_number,customer_name,payment_confirm,payment_mode,time_txt,distance_txt,total_price;
        CardView subItemCard_view;
        ImageView order_status;
        LinearLayout payment_confirmHolder,payment_modeHolder;

        Button accept,reject,pending,cancel,assign,path;

        public RecyclerViewHolder(View view)
        {
            super(view);
            order_number = (TextView)view.findViewById(R.id.order_number);
            customer_name = (TextView)view.findViewById(R.id.customer_name);
            payment_confirm = (TextView)view.findViewById(R.id.payment_confirm);
            payment_mode = (TextView)view.findViewById(R.id.payment_mode);
            distance_txt = (TextView) view.findViewById(R.id.distance_txt);
            time_txt = (TextView) view.findViewById(R.id.time_txt);
            subItemCard_view = (CardView) view.findViewById(R.id.subItemCard_view);
            total_price = (TextView) view.findViewById(R.id.total_price);

            payment_confirmHolder = (LinearLayout) view.findViewById(R.id.payment_confirmHolder);
            payment_modeHolder = (LinearLayout) view.findViewById(R.id.payment_modeHolder);

            order_status = (ImageView) view.findViewById(R.id.order_status);

            accept = (Button) view.findViewById(R.id.accept);
            reject = (Button) view.findViewById(R.id.reject);
            pending = (Button) view.findViewById(R.id.pending);
            cancel = (Button) view.findViewById(R.id.cancel);

            assign = (Button) view.findViewById(R.id.assign);
            path = (Button) view.findViewById(R.id.path);
        }
    }

    private void updateOrderDetails(String orderID, String OrderStatus) {
        try {

            String url = Constants.UpdateOrder;

            JSONObject jsonObject = new JSONObject();

            try {

                JSONObject postObject = new JSONObject();

                postObject.put("id", orderID);
                postObject.put("status",OrderStatus);

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

                    PaymentUpdateResponse(result);
                }
            });

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void PaymentUpdateResponse(String Response)
    {
        try
        {

            JSONObject jsonObject = new JSONObject(Response);

            String message = jsonObject.getString("message");

            if(message.equalsIgnoreCase("Updated Successfull!!"))
            {
                OpenOrderFragment.getOrderList(context);
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

}
