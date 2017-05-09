package com.kesari.tkfops.OpenOrders;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kesari.tkfops.Map.JSON_POJO;
import com.kesari.tkfops.OrderDetailsSection.OrdersProductListActivity;
import com.kesari.tkfops.R;

import java.util.List;

/**
 * Created by kesari-Aniket on 8/1/16.
 */

public class OpenOrdersRecycler_Adapter extends RecyclerView.Adapter<OpenOrdersRecycler_Adapter.RecyclerViewHolder>{

    private List<JSON_POJO> OrdersListReView;
    private Double latitude,longitude;

    public OpenOrdersRecycler_Adapter(List<JSON_POJO> OrdersListReView,Double latitude,Double longitude)
    {
        this.OrdersListReView = OrdersListReView;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public OpenOrdersRecycler_Adapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.open_orders_rowlayout,parent,false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(OpenOrdersRecycler_Adapter.RecyclerViewHolder holder, final int position) {

        try {

            holder.order_number.setText(OrdersListReView.get(position).getId());
            holder.customer_name.setText(OrdersListReView.get(position).getCustomer_name());
            holder.payment_confirm.setText(OrdersListReView.get(position).getPayment_confirmation());
            holder.payment_mode.setText(OrdersListReView.get(position).getPayment_mode());

            holder.subItemCard_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), OrdersProductListActivity.class);
                    intent.putExtra("order_id",OrdersListReView.get(position).getId());
                    intent.putExtra("customer_name",OrdersListReView.get(position).getCustomer_name());
                    intent.putExtra("payment_mode",OrdersListReView.get(position).getPayment_mode());
                    intent.putExtra("payment_confirm",OrdersListReView.get(position).getPayment_confirmation());
                    intent.putExtra("latitude",OrdersListReView.get(position).getLatitude());
                    intent.putExtra("longitude",OrdersListReView.get(position).getLongitude());
                    v.getContext().startActivity(intent);
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
        TextView order_number,customer_name,payment_confirm,payment_mode,time_txt,distance_txt;
        CardView subItemCard_view;
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
        }
    }


}
