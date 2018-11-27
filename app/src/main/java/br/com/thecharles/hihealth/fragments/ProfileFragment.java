package br.com.thecharles.hihealth.fragments;



import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.thecharles.hihealth.R;
import br.com.thecharles.hihealth.activity.MapsActivity;
import br.com.thecharles.hihealth.config.SettingsFirebase;
import br.com.thecharles.hihealth.model.Location;
import br.com.thecharles.hihealth.model.User;

public class ProfileFragment extends Fragment {

    private TextView tvProfile, tvName, tvAddress, tvHeight, tvWeight, tvBlood;

    private static final String TAG = "User: ";

    private String userID;

//    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference firebaseRef = SettingsFirebase.getFirebaseDatabase();
    DatabaseReference firebaseRefDebug = firebaseRef.child("debug");
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private ValueEventListener valueEventListenerProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

//        tvProfile = v.findViewById(R.id.tvProfile);
        tvName = v.findViewById(R.id.nmUsuario);
        tvAddress = v.findViewById(R.id.txEndereco);
        tvHeight = v.findViewById(R.id.txAltura);
        tvWeight = v.findViewById(R.id.txPeso);


        DatabaseReference reference = firebaseRefDebug.child("users");

        FirebaseUser user = firebaseAuth.getCurrentUser();
        userID = user.getUid();


       valueEventListenerProfile =  reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User usuario = new User();
                usuario.setName(dataSnapshot.child(userID).child("registered").getValue(User.class).getName()); //set the name
                usuario.setEmail(dataSnapshot.child(userID).child("registered").getValue(User.class).getEmail()); //set the email
                usuario.setPhone(dataSnapshot.child(userID).child("registered").getValue(User.class).getPhone()); //set the phone
                usuario.setWeight(dataSnapshot.child(userID).child("registered").getValue(User.class).getWeight()); //set the weight
                usuario.setHeight(dataSnapshot.child(userID).child("registered").getValue(User.class).getHeight()); //set the height
                usuario.setDocument(dataSnapshot.child(userID).child("registered").getValue(User.class).getDocument()); //set the document
                usuario.setAddress(dataSnapshot.child(userID).child("registered").getValue(User.class).getAddress()); //set the address
                usuario.setGenre(dataSnapshot.child(userID).child("registered").getValue(User.class).getGenre()); //set the genre
                usuario.setBirthDay(dataSnapshot.child(userID).child("registered").getValue(User.class).getBirthDay()); //set the birthday
                usuario.setBlood(dataSnapshot.child(userID).child("registered").getValue(User.class).getBlood()); //set the blood

//                String profile = usuario.getName() + "\n" + usuario.getEmail() + "\n" +
//                        usuario.getDocument() + "\n" + usuario.getHeight() + "\n" + usuario.getWeight() + "\n"
//                        + usuario.getAddress() + "\n" + usuario.getGenre() + "\n" + usuario.getBirthDay() + "\n"
//                        + usuario.getBlood() + "\n" + "...";
//                tvProfile.setText(profile);

                Location location = new Location();
                Double lat = dataSnapshot.child(userID).child("location").child("latLng").child("latitude").getValue(Double.class);
                Double lng = dataSnapshot.child(userID).child("location").child("latLng").child("longitude").getValue(Double.class);

                LatLng latLng = new LatLng(lat, lng);
                location.setLatLng(latLng);

                Intent intent = new Intent(getActivity(),  MapsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("myLat", lat);
                bundle.putDouble("myLng", lng);
                intent.putExtras(bundle);

                Log.d(TAG, "showData: Lat: " + lat);
                Log.d(TAG, "showData: Lgn: " + lng);
                Log.d(TAG, "showData: latLng: " + latLng);



//                myLocation = location.getLatLng();
//                myLatitude = lat;
//                myLongitude = lng;

                tvName.setText(usuario.getName());
                tvAddress.setText(usuario.getAddress());
                tvHeight.setText(usuario.getHeight());
                tvWeight.setText(usuario.getWeight());

                //display all the information
                Log.d(TAG, "showData: Nome: " + usuario.getName());
                Log.d(TAG, "showData: Email: " + usuario.getEmail());
                Log.d(TAG, "showData: Telefone: " + usuario.getPhone());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return v;
    }
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }
}
