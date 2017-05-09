package com.kesari.tkfops.DeliveredOrders;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kesari.tkfops.OpenOrders.Order_POJO;
import com.kesari.tkfops.R;

import java.util.List;

/**
 * Created by kesari on 03/05/17.
 */

public class DeliveredOrdersRecycler_Adapter extends RecyclerView.Adapter<DeliveredOrdersRecycler_Adapter.RecyclerViewHolder>{

    private List<Order_POJO> OrdersListReView;

    public DeliveredOrdersRecycler_Adapter(List<Order_POJO> OrdersListReView)
    {
        this.OrdersListReView = OrdersListReView;

    }

    @Override
    public DeliveredOrdersRecycler_Adapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.delivered_orders_rowlayout,parent,false);

        DeliveredOrdersRecycler_Adapter.RecyclerViewHolder recyclerViewHolder = new DeliveredOrdersRecycler_Adapter.RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(DeliveredOrdersRecycler_Adapter.RecyclerViewHolder holder, int position) {

        try {
            holder.order_name.setText(OrdersListReView.get(position).getOrder_id());
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
        TextView order_name;
        public RecyclerViewHolder(View view)
        {
            super(view);
            order_name = (TextView)view.findViewById(R.id.order_name);
        }
    }
}
