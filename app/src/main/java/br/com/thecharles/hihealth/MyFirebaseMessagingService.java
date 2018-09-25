package br.com.thecharles.hihealth;


import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

import br.com.thecharles.hihealth.activity.ChatActivity;
import br.com.thecharles.hihealth.activity.MainActivity;
import br.com.thecharles.hihealth.activity.MapsActivity;
import br.com.thecharles.hihealth.config.SettingsFirebase;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    DatabaseReference database = SettingsFirebase.getFirebaseDatabase();
    DatabaseReference firebaseRefDebug = database.child("debug");
    DatabaseReference messageRef = firebaseRefDebug.child("messages");
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private String idUserSender;
    private String idUserReceiver;




//    String KEY_REPLY = "key_reply";
    private static final String KEY_REPLY = "key_text_reply";
    public static final int NOTIFICATION_ID = 1;

//    public static CharSequence getReplyMessage(Intent intent) {
//        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
//        if (remoteInput != null) {
////            intent.putExtra("message", remoteInput.getCharSequence(KEY_REPLY).toString());
//            return remoteInput.getCharSequence(KEY_REPLY);
//        }
//        return null;
//    }



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

//    private void sendMsg() {
//        Intent it = new Intent(this,  MyFirebaseMessagingService.class);
//        CharSequence message = MyFirebaseMessagingService.getReplyMessage(it);
//        idUserSender = firebaseAuth.getCurrentUser().getUid();
//
//
//            //Set the inline reply text in the TextView
////            txtReplied.setText("Reply is "+reply);
//            Log.d(TAG, "Reply is " + message);
//
//            //Update the notification to show that the reply was received.
//
//            messageRef.child(idUserSender)
//                    .child(idUserSender)
//                    .push()
//                    .setValue(message);
//
//    }

    private void sendNotification(RemoteMessage.Notification notification,
                                  Map<String, String> data) {


        String title;
        String body;

        if(notification == null){
            title = "Hi-Health";
            body = data.get("name")+ data.get("message")
//                    + " = " + data.get("userName")
            ;
        }else{
            title = notification.getTitle();
            body  = notification.getBody();
        }


//        Intent it = new Intent(this, CustomNotificationActivity.class);
//        it.putExtra("message", data.get("message"));
////System.currentTimeMillis() is used for unique Id
//        PendingIntent contentIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), it,0);

        Intent it = new Intent(this,  MapsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("idUser", data.get("id"));
        bundle.putString("nameUser", data.get("name"));
        bundle.putString("latlngUser", data.get("latlng"));
        it.putExtras(bundle);
        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a Notification Builder instance.
        int smallIconResId = R.mipmap.ic_launcher_foreground_notifaction;
        int largeIconResId = R.drawable.ic_account_circle_black_24dp;
        long sendTime = System.currentTimeMillis();

//        BitmapDrawable bitmapDrawable = (BitmapDrawable)getDrawable(largeIconResId);
//        Bitmap largeIconBitmap = bitmapDrawable.getBitmap();

        RemoteViews collapsedView = new RemoteViews(getPackageName(), R.layout.view_collapsed_notification);
        collapsedView.setTextViewText(R.id.timestamp, DateUtils.formatDateTime(this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME));
        collapsedView.setTextViewText(R.id.content_text, data.get("name") + data.get("message"));



        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "fcm-instance-specific")
                        .setSmallIcon(smallIconResId)
//                        .setTicker(title)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_person_pin_circle_black_24dp))
                        .setContentTitle(title)
                        .setContentText(body)

//                        .setBadgeIconType(R.drawable.ic_favorite_black_24dp)
//                        .setVibrate()
                        .setAutoCancel(false)
//                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                        .setLargeIcon(largeIconBitmap)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
//                        .setContentIntent(contentIntent)
                        //Heads-up notification.
                        .setCustomContentView(collapsedView)
                        .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                        .setFullScreenIntent(pendingIntent, true)
                        .setWhen(sendTime)
                        .setDefaults(Notification.DEFAULT_ALL)
                ;
//        //Initialise RemoteInput
//        String replyLabel = "Enter your reply here";
//        RemoteInput remoteInput = new RemoteInput.Builder(KEY_REPLY)
//                .setLabel(replyLabel)
//                .build();
//
//
//
//        Intent resultIntent = new Intent(this, ChatActivity.class);
//        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
////        RemoteInput remoteInput = new RemoteInput.Builder(KEY_QUICK_REPLY)
////                .setLabel("Reply")
////                .build();
////
//
//        //Notification Action with RemoteInput instance added.
//        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
//                android.R.drawable.sym_action_chat, "REPLY", resultPendingIntent)
//                .addRemoteInput(remoteInput)
//                .setAllowGeneratedReplies(true)
//                .build();
//
//        //Notification.Action instance added to Notification Builder.
//        notificationBuilder.addAction(replyAction);
//
//        notificationBuilder.addAction(android.R.drawable.ic_menu_close_clear_cancel, "DISMISS", dismissIntent);
//
//        //Create Notification.
//        NotificationManager notificationManager =
//                (NotificationManager)
//                        getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(NOTIFICATION_ID,
//                builder.build());
//        break;






//        PendingIntent muteIntent = notificationBuilder.addAction(new NotificationCompat.Action(
//                R.drawable.ic_account_circle_black_24dp, "Mute", mu));


//
//
//        String replyLabel = "Enter your reply here";
//
//        //Initialise RemoteInput
//        RemoteInput remoteInput = new RemoteInput.Builder(KEY_REPLY)
//                .setLabel(replyLabel)
//                .build();
//
//
//        int randomRequestCode = new Random().nextInt(54325);
//
//        //PendingIntent that restarts the current activity instance.
//        Intent resultIntent = new Intent(this, MapsActivity.class);
//        resultIntent.putExtra("idUser", data.get("id"));
//        resultIntent.putExtra("nameUser", data.get("name"));
//        resultIntent.putExtra("latlngUser", data.get("latlng"));
//        resultIntent.putExtra("message", KEY_REPLY);
//        resultIntent.putExtra("notificationId", 3);
//        //Set a unique request code for this pending intent
//        PendingIntent resultPendingIntent = PendingIntent.getActivity(
//                this, randomRequestCode, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//        //Notification Action with RemoteInput instance added.
//        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
//                android.R.drawable.sym_action_chat, "REPLY", resultPendingIntent)
//                .addRemoteInput(remoteInput)
//                .setAllowGeneratedReplies(true)
//                .build();





        //Notification.Action instance added to Notification Builder.
//        notificationBuilder.addAction(replyAction);

//        Intent intent = new Intent(this, MapsActivity.class);
//        intent.putExtra("notificationId", NOTIFICATION_ID);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//
//        PendingIntent dismissIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//
//
//        notificationBuilder.addAction(android.R.drawable.ic_menu_close_clear_cancel, "DISMISS", dismissIntent);

        //Create Notification.
//        NotificationManager notificationManager =
//                (NotificationManager)
//                        getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(NOTIFICATION_ID,
//                builder.build());

//        Intent intent = new Intent(this, MyFirebaseMessagingService.class);
//        intent.putExtra("notificationId", NOTIFICATION_ID);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent dismissIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//
//
//        notificationBuilder.addAction(android.R.drawable.ic_menu_close_clear_cancel, "DISMISS", dismissIntent);


//        Intent intentDismiss = new Intent(this, ChatActivity.class);
//        intentDismiss.putExtra("notificationId", NOTIFICATION_ID);
//        intentDismiss.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent dismissIntent = PendingIntent.getActivity(getBaseContext(), 0, it, PendingIntent.FLAG_CANCEL_CURRENT);
//        NotificationCompat.Action doneAction =
//                new NotificationCompat.Action.Builder(android.R.drawable.ic_menu_close_clear_cancel,
//                        "Close", dismissIntent)
//                        .build();
//
//        notificationBuilder.addAction(android.R.drawable.ic_menu_close_clear_cancel, "DISMISS", dismissIntent);



        // Create Notification instance.
        Notification alert = notificationBuilder.build();
        alert.flags =
                //Noticaçao permanente
//                Notification.FLAG_ONGOING_EVENT |
                Notification.FLAG_SHOW_LIGHTS
        //Vibrar até olhar a mensagem
                        | Notification.FLAG_INSISTENT
        ;

//        processInlineReply(resultIntent);

        notificationManager.notify(2, alert);


    }


}
