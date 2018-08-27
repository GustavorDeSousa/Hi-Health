package br.com.thecharles.hihealth.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.thecharles.hihealth.R;
import br.com.thecharles.hihealth.model.Sensor;

public class ChildBFragment extends Fragment {

    private static final String TAG = "Steps";
    private TextView tvSteps;

    private String userID;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_child_b, container, false);

        tvSteps = v.findViewById(R.id.tvSteps);
        // Todo - Fazer os dados retornar corretamente
        FirebaseUser user = firebaseAuth.getCurrentUser();
        userID = user.getUid();

        DatabaseReference reference = databaseReference.child("users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Sensor sensor = new Sensor();
                sensor.setStepCount(dataSnapshot.child(userID)
                        .child("sensor").getValue(Sensor.class).getStepCount()); //set the step


                String stepsCount = sensor.getStepCount();
                tvSteps.setText(stepsCount);

                //display all the information
                Log.d(TAG, "showData: Steps: " + sensor.getStepCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return v;
    }



}
