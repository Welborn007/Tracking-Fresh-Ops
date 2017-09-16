package com.kesari.tkfops.SNS;

/**
 * Created by kesari on 19/10/16.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kesari.tkfops.R;
import com.kesari.tkfops.Splash.SplashActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Random;


public class MyFirebaseMessagingService extends FirebaseMessagingService
{
    private String TAG = this.getClass().getSimpleName();
    private Intent intent;
    Bitmap bitmap;
    String image,message;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        //MainActivity.myMsg = remoteMessage.getNotification().getBody();

        //Calling method to generate notification
        //sendNotification(remoteMessage.getNotification().getBody());

        try {

            Log.d(TAG, "Notification Message Data: " + remoteMessage.getData());
            //Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

            Map<String, String> data = remoteMessage.getData();
            Log.i("message_json",data.toString());

            if(data.get("result") != null)
            {

                JSONObject jsonObject = new JSONObject(data.get("result"));

                message = jsonObject.getString("message");

                if(jsonObject.has("image"))
                {
                    image = jsonObject.getString("image");
                }
                else
                {
                    image = "";
                }

                sendNotification(message,image);
            }

        }catch (Exception je)
        {
            je.printStackTrace();
            //Toast.makeText(this, "Push Received", Toast.LENGTH_SHORT).show();
        }
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody,String Image) {
        try
        {


            Intent intent = new Intent(this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle("TKF Ops")
                    //.setContentText(messageBody)
                    //.setLargeIcon(largeIcon)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                notificationBuilder.setSmallIcon(R.drawable.ic_stat_tkf);
            } else {
                notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
            }

            if(!Image.isEmpty())
            {
                try {
                    bitmap = BitmapFactory.decodeStream((InputStream) new URL(Image).getContent());
                    bitmap = Bitmap.createScaledBitmap(bitmap, 350, 150, false);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)).setSubText(messageBody);
            }
            else
            {
                notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody)).setContentText(messageBody);
            }

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Random random = new Random();
            int id = random.nextInt(9999 - 1000) + 1000;
            notificationManager.notify(id, notificationBuilder.build());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
