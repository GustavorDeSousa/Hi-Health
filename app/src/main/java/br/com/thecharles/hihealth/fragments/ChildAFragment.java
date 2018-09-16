package br.com.thecharles.hihealth.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.concurrent.TimeUnit;

import br.com.thecharles.hihealth.R;
import br.com.thecharles.hihealth.config.SettingsFirebase;
import br.com.thecharles.hihealth.model.Sensor;
import br.com.thecharles.hihealth.model.User;

public class ChildAFragment extends Fragment {

    private static final String TAG = "Heart";
    private TextView tvRateHeart;


    private String userID;

//    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    DatabaseReference firebaseRef = SettingsFirebase.getFirebaseDatabase();
    DatabaseReference firebaseRefDebug = firebaseRef.child("debug");
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_child_a, container, false);

        tvRateHeart = v.findViewById(R.id.tvRateHeart);

        // Todo - Fazer os dados retornar corretamente

        FirebaseUser user = firebaseAuth.getCurrentUser();
        userID = user.getUid();

        DatabaseReference reference = firebaseRefDebug.child("users");


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Sensor sensor = new Sensor();
                sensor.setHeartRate(dataSnapshot.child(userID)
                        .child("sensor").getValue(Sensor.class).getHeartRate()); //set the heart


                String heartRate = sensor.getHeartRate();
                tvRateHeart.setText(heartRate);

                //display all the information
                Log.d(TAG, "showData: Heart: " + sensor.getHeartRate());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return v;
    }








}
