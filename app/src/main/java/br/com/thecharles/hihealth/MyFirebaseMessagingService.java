package br.com.thecharles.hihealth;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

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
//    public static final int NOTIFICATION_ID = 1;



    private static final String KEY_TEXT_REPLY = "key_text_reply";
    int mRequestCode = 1000;

    private static final String ALERT_ID = "channel_alert";
    private static final String MESSAGE_ID = "channel_message";


//    public static final int REPLY_INTENT_ID = 0;
//    public static final int ARCHIVE_INTENT_ID = 1;
//    public static final int REMOTE_INPUT_ID = 1247;
//    public static final String LABEL_REPLY = "Reply";
//    public static final String LABEL_ARCHIVE = "Archive";
//    public static final String REPLY_ACTION = "br.com.thecharles.hihealth.ACTION_MESSAGE_REPLY";
//    public static final String KEY_PRESSED_ACTION = "KEY_PRESSED_ACTION";
//    //    public static final String KEY_TEXT_REPLY = "KEY_TEXT_REPLY";
//    private static final String KEY_NOTIFICATION_GROUP = "KEY_NOTIFICATION_GROUP";


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
//            showBundleNotification();
            getMessages(null, remoteMessage.getData());

        }
        if (remoteMessage.getNotification() != null) {

            sendNotification(remoteMessage.getNotification(), null);
            getMessages(remoteMessage.getNotification(), null);
//            showBundleNotification();

        }
    }

    private void getMessages(RemoteMessage.Notification message, Map<String, String> data) {

        String title;
        String body;

        if(message == null){
            title = data.get("senderName") + " enviou uma mensagem";
            body = data.get("senderMessage")
//                    + " = " + data.get("userName")
            ;
        }else{
            title = message.getTitle();
            body  = message.getBody();
        }

        if (body != null) {

            Intent intent = new Intent(MyFirebaseMessagingService.this, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("idUser", data.get("senderId"));
            bundle.putString("nameUser", data.get("senderName"));
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            TaskStackBuilder taskStack = TaskStackBuilder.create(this);
            taskStack.addParentStack(MainActivity.class);
            taskStack.addNextIntent(intent);


            String reply = "RESPONDER";
            RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                    .setLabel("Mensagem")
                    .build();

            PendingIntent pendingIntent = taskStack.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);


            int smallIconResId = R.mipmap.ic_launcher_foreground_notifaction;


            NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                    R.mipmap.ic_launcher_round, reply, pendingIntent)
                    .addRemoteInput(remoteInput).build();


            Notification builder = new NotificationCompat.Builder(this, MESSAGE_ID)
                    // set title, message, etc.
                    .setAutoCancel(true)
                    .setSmallIcon(smallIconResId)
                    .addAction(action)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
//                        .setContentIntent(contentIntent)
                    //Heads-up notification.
//                        .setCustomContentView(collapsedView)
//                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setFullScreenIntent(pendingIntent, true)
//                .setWhen(sendTime)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(pendingIntent)
                    .build();

            //         Create Notification instance.
            Notification alert = builder;
            alert.flags =
                    //Noticaçao permanente
//                Notification.FLAG_ONGOING_EVENT |
                    Notification.FLAG_SHOW_LIGHTS
            //Vibrar até olhar a mensagem
//                        | Notification.FLAG_INSISTENT
            ;


            NotificationManager manager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Support for Android Oreo: Notification Channels
                NotificationChannel channel = new NotificationChannel(
                        MESSAGE_ID,
                        "Mensagens",
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.enableLights(true);
                channel.enableVibration(true);
                manager.createNotificationChannel(channel);
            }



//        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(mRequestCode, builder);


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

            if (notification == null) {
                title = "Hi-Health";
                body = data.get("message")
//                    + " = " + data.get("userName")
                ;
            } else {
                title = notification.getTitle();
                body = notification.getBody();
            }



//        Intent it = new Intent(this, CustomNotificationActivity.class);
//        it.putExtra("message", data.get("message"));
////System.currentTimeMillis() is used for unique Id
//        PendingIntent contentIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), it,0);

        if (body != null) {

            Intent intent = new Intent(MyFirebaseMessagingService.this, MapsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("idUser", data.get("id"));
            bundle.putString("nameUser", data.get("name"));
            bundle.putString("latlngUser", data.get("latlng"));
            intent.putExtras(bundle);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            TaskStackBuilder taskStack = TaskStackBuilder.create(this);
            taskStack.addParentStack(MapsActivity.class);
            taskStack.addNextIntent(intent);


            String reply = "RESPONDER";
            RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                    .setLabel("Mensagem")
                    .build();

            PendingIntent pendingIntent = taskStack.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


            int smallIconResId = R.mipmap.ic_launcher_foreground_notifaction;

//        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_prev, "Previous", prevPendingIntent).build();


            NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                    R.mipmap.ic_launcher_round, reply, pendingIntent)
                    .addRemoteInput(remoteInput).build();

//        Notification.Action action = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher_round,
//                reply,pendingIntent)
//                .addRemoteInput(remoteInput)
//                .build();


            Notification builder = new NotificationCompat.Builder(this, ALERT_ID)
                    // set title, message, etc.
                    .setAutoCancel(true)
                    .setSmallIcon(smallIconResId)
                    .addAction(action)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
//                        .setContentIntent(contentIntent)
                    //Heads-up notification.
//                        .setCustomContentView(collapsedView)
//                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setFullScreenIntent(pendingIntent, true)
//                .setWhen(sendTime)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(pendingIntent)
                    .build();

            //         Create Notification instance.
            Notification alert = builder;
            alert.flags =
                    //Noticaçao permanente
//                Notification.FLAG_ONGOING_EVENT |
                    Notification.FLAG_SHOW_LIGHTS
                            //Vibrar até olhar a mensagem
                            | Notification.FLAG_INSISTENT
            ;

            NotificationManager manager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Support for Android Oreo: Notification Channels
                NotificationChannel channel = new NotificationChannel(
                        ALERT_ID,
                        "Alertas",
                        NotificationManager.IMPORTANCE_HIGH);
                channel.enableLights(true);
                channel.enableVibration(true);
                manager.createNotificationChannel(channel);
            }



//        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(mRequestCode, builder);


//        Intent it = new Intent(this,  MapsActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putString("idUser", data.get("id"));
//        bundle.putString("nameUser", data.get("name"));
//        bundle.putString("latlngUser", data.get("latlng"));
//        it.putExtras(bundle);
//        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_ONE_SHOT);

//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Create a Notification Builder instance.
//        int smallIconResId = R.mipmap.ic_launcher_foreground_notifaction;
            int largeIconResId = R.drawable.ic_account_circle_black_24dp;
            long sendTime = System.currentTimeMillis();

//        BitmapDrawable bitmapDrawable = (BitmapDrawable)getDrawable(largeIconResId);
//        Bitmap largeIconBitmap = bitmapDrawable.getBitmap();

            RemoteViews collapsedView = new RemoteViews(getPackageName(), R.layout.view_collapsed_notification);
            collapsedView.setTextViewText(R.id.timestamp, DateUtils.formatDateTime(this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME));
            collapsedView.setTextViewText(R.id.content_text, data.get("name") + data.get("message"));


            Bitmap largeIcon = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.ic_account_circle_black_24dp);

//        NotificationCompat.Builder notificationBuilder =
//                new NotificationCompat.Builder(this, "fcm-instance-specific")
//                        .setSmallIcon(smallIconResId)
////                        .setTicker(title)
//                        .setContentTitle(title)
//                        .setContentText(body)
//                        .setLargeIcon(largeIcon)
//                        .setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
////                        .setBadgeIconType(R.drawable.ic_favorite_black_24dp)
////                        .setVibrate()
//                        .setAutoCancel(false)
////                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
////                        .setLargeIcon(largeIconBitmap)
//                        .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
////                        .setContentIntent(contentIntent)
//                        //Heads-up notification.
////                        .setCustomContentView(collapsedView)
//                        .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
//                        .setFullScreenIntent(pendingIntent, true)
//                        .setWhen(sendTime)
//                        .setDefaults(Notification.DEFAULT_ALL)
//                ;
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


////         Create Notification instance.
//        Notification alert = notificationBuilder.build();
//        alert.flags =
//                //Noticaçao permanente
////                Notification.FLAG_ONGOING_EVENT |
//                Notification.FLAG_SHOW_LIGHTS
//        //Vibrar até olhar a mensagem
//                        | Notification.FLAG_INSISTENT
//        ;

//        processInlineReply(resultIntent);

//        notificationManager.notify(2, alert);


        }


//    public NotificationCompat.Builder createNotificationBuider(Context context,
//                                                               String title, String message) {
//        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(),
//                R.drawable.ic_account_circle_black_24dp);
//        return new NotificationCompat.Builder(context)
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setLargeIcon(largeIcon)
//                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
//                .setAutoCancel(true);
//    }


//    public NotificationCompat.Builder createNotificationBuider(RemoteMessage.Notification notification,
//                                                               Map<String, String> data) {
//
//        String title;
//        String body;
//
//        if(notification == null){
//            title = "Hi-Health";
//            body = data.get("name")+ data.get("message")
//                    + " = " + data.get("userName")
//            ;
//        }else{
//            title = notification.getTitle();
//            body  = notification.getBody();
//        }
//
//        Bitmap largeIcon = BitmapFactory.decodeResource(this.getResources(),
//                R.drawable.ic_account_circle_black_24dp);
//        return new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                .setContentTitle(title)
//                .setContentText(body)
//                .setLargeIcon(largeIcon)
//                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
//                .setAutoCancel(true);
//    }
//
//
//    private Intent getMessageReplyIntent(String label) {
//        return new Intent()
//                .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
//                .setAction(REPLY_ACTION)
//                .putExtra(KEY_PRESSED_ACTION, label);
//    }
//
//
//    private void showNotification(Context context, Notification notification, int id) {
//        NotificationManager mNotificationManager =
//                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.notify(id, notification);
//    }


//    public void showRemoteInputNotification(Context context) {
//        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
//                .setLabel(context.getString(R.string.text_label_reply))
//                .build();
//
//        PendingIntent replyIntent = PendingIntent.getActivity(context,
//                REPLY_INTENT_ID,
//                getMessageReplyIntent(LABEL_REPLY),
//                PendingIntent.FLAG_UPDATE_CURRENT);
//
//        PendingIntent archiveIntent = PendingIntent.getActivity(context,
//                ARCHIVE_INTENT_ID,
//                getMessageReplyIntent(LABEL_ARCHIVE),
//                PendingIntent.FLAG_UPDATE_CURRENT);
//
//        NotificationCompat.Action replyAction =
//                new NotificationCompat.Action.Builder(android.R.drawable.sym_def_app_icon,
//                        LABEL_REPLY, replyIntent)
//                        .addRemoteInput(remoteInput)
//                        .build();
//
//        NotificationCompat.Action archiveAction =
//                new NotificationCompat.Action.Builder(android.R.drawable.sym_def_app_icon,
//                        LABEL_ARCHIVE, archiveIntent)
//                        .build();
//
//        NotificationCompat.Builder builder =
//                createNotificationBuider(context, "Remote input", "Try typing some text!");
//        builder.addAction(replyAction);
//        builder.addAction(archiveAction);
//
//        showNotification(context, builder.build(), REMOTE_INPUT_ID);
//    }

//    private static Intent getDirectReplyIntent(Context context, String label) {
//        return MyFirebaseMessagingService.getStartIntent(context)
//                .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
//                .setAction(REPLY_ACTION)
//                .putExtra(CONVERSATION_LABEL, label);
//    }

    }

}
