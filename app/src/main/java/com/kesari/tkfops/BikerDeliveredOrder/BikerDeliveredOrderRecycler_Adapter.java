package com.kesari.tkfops.BikerDeliveredOrder;

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

import com.kesari.tkfops.BikerOrderList.BikerOrderSubPOJO;
import com.kesari.tkfops.BikerOrderReview.BikerOrderReviewActivity;
import com.kesari.tkfops.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by kesari on 04/07/17.
 */

public class BikerDeliveredOrderRecycler_Adapter extends RecyclerView.Adapter<BikerDeliveredOrderRecycler_Adapter.RecyclerViewHolder>{

    private List<BikerOrderSubPOJO> OrdersListReView;
    Context context;

    public BikerDeliveredOrderRecycler_Adapter(List<BikerOrderSubPOJO> OrdersListReView,Context context)
    {
        this.OrdersListReView = OrdersListReView;
        this.context = context;
    }

    @Override
    public BikerDeliveredOrderRecycler_Adapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.biker_delivered_order_rowlayout,parent,false);

        BikerDeliveredOrderRecycler_Adapter.RecyclerViewHolder recyclerViewHolder = new BikerDeliveredOrderRecycler_Adapter.RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(BikerDeliveredOrderRecycler_Adapter.RecyclerViewHolder holder, final int position) {

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
        TextView order_number,customer_name,payment_confirm,payment_mode,time_txt,distance_txt,total_price,orderDate,orderNo;
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

            orderDate = (TextView) view.findViewById(R.id.orderDate);
            orderNo = (TextView) view.findViewById(R.id.orderNo);
        }
    }
}
