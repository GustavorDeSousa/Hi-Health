package br.com.thecharles.hihealth.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.concurrent.TimeUnit;

import br.com.thecharles.hihealth.R;
import br.com.thecharles.hihealth.ServiceShakeNotification;
import br.com.thecharles.hihealth.config.SettingsFirebase;
import br.com.thecharles.hihealth.fragments.ContactsFragment;
import br.com.thecharles.hihealth.fragments.DataFragment;
import br.com.thecharles.hihealth.fragments.ProfileFragment;
import br.com.thecharles.hihealth.model.Location;
import br.com.thecharles.hihealth.model.Sensor;

// TODO Corrigir bug de ciclo de vida
public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;


    private String userID;
    DatabaseReference firebaseRef = SettingsFirebase.getFirebaseDatabase();
    DatabaseReference firebaseRefDebug = firebaseRef.child("debug");
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private ValueEventListener valueEventListenerMaps;

    private String heartRate = "0.0";
    private String heartRateMax = "0.0";
    private String heartRateMin = "0.0";
    private String stepsCount = "0";


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_data:
                    getSupportActionBar().setTitle(R.string.title_data);
                    Fragment dataFragment = DataFragment.newInstance();
                    openFragment(dataFragment);
                    return true;
                case R.id.navigation_contacts:
                    getSupportActionBar().setTitle(R.string.title_contacts);
                    Fragment contactsFragment = ContactsFragment.newInstance();
                    openFragment(contactsFragment);
                    return true;
                case R.id.navigation_profile:
                    getSupportActionBar().setTitle(R.string.title_profile);
                    Fragment profileFragment = ProfileFragment.newInstance();
                    openFragment(profileFragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Fragment dataFragment = DataFragment.newInstance();
        getSupportActionBar().setTitle(R.string.title_data);
        openFragment(dataFragment);

        DatabaseReference reference = firebaseRefDebug.child("users");


        FirebaseUser user = firebaseAuth.getCurrentUser();
        userID = user.getUid();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .addConnectionCallbacks(this)
                .enableAutoManage(this, 0, this)
                .build();

        Intent intent = new Intent(this, ServiceShakeNotification.class);
        //Start Service
        startService(intent);

    }



    @Override
    protected void onStart() {
        super.onStart();
        dataFitness();
    }

    private void dataFitness() {
        new ViewTodaysHeartRateTask().execute();
        new ViewTodaysStepsTask().execute();
    }


    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
//        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sign_out_menu) {
            firebaseAuth.signOut();
            Toast.makeText(this, "Usuário Saiu com Sucesso",
                    Toast.LENGTH_SHORT).show();
            openIntent(WelcomeActivity.class);
        }
        return super.onOptionsItemSelected(item);
    }

    private void openIntent(Class classOpen) {
        Intent intent = new Intent(MainActivity.this, classOpen);
        startActivity(intent);
        finish();
    }


    //Fitness

    //In use, call this every 30 seconds in active mode, 60 in ambient on watch faces
    private void displayStepsData() {
        DailyTotalResult result = Fitness.HistoryApi.readDailyTotal(
                mGoogleApiClient, DataType.TYPE_STEP_COUNT_DELTA).await(1, TimeUnit.MINUTES);
        showDataSet(result.getTotal());
//        DailyTotalResult result = Fitness.HistoryApi.readDailyTotal(
//                mGoogleApiClient, HealthDataTypes.TYPE_BODY_TEMPERATURE).await(1, TimeUnit.MINUTES);
//        showDataSet(result.getTotal());
    }

    private void displayHeartRateData() {
        DailyTotalResult result = Fitness.HistoryApi.readDailyTotal(
                mGoogleApiClient, DataType.TYPE_HEART_RATE_BPM ).await(1, TimeUnit.MINUTES);
        showDataSet(result.getTotal());

        /*DailyTotalResult result = Fitness.HistoryApi.readDailyTotal(
                mGoogleApiClient, DataType.TYPE_HEIGHT ).await(1, TimeUnit.MINUTES);
        showDataSet(result.getTotal());*/

        /*Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        java.text.DateFormat dateFormat = DateFormat.getDateInstance();
        Log.e("History", "Range Start: " + dateFormat.format(startTime));
        Log.e("History", "Range End: " + dateFormat.format(endTime));

        //Check how many steps were walked and recorded in the last 7 days
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_WEIGHT, DataType.TYPE_WEIGHT)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        DataReadResult dataReadResult = Fitness.HistoryApi.readData(mGoogleApiClient, readRequest).await(1, TimeUnit.MINUTES);

        //Used for aggregated data
        if (dataReadResult.getBuckets().size() > 0) {
            Log.e("History", "Number of buckets: " + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    showDataSet(dataSet);
                }
            }
        }
        //Used for non-aggregated data
        else if (dataReadResult.getDataSets().size() > 0) {
            Log.e("History", "Number of returned DataSets: " + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                showDataSet(dataSet);
            }
        }
        */



    }

    private void showDataSet(DataSet dataSet) {
        Log.e("History", "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();




        for (final DataPoint dp : dataSet.getDataPoints()) {
            Log.e("History", "Data point:");
            Log.e("History", "\tType: " + dp.getDataType().getName());
            Log.e("History", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS))
                    + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.e("History", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS))
                    + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));

            for(final Field field : dp.getDataType().getFields()) {

                Log.e("History", "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));

                DatabaseReference reference =
                        firebaseRefDebug.child("users").child(firebaseAuth.getCurrentUser().getUid());



                if (dp.getDataType().getName().equals("com.google.heart_rate.summary") && field.getName().equals("average")) {
//                    if (field.getName().equals("average")) {
//                        sensor.setHeartRate(String.valueOf(dp.getValue(field)));
                        heartRate = String.valueOf(dp.getValue(field));
//                        sensor.setHeartRate(heartRate);
//                        reference.child("sensor").setValue(sensor);
//                    }
                }
                if (dp.getDataType().getName().equals("com.google.heart_rate.summary") && field.getName().equals("max")) {
                    heartRateMax = String.valueOf(dp.getValue(field));
                }

                if (dp.getDataType().getName().equals("com.google.heart_rate.summary") && field.getName().equals("min")) {
                    heartRateMin = String.valueOf(dp.getValue(field));
                }

                if(dp.getDataType().getName().equals("com.google.step_count.delta")) {
//                    sensor.setStepCount(String.valueOf(dp.getValue(field)));
//                    heartRate = String.valueOf(dp.getValue(field));
                    stepsCount = String.valueOf(dp.getValue(field));
//                    sensor.setStepCount(stepsCount);

                }

                Sensor sensor = new Sensor(heartRate, heartRateMax, heartRateMin, stepsCount);
                reference.child("sensor").setValue(sensor);


//                sensor.setHeartRate(heartRate);
////                sensor.setStepCount(stepsCount);
//                reference.child("sensor").setValue(sensor);
/*
//                if(field.getName().equals(F)){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        DatabaseReference reference =
                                databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid());

                        Sensor sensor = new Sensor();

                        if (dp.getDataType().getName().equals("com.google.heart_rate.summary")) {
                            if (field.getName().equals("average")) {
//                                tvHeight.setText(String.valueOf(dp.getValue(field)) + " BPM");

//                                reference.child("sensor")
//                                        .child("heart_rate").setValue();

                                sensor.setHeartRate(String.valueOf(dp.getValue(field)));
                                reference.child("sensor").setValue(sensor);

                            }
                        } else if(dp.getDataType().getName().equals("com.google.step_count.delta")) {
//                            tvWeight.setText(String.valueOf(dp.getValue(field)));


//                            sensor.setStepCount(String.valueOf(dp.getValue(field)));
//                            reference.child("sensor").setValue(sensor);

//                            reference.child("sensor")
//                                    .child("step_count").setValue();

                        }

//                        reference.child("sensor").setValue(sensor);

                    }
                });

//                }


                */

            }
        }
    }

    /** AssyncTasks */
    private class ViewTodaysHeartRateTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            displayHeartRateData();
            return null;
        }
    }

    private class ViewTodaysStepsTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            displayStepsData();
            return null;
        }
    }

    @Override
    public void onClick(View v) {


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("HistoryAPI", "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
