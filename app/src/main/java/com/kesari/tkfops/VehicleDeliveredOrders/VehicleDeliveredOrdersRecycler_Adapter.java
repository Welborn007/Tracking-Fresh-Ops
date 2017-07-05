package com.kesari.tkfops.VehicleDeliveredOrders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kesari.tkfops.OpenOrders.OrderSubPOJO;
import com.kesari.tkfops.R;
import com.kesari.tkfops.VehicleOrderReview.VehicleOrderReviewActivity;

import java.util.List;

/**
 * Created by kesari on 03/05/17.
 */

public class VehicleDeliveredOrdersRecycler_Adapter extends RecyclerView.Adapter<VehicleDeliveredOrdersRecycler_Adapter.RecyclerViewHolder>{

    private List<OrderSubPOJO> OrdersListReView;
    Context context;

    public VehicleDeliveredOrdersRecycler_Adapter(List<OrderSubPOJO> OrdersListReView,Context context)
    {
        this.OrdersListReView = OrdersListReView;
        this.context = context;
    }

    @Override
    public VehicleDeliveredOrdersRecycler_Adapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.delivered_orders_rowlayout,parent,false);

        VehicleDeliveredOrdersRecycler_Adapter.RecyclerViewHolder recyclerViewHolder = new VehicleDeliveredOrdersRecycler_Adapter.RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(VehicleDeliveredOrdersRecycler_Adapter.RecyclerViewHolder holder, final int position) {

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


            holder.subItemCard_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String orderID = OrdersListReView.get(position).get_id();

                    Intent intent = new Intent(context, VehicleOrderReviewActivity.class);
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
}
