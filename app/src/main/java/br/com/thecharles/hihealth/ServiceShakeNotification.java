package br.com.thecharles.hihealth;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.RemoteViews;

import br.com.thecharles.hihealth.activity.MainActivity;

import static android.os.Build.ID;

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

//    /** OnShakeListener that is called when shake is detected. */
//    private OnShakeListener mShakeListener;
//
//    /**
//     * Interface for shake gesture.
//     */
//    public interface OnShakeListener {
//
//        /**
//         * Called when shake gesture is detected.
//         */
//        void onShake();
//    }
//
//    public void setOnShakeListener(OnShakeListener listener) {
//        mShakeListener = listener;
//    }


    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity

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
        long sendTime = System.currentTimeMillis();

        RemoteViews collapsedView = new RemoteViews(getPackageName(), R.layout.view_collapsed_notification);
//        collapsedView.setTextViewText(R.id.timestamp, DateUtils.formatDateTime(this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME));
        collapsedView.setTextViewText(R.id.content_text, "");
        collapsedView.setTextViewText(R.id.content_title, "ATENÇÃO - Está tudo bem com você ?");

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
        PendingIntent pIntent = PendingIntent.getActivity(this, 104, intent, flags);


        final NotificationManager mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, "fcm-instance-specific");
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_foreground_notifaction)
                .setUsesChronometer(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setCustomContentView(collapsedView)
                .setFullScreenIntent(pIntent, true)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_check_black_24dp, "SIM", pIntent)
                .addAction(R.drawable.ic_close_black_24dp, "NÃO", pIntent).build()
        ;


// Start a lengthy operation in a background thread
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        int incr;
                        // Do the "lengthy" operation 20 times
                        for (incr = 0; incr <= 20; incr+=3) {
                            // Sets the progress indicator to a max value, the
                            // current completion percentage, and "determinate"
                            // state
                            mBuilder.setProgress(20, incr, false);
                            // Displays the progress bar for the first time.
                            mNotifyManager.notify(104, mBuilder.build());
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

                        showNotification();
//                        mNotifyManager.notify(0, mBuilder.build());
                    }
                }
// Starts the thread by calling the run() method in its Runnable
        ).start();

    }

    /**
     * show notification when Accel is more then the given int.
     */
    private void showNotification() {

        final NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a Notification Builder instance.
        int smallIconResId = R.mipmap.ic_launcher_foreground_notifaction;
        int largeIconResId = R.drawable.ic_account_circle_black_24dp;
        long sendTime = System.currentTimeMillis();

        RemoteViews collapsedView = new RemoteViews(getPackageName(), R.layout.view_collapsed_notification);
        collapsedView.setTextViewText(R.id.timestamp, DateUtils.formatDateTime(this, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME));
        collapsedView.setTextViewText(R.id.content_text, "Seus amigos foram notificados!");
        collapsedView.setTextViewText(R.id.content_title, "Alerta Enviado");

        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this,
                MainActivity.class), 0);

        final NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "fcm-instance-specific")
                        .setSmallIcon(smallIconResId)
//                        .setTicker(title)
//                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_person_pin_circle_black_24dp))
//                        .setContentTitle("Device Accelerometer Notification")
//                        .setContentText("New Message Alert!")

//                        .setBadgeIconType(R.drawable.ic_favorite_black_24dp)
//                        .setVibrate()
                        .setAutoCancel(false)
//                        .setUsesChronometer(true)
//                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                        .setLargeIcon(largeIconBitmap)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText("New Message Alert!"))
//                        .setContentIntent(pi)
                        //Heads-up notification.
                        .setCustomContentView(collapsedView)
                        .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                        .setFullScreenIntent(pi, true)
                        .setWhen(sendTime)
                        .setDefaults(Notification.DEFAULT_ALL)
                ;

        // Create Notification instance.
        Notification alert = notificationBuilder.build();
        alert.flags =
                //Noticaçao permanente
                Notification.FLAG_ONGOING_EVENT |
                        Notification.FLAG_SHOW_LIGHTS
        //Vibrar até olhar a mensagem
//                        | Notification.FLAG_INSISTENT
        ;

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


        notificationManager.notify(104, notificationBuilder.build());


    }
}
