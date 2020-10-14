package com.example.donorapp.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.donorapp.NotificationActivity;
import com.example.donorapp.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class NotificationService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.e("newToken", token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("FROM", remoteMessage.getFrom());

        Log.d("TITLE", remoteMessage.getNotification().getTitle());
        Log.d("BODY",remoteMessage.getNotification().getBody());

        if (remoteMessage.getData().isEmpty()){
            notifForeground(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        } else {
            notifBackground(remoteMessage.getData());
        }
    }

    private void notifBackground(Map<String, String> data) {

        String title = data.get("title").toString();
        String body = data.get("body").toString();


        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String ChannelID = "Default";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel notificationChannel = new NotificationChannel(ChannelID, "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("New Notification");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);

            notificationManager.createNotificationChannel(notificationChannel);
        }

//        Intent i = new Intent(this, NotificationActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent bukaNotif = PendingIntent.getActivity(this, 0, i,0);

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this, ChannelID);

        notifBuilder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_message)
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info");

        notificationManager.notify(new Random().nextInt(), notifBuilder.build());

    }

    private void notifForeground(String title, String body) {

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String ChannelID = "Default";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel notificationChannel = new NotificationChannel(ChannelID, "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("New Notification");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent i = new Intent(this, NotificationActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent bukaNotif = PendingIntent.getActivity(this, 0, i,0);

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this, ChannelID);

        notifBuilder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_notif_fore)
                .setContentTitle(title)
                .setContentText(body)
                .setContentInfo("Info")
                .setContentIntent(bukaNotif);

        notificationManager.notify(new Random().nextInt(), notifBuilder.build());
    }
}
