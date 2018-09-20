package br.com.thecharles.hihealth;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FcmDeviceSpecificMsgSer";

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
            title = "New Item Details";
            body = data.get("itemName")+" "+data.get("itemPrice")+" "+data.get("location")
//                    + " = " + data.get("userName")
            ;
        }else{
            title = notification.getTitle();
            body  = notification.getBody();
        }


//        Intent intent = new Intent(this,  DeviceSepecificMessageFCMActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
//                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "fcm-instance-specific")
                        .setSmallIcon(R.drawable.ic_favorite_black_24dp)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
//                        .setContentIntent(pendingIntent)
                ;

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
