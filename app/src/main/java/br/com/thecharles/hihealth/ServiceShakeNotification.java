package br.com.thecharles.hihealth;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import br.com.thecharles.hihealth.activity.MainActivity;

public class ServiceShakeNotification extends Service implements SensorEventListener {
    /** Minimum movement force to consider. */
    private static final int MIN_FORCE = 10;

    /**
     * Minimum times in a shake gesture that the direction of movement needs to
     * change.
     */
    private static final int MIN_DIRECTION_CHANGE = 3;

    /** Maximum pause between movements. */
    private static final int MAX_PAUSE_BETHWEEN_DIRECTION_CHANGE = 500;

    /** Minimum allowed time for shake gesture. */
    private static final int MIN_TOTAL_DURATION_OF_SHAKE = 600; // 3 seconds
    private static final String TAG = ServiceShakeNotification.class.getSimpleName();

    /** Time when the gesture started. */
    private long mFirstDirectionChangeTime = 0;

    /** Time when the last movement started. */
    private long mLastDirectionChangeTime;

    /** How many movements are considered so far. */
    private int mDirectionChangeCount = 0;

    /** The last x position. */
    private float lastX = 0;

    /** The last y position. */
    private float lastY = 0;

    /** The last z position. */
    private float lastZ = 0;

    NotificationFCM alert = new NotificationFCM();

    private static final int NOTIFICATION_ID = 8;
    private static final int PROGRESS_ID = 44;
    private static final String ACTION_SEND_NOTIFICATION =
            "br.com.thecharles.hihealth.ACTION_SEND_NOTIFICATION";
    private static final String ACTION_CANCEL_NOTIFICATION =
            "br.com.thecharles.hihealth.ACTION_CANCEL_NOTIFICATION";

    private NotificationReceiver mReceiver = new NotificationReceiver();
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity
    Handler handler;
    boolean running = false;

    final int PROGRESS_MAX = 35;
    int PROGRESS_CURRENT = 0;

    int incr;

    @Override
    public void onCreate() {
        super.onCreate();
        //Initialize and register the notification receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_CANCEL_NOTIFICATION);
        intentFilter.addAction(ACTION_SEND_NOTIFICATION);
        registerReceiver(mReceiver, intentFilter);

        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mBuilder = new NotificationCompat.Builder(this, "fcm-instance-specific");

        handler= new Handler();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_UI, new Handler());
        return START_STICKY;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta; // perform low-cut filter

        // calculate movement
        float totalMovement = Math.abs(x + y + z - lastX - lastY - lastZ);

        if (mAccel > 20) {
//            showNotification();

            if (totalMovement > MIN_FORCE) {

                // get time
                long now = System.currentTimeMillis();

                // store first movement time
                if (mFirstDirectionChangeTime == 0) {
                    mFirstDirectionChangeTime = now;
                    mLastDirectionChangeTime = now;
                }

                // check if the last movement was not long ago
                long lastChangeWasAgo = now - mLastDirectionChangeTime;
                if (lastChangeWasAgo < MAX_PAUSE_BETHWEEN_DIRECTION_CHANGE) {

                    // store movement data
                    mLastDirectionChangeTime = now;
                    mDirectionChangeCount++;

                    // store last sensor data
                    lastX = x;
                    lastY = y;
                    lastZ = z;

                    // check how many movements are so far
                    if (mDirectionChangeCount >= MIN_DIRECTION_CHANGE) {

                        // check total duration
                        long totalDuration = now - mFirstDirectionChangeTime;
                        if (totalDuration >= MIN_TOTAL_DURATION_OF_SHAKE) {
//                            mShakeListener.onShake();
//                            alert.getDataNotification();
//                            showNotification();
                            showCountDown();
                            resetShakeParameters();
                        }
                    }

                } else {
                    resetShakeParameters();
                }
            }

        }
    }

    /**
     * Resets the shake parameters to their default values.
     */
    private void resetShakeParameters() {
        mFirstDirectionChangeTime = 0;
        mDirectionChangeCount = 0;
        mLastDirectionChangeTime = 0;
        lastX = 0;
        lastY = 0;
        lastZ = 0;
    }

    private void showCountDown() {

        // Create a Notification Builder instance.
        int smallIconResId = R.mipmap.ic_launcher_foreground_notifaction;
        int largeIconResId = R.drawable.ic_account_circle_black_24dp;
        int checkIconResId = R.drawable.ic_check_black_24dp;
        int closeIconResId = R.drawable.ic_close_black_24dp;
        long sendTime = System.currentTimeMillis();

//        RemoteViews collapsedView = new RemoteViews(getPackageName(), R.layout.view_collapsed_notification);
////        collapsedView.setTextViewText(R.id.timestamp, DateUtils.formatDateTime(this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME));
//        collapsedView.setTextViewText(R.id.content_text, "");
//        collapsedView.setTextViewText(R.id.content_title, "ATENÇÃO - Está tudo bem com você ?");

//        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this,
//                MainActivity.class), 0);

        // First let's define the intent to trigger when notification is selected
// Start out by creating a normal intent (in this case to open an activity)
        Intent intent = new Intent(this, MainActivity.class);
// Next, let's turn this into a PendingIntent using
//   public static PendingIntent getActivity(Context context, int requestCode,
//       Intent intent, int flags)
        int requestID = (int) System.currentTimeMillis(); //unique requestID to differentiate between various notification with same NotifId
        int flags = PendingIntent.FLAG_CANCEL_CURRENT; // cancel old intent and create new one



//        Intent cancelIntent = new Intent(ACTION_CANCEL_NOTIFICATION);
//        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast
//                (this, NOTIFICATION_ID, cancelIntent, PendingIntent.FLAG_ONE_SHOT);

        Intent cancelIntent = new Intent(ACTION_CANCEL_NOTIFICATION);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast
                (this, PROGRESS_CURRENT, cancelIntent, PendingIntent.FLAG_ONE_SHOT);

        Intent sendIntent = new Intent(ACTION_SEND_NOTIFICATION);
        PendingIntent sendPendingIntent = PendingIntent.getBroadcast
                (this, PROGRESS_CURRENT, sendIntent, PendingIntent.FLAG_ONE_SHOT);

//        PendingIntent updatePendingIntent = PendingIntent.getBroadcast
//                (this, PROGRESS_CURRENT, updateIntent, PendingIntent.FLAG_ONE_SHOT);

//        PendingIntent pIntent = PendingIntent.getActivity(this, PROGRESS_ID, intent, flags);




        Bitmap largeIcon = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.ic_account_circle_black_24dp);



        final NotificationManager mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder =
                new NotificationCompat.Builder(this, "fcm-instance-specific");
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_foreground_notifaction)
                .setUsesChronometer(true)
                .setDefaults(Notification.DEFAULT_ALL)
//                .setContentTitle("Hi-Health")
                .setContentTitle("Está tudo bem com você ?")
                .setWhen(sendTime)
                .setLargeIcon(largeIcon)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
//                .setCustomContentView(collapsedView)
                .setFullScreenIntent(sendPendingIntent, true)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setContentIntent(sendPendingIntent)
                .setAutoCancel(true)
//                .addAction(checkIconResId, "SIM", pIntent)
                .addAction(R.drawable.ic_check_black_24dp, "SIM", cancelPendingIntent)
                .addAction(R.drawable.ic_close_black_24dp, "NÃO", sendPendingIntent)
                .setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false)

                .build()
        ;




// Start a lengthy operation in a background thread
//        new Thread(
//                new Runnable() {
//                    @Override
//                    public void run() {
////                            while (!Thread.currentThread().isInterrupted()) {
//                        // Do stuff
//                        if (running) {
//
//
//                            // Do the "lengthy" operation 20 times
//                            for (incr = 0; incr <= PROGRESS_MAX; incr+=3) {
//                                // Sets the progress indicator to a max value, the
//                                // current completion percentage, and "determinate"
//                                // state
//                                mBuilder.setProgress(PROGRESS_MAX, incr, false);
//                                // Displays the progress bar for the first time.
//                                mNotifyManager.notify(PROGRESS_ID, mBuilder.build());
//                                // Sleeps the thread, simulating an operation
//                                // that takes time
//                                try {
//                                    // Sleep for 5 seconds
//                                    Thread.sleep(2*1000);
//                                } catch (InterruptedException e) {
//                                    Log.d(TAG, "sleep failure");
//                                }
//                            }
//                            // When the loop is finished, updates the notification
////                        mBuilder.setContentText("Download complete")
////                                // Removes the progress bar
////                                .setProgress(0,0,false);
//
//                            showNotification();
////                        mNotifyManager.notify(0, mBuilder.build());
//
//                        }
//
//                    }
//                }
//// Starts the thread by calling the run() method in its Runnable
//        ).start();

        running = true;
        thread.start();
//        thread.interrupt();
    }




    /**
     * The broadcast receiver class for notifications. Responds to the update notification and
     * cancel notification pending intents actions.
     */
    private class NotificationReceiver extends BroadcastReceiver {

        /**
         * Gets the action from the incoming broadcast intent and responds accordingly
         * @param context Context of the app when the broadcast is received.
         * @param intent The broadcast intent containing the action.
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case ACTION_SEND_NOTIFICATION:
                    sendNotification();
                    break;
                case ACTION_CANCEL_NOTIFICATION:
                    cancelNotification();
                    break;
            }
        }
    }

    /**
     * show notification when Accel is more then the given int.
     */
    private void sendNotification() {

//        final NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        alert = new NotificationFCM();
        alert.getDataNotification();

        // Create a Notification Builder instance.
        int smallIconResId = R.mipmap.ic_launcher_foreground_notifaction;
        int largeIconResId = R.drawable.ic_account_circle_black_24dp;
        long sendTime = System.currentTimeMillis();

//        RemoteViews collapsedView = new RemoteViews(getPackageName(), R.layout.view_collapsed_notification);
//        collapsedView.setTextViewText(R.id.timestamp, DateUtils.formatDateTime(this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME));
//        collapsedView.setTextViewText(R.id.content_text, "Seus amigos foram notificados!");
//        collapsedView.setTextViewText(R.id.content_title, "Alerta Enviado");

//        PendingIntent pi = PendingIntent.getActivity(this, PROGRESS_ID, new Intent(this,
//                MainActivity.class), 0);

        //Sets up the pending intent that is delivered when the notification is clicked
        Intent notificationIntent = new Intent(ServiceShakeNotification.this, MainActivity.class);
//        PendingIntent notificationPendingIntent = PendingIntent.getActivity
//                (this, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap largeIcon = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.ic_account_circle_black_24dp);

        int flags = PendingIntent.FLAG_CANCEL_CURRENT;
        PendingIntent pIntent = PendingIntent.getActivity(ServiceShakeNotification.this,
                NOTIFICATION_ID, notificationIntent, flags);

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(smallIconResId)
                .setContentTitle("Alerta Enviado")
                .setContentText("Seus amigos foram notificados!")
                .setContentIntent(pIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setAutoCancel(true)
//                        .setUsesChronometer(true)
//                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                        .setLargeIcon(largeIconBitmap)
//                .setStyle(new NotificationCompat.BigTextStyle().bigText("New Message Alert!"))
//                        .setContentIntent(pi)
                //Heads-up notification.
//                        .setCustomContentView(collapsedView)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
//                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setFullScreenIntent(pIntent, true)
//                .setWhen(sendTime)
//                .setDefaults(Notification.DEFAULT_ALL)
//                .setDeleteIntent(cancelPendingIntent)
//                .addAction(R.drawable.ic_learn_more, getString(R.string.learn_more),
//                        learnMorePendingIntent)
//                .setStyle(new NotificationCompat.BigPictureStyle()
//                        .bigPicture(androidImage)
//
//                        .setBigContentTitle("Notificação Atualizada!"))
                ;

//        final NotificationCompat.Builder notificationBuilder =
//                new NotificationCompat.Builder(this, "fcm-instance-specific")
//                        .setSmallIcon(smallIconResId)
////                        .setTicker(title)
//                        .setContentTitle("Alerta Enviado")
//                        .setContentText("Seus amigos foram notificados!")
////                        .setContentInfo("sei la oq colocar aqui")
//                        .setLargeIcon(largeIcon)
////                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_person_pin_circle_black_24dp))
////                        .setContentTitle("Device Accelerometer Notification")
////                        .setContentText("New Message Alert!")
//
////                        .setBadgeIconType(R.drawable.ic_favorite_black_24dp)
////                        .setVibrate()
//                        .setAutoCancel(true)
////                        .setUsesChronometer(true)
////                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
////                        .setLargeIcon(largeIconBitmap)
//                        .setStyle(new NotificationCompat.BigTextStyle().bigText("New Message Alert!"))
////                        .setContentIntent(pi)
//                        //Heads-up notification.
////                        .setCustomContentView(collapsedView)
//                        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
//                        .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
//                        .setFullScreenIntent(pi, true)
//                        .setWhen(sendTime)
//                        .setDefaults(Notification.DEFAULT_ALL)
//                ;
//
//        // Create Notification instance.
//        Notification alert = notifyBuilder.build();
//        alert.flags =
//                //Noticaçao permanente
//                Notification.FLAG_ONGOING_EVENT |
//                        Notification.FLAG_SHOW_LIGHTS
//        //Vibrar até olhar a mensagem
////                        | Notification.FLAG_INSISTENT
//        ;

//// Start a lengthy operation in a background thread
//        new Thread(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        int incr;
//                        // Do the "lengthy" operation 20 times
//                        for (incr = 0; incr <= 50; incr+=5) {
//                            // Sets the progress indicator to a max value, the
//                            // current completion percentage, and "determinate"
//                            // state
//                            notificationBuilder.setProgress(50, incr, false);
//                            // Displays the progress bar for the first time.
//                            notificationManager.notify(0, notificationBuilder.build());
//                            // Sleeps the thread, simulating an operation
//                            // that takes time
//                            try {
//                                // Sleep for 5 seconds
//                                Thread.sleep(1*1000);
//                            } catch (InterruptedException e) {
//                                Log.d(TAG, "sleep failure");
//                            }
//                        }
//                        // When the loop is finished, updates the notification
//                        notificationBuilder.setContentText("Download complete")
//                                // Removes the progress bar
//                                .setProgress(0,0,false);
//                        notificationManager.notify(104, notificationBuilder.build());
//                    }
//                }
//// Starts the thread by calling the run() method in its Runnable
//        ).start();
//


        stopRunning();
        incr = 85;

//        handler.removeCallbacks(thread);
//        thread.interrupt();

        //Deliver the notification
        Notification myNotification = notifyBuilder.build();


        mBuilder.setProgress(PROGRESS_MAX, incr, false);
        mNotifyManager.notify(PROGRESS_ID, myNotification);



//        mNotifyManager.cancel(PROGRESS_ID);

//        notificationManager.notify(PROGRESS_ID, mBuilder.build());


    }

    private void cancelNotification() {

        //Sets up the pending intent that is delivered when the notification is clicked
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity
                (this, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Alerta Cancelado!")
                .setSmallIcon(R.mipmap.ic_launcher_foreground_notifaction)
                .setContentIntent(notificationPendingIntent)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
//                .setDeleteIntent(cancelPendingIntent)
//                .addAction(R.drawable.ic_learn_more, getString(R.string.learn_more),
//                        learnMorePendingIntent)
//                .setStyle(new NotificationCompat.BigPictureStyle()
//                        .bigPicture(androidImage)
//
//                        .setBigContentTitle("Notificação Atualizada!"))
                ;



        stopRunning();
        incr = 95;
//        handler.removeCallbacks(thread);
//        thread.interrupt();

        //Deliver the notification
        Notification myNotification = notifyBuilder.build();


        mBuilder.setProgress(PROGRESS_MAX, incr, false);
        mNotifyManager.notify(PROGRESS_ID, myNotification);

//        mNotifyManager.cancel(PROGRESS_ID);
}


    Thread thread = new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            if (running) {


                                // Do the "lengthy" operation 20 times
                                for (incr = 0; incr <= PROGRESS_MAX; incr+=5) {
                                    // Sets the progress indicator to a max value, the
                                    // current completion percentage, and "determinate"
                                    // state
                                    mBuilder.setProgress(PROGRESS_MAX, incr, false);
                                    // Displays the progress bar for the first time.
                                    mNotifyManager.notify(PROGRESS_ID, mBuilder.build());
                                    // Sleeps the thread, simulating an operation
                                    // that takes time
                                    try {
                                        // Sleep for 5 seconds
                                        Thread.sleep(2*1000);
                                    } catch (InterruptedException e) {
                                        Log.d(TAG, "sleep failure");
                                    }
                                }
                                // When the loop is finished, updates the notification
//                        mBuilder.setContentText("Download complete")
//                                // Removes the progress bar
//                                .setProgress(0,0,false);

                                if (incr != 100 || incr != 90) {
                                    sendNotification();
                                }



//                        mNotifyManager.notify(0, mBuilder.build());

                            }

                        }

                    }
// Starts the thread by calling the run() method in its Runnable


    );

    public void stopRunning(){
        running = false;
    }

}
