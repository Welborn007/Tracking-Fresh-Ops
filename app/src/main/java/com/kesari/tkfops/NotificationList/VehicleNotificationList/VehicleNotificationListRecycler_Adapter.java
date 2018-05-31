package com.kesari.tkfops.NotificationList.VehicleNotificationList;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kesari.tkfops.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by kesari on 14/09/17.
 */

public class VehicleNotificationListRecycler_Adapter extends RecyclerView.Adapter<VehicleNotificationListRecycler_Adapter.RecyclerViewHolder>{

    private List<VehicleNotificationSubPOJO> NotificationListReView;
    private String TAG = this.getClass().getSimpleName();
    private Context context;

    public VehicleNotificationListRecycler_Adapter(List<VehicleNotificationSubPOJO> NotificationListReView, Context context)
    {
        this.NotificationListReView = NotificationListReView;
        this.context = context;
    }

    @Override
    public VehicleNotificationListRecycler_Adapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_row_layout,parent,false);

        VehicleNotificationListRecycler_Adapter.RecyclerViewHolder recyclerViewHolder = new VehicleNotificationListRecycler_Adapter.RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(VehicleNotificationListRecycler_Adapter.RecyclerViewHolder holder, final int position) {

        try {

            holder.notificationMessage.setText(NotificationListReView.get(position).getMessage());

            /*SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat sdfOutput = new SimpleDateFormat("dd-MM-yyyy");
            Date d = sdfInput.parse(NotificationListReView.get(position).getCreatedAt());
            String orderDateFormatted = sdfOutput.format(d);
            holder.time.setText(orderDateFormatted);
*/

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            long time = sdf.parse(NotificationListReView.get(position).getCreatedAt()).getTime();
            long now = System.currentTimeMillis();

            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);

            holder.time.setText(ago);

            holder.subItemCard_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return NotificationListReView.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        TextView notificationMessage,time;
        CardView subItemCard_view;

        public RecyclerViewHolder(View view)
        {
            super(view);

            notificationMessage = (TextView)view.findViewById(R.id.notificationMessage);
            time = (TextView)view.findViewById(R.id.time);
            subItemCard_view = (CardView) view.findViewById(R.id.subItemCard_view);
        }
    }
}
