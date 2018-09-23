package br.com.thecharles.hihealth;


import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import br.com.thecharles.hihealth.activity.MapsActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "Message from server " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            sendNotification(null, remoteMessage.getData());
        }
        if (remoteMessage.getNotification() != null) {
            sendNotification(remoteMessage.getNotification(), null);
        }
    }

    private void sendNotification(RemoteMessage.Notification notification,
                                  Map<String, String> data) {


        String title;
        String body;

        if(notification == null){
            title = "SOS";
            body = data.get("name")+ data.get("message")
//                    + " = " + data.get("userName")
            ;
        }else{
            title = notification.getTitle();
            body  = notification.getBody();
        }


        Intent intent = new Intent(this,  MapsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("idUser", data.get("id"));
        bundle.putString("nameUser", data.get("name"));
        bundle.putString("latlngUser", data.get("latlng"));
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);


        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "fcm-instance-specific")
                        .setSmallIcon(R.mipmap.ic_launcher_foreground_notifaction)
                        .setContentTitle(title)
                        .setContentText(body)
//                        .setBadgeIconType(R.drawable.ic_favorite_black_24dp)
//                        .setVibrate()
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(body))
                        .setContentIntent(pendingIntent)
                ;


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        NotificationCompat.MessagingStyle.Message message2 =
//                new NotificationCompat.MessagingStyle.Message(data.get("nameUser"));


        notificationManager.notify(0, notificationBuilder.build());
    }




}
