package com.kesari.tkfops.OrderAssignedToBiker;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kesari.tkfops.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by kesari on 25/07/17.
 */

public class BikerOrderAssignedRecycler_Adapter extends RecyclerView.Adapter<BikerOrderAssignedRecycler_Adapter.RecyclerViewHolder>{

    private List<OrderAssignedDataPOJO> OrdersListReView;
    private Context context;

    public BikerOrderAssignedRecycler_Adapter(List<OrderAssignedDataPOJO> OrdersListReView,Context context)
    {
        this.OrdersListReView = OrdersListReView;
        this.context = context;
    }

    @Override
    public BikerOrderAssignedRecycler_Adapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.biker_assigned_row_layout,parent,false);

        BikerOrderAssignedRecycler_Adapter.RecyclerViewHolder recyclerViewHolder = new BikerOrderAssignedRecycler_Adapter.RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(BikerOrderAssignedRecycler_Adapter.RecyclerViewHolder holder, final int position) {

        try {

            holder.order_number.setText(String.valueOf(position + 1));
            holder.customer_name.setText(OrdersListReView.get(position).getOrder().getCreatedBy());
            holder.bikeNo.setText(OrdersListReView.get(position).getBiker().getBikeNo());
            holder.bikerName.setText(OrdersListReView.get(position).getBiker().getBikerName());

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

            if(OrdersListReView.get(position).getOrder().getStatus().equalsIgnoreCase("Rejected"))
            {
                holder.cancelHolder.setVisibility(View.GONE);
                holder.rejectHolder.setVisibility(View.VISIBLE);
                holder.order_status.setImageResource(R.drawable.rejected);

                if(OrdersListReView.get(position).getOrder().getRejectReason() != null)
                {
                    if(!OrdersListReView.get(position).getOrder().getRejectReason().isEmpty())
                    {
                        holder.rejectReason.setText(OrdersListReView.get(position).getOrder().getRejectReason());
                        holder.rejectHolder.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        holder.rejectHolder.setVisibility(View.GONE);
                    }
                }
            }
            else if(OrdersListReView.get(position).getOrder().getStatus().equalsIgnoreCase("Accepted"))
            {
                holder.cancelHolder.setVisibility(View.GONE);
                holder.rejectHolder.setVisibility(View.GONE);
                holder.order_status.setImageResource(R.drawable.accepted);
            }
            else if(OrdersListReView.get(position).getOrder().getStatus().equalsIgnoreCase("Pending"))
            {
                holder.cancelHolder.setVisibility(View.GONE);
                holder.rejectHolder.setVisibility(View.GONE);
                holder.order_status.setImageResource(R.drawable.pending);
            }
            else if(OrdersListReView.get(position).getOrder().getStatus().equalsIgnoreCase("Cancelled"))
            {
                holder.cancelHolder.setVisibility(View.VISIBLE);
                holder.rejectHolder.setVisibility(View.GONE);
                holder.order_status.setImageResource(R.drawable.cancel);

                if(OrdersListReView.get(position).getOrder().getCancelReason() != null)
                {
                    if(!OrdersListReView.get(position).getOrder().getCancelReason().isEmpty())
                    {
                        holder.cancelReason.setText(OrdersListReView.get(position).getOrder().getCancelReason());
                        holder.cancelHolder.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        holder.cancelHolder.setVisibility(View.GONE);
                    }
                }
            }
            else if(OrdersListReView.get(position).getOrder().getStatus().equalsIgnoreCase("Delivered"))
            {
                holder.cancelHolder.setVisibility(View.GONE);
                holder.rejectHolder.setVisibility(View.GONE);
                holder.order_status.setImageResource(R.drawable.delivered);
            }


            holder.subItemCard_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String orderID = OrdersListReView.get(position).getOrder().get_id();

                    Intent intent = new Intent(context, AssignedOrderReviewActivity.class);
                    intent.putExtra("orderID",orderID);
                    intent.putExtra("bikeNo",OrdersListReView.get(position).getBiker().getBikeNo());
                    intent.putExtra("bikerName",OrdersListReView.get(position).getBiker().getBikerName());
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
        TextView order_number,customer_name,payment_confirm,payment_mode,total_price,bikeNo,bikerName,orderDate,orderNo,rejectReason,cancelReason;
        CardView subItemCard_view;
        ImageView order_status;
        LinearLayout payment_confirmHolder,payment_modeHolder,rejectHolder,cancelHolder;

        public RecyclerViewHolder(View view)
        {
            super(view);
            order_number = (TextView)view.findViewById(R.id.order_number);
            customer_name = (TextView)view.findViewById(R.id.customer_name);
            payment_confirm = (TextView)view.findViewById(R.id.payment_confirm);
            payment_mode = (TextView)view.findViewById(R.id.payment_mode);
            subItemCard_view = (CardView) view.findViewById(R.id.subItemCard_view);
            total_price = (TextView) view.findViewById(R.id.total_price);
            bikeNo = (TextView) view.findViewById(R.id.bikeNo);
            bikerName = (TextView) view.findViewById(R.id.bikerName);
            rejectReason = (TextView) view.findViewById(R.id.rejectReason);
            cancelReason = (TextView) view.findViewById(R.id.cancelReason);

            payment_confirmHolder = (LinearLayout) view.findViewById(R.id.payment_confirmHolder);
            payment_modeHolder = (LinearLayout) view.findViewById(R.id.payment_modeHolder);
            rejectHolder = (LinearLayout) view.findViewById(R.id.rejectHolder);
            cancelHolder = (LinearLayout) view.findViewById(R.id.cancelHolder);

            order_status = (ImageView) view.findViewById(R.id.order_status);
            orderDate = (TextView) view.findViewById(R.id.orderDate);
            orderNo = (TextView) view.findViewById(R.id.orderNo);

        }
    }
}