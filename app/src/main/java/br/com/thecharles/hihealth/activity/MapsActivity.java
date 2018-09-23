package br.com.thecharles.hihealth.activity;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.thecharles.hihealth.R;
import br.com.thecharles.hihealth.config.SettingsFirebase;
import br.com.thecharles.hihealth.helper.UserFirebase;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;

    private TextView tvUserAlert;
    private ImageView ivUserAlert;

    DatabaseReference firebaseRef = SettingsFirebase.getFirebaseDatabase();
    DatabaseReference firebaseRefDebug = firebaseRef.child("debug").child("users");
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private String userID;


    String name;

    String alertLocation;
    String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        userID = user.getUid();
        firebaseRefDebug = firebaseRefDebug.child(userID);



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");

//        getLocation();

        tvUserAlert = findViewById(R.id.tvUserAlert);
        ivUserAlert = findViewById(R.id.ivPhotoAlert);




//        //TODO Recuperar dados do usuário destinatario
//        Bundle bundle = getIntent().getExtras();
//
//        if (bundle != null) {
//
//
//            id = bundle.getString("idUser");
//
//
//
//            if (id != null) {
//
//                alertLocation = bundle.getString("latlngUser");
//
//                alertLocation = alertLocation.replace("lat/lng: ","");
//                alertLocation = alertLocation.replace("(","");
//                alertLocation = alertLocation.replace(")","");
//
//                String[] lotacionConvert = alertLocation.split(",");
//                alertLatitude = Float.parseFloat(lotacionConvert[0]);
//                alertLongitude = Float.parseFloat(lotacionConvert[1]);
//                name = bundle.getString("nameUser");
//                tvUserAlert.setText(name);
//
////                myLatitude = bundle.getDouble("lat");
////                myLongitude = bundle.getDouble("lgn");
////                myLatitude = bundle.getDouble("myLat");
////                myLongitude = bundle.getDouble("myLgn");
//
//            } else {
//
////
//
////                myLatitude = bundle.getDouble("lat");
////                myLongitude = bundle.getDouble("lgn");
//                name = "Localização Atual";
//                tvUserAlert.setText(name);
//
//            }
//        }

    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //TODO Recuperar dados do usuário destinatario
        Bundle bundle = getIntent().getExtras();

        double myLatitude = 0.0;
        double alertLatitude = 0.0;
        double myLongitude = 0.0;
        double alertLongitude = 0.0;

        if (bundle != null) {

            id = bundle.getString("idUser");

            if (id != null) {

                alertLocation = bundle.getString("latlngUser");

                alertLocation = alertLocation.replace("lat/lng: ","");
                alertLocation = alertLocation.replace("(","");
                alertLocation = alertLocation.replace(")","");

                String[] lotacionConvert = alertLocation.split(",");
                alertLatitude = Float.parseFloat(lotacionConvert[0]);
                alertLongitude = Float.parseFloat(lotacionConvert[1]);
                name = bundle.getString("nameUser");
                tvUserAlert.setText(name);

            } else {//

//                myLatitude = bundle.getDouble("lat");
//                myLongitude = bundle.getDouble("lgn");
                name = "Localização Atual";
                tvUserAlert.setText(name);

            }
        }


        final Double[] myLat = {myLatitude};
        final Double[] myLng ={myLongitude};

        DatabaseReference firebaseRef = SettingsFirebase.getFirebaseDatabase();
        DatabaseReference firebaseRefDebug = firebaseRef.child("debug").child("users").child(userID).child("location");
        final double finalAlertLatitude = alertLatitude;
        final double finalAlertLongitude = alertLongitude;
        firebaseRefDebug.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myLat[0] = (Double) dataSnapshot.child("latLng").child("latitude").getValue();
                myLng[0] = (Double) dataSnapshot.child("latLng").child("longitude").getValue();

                LatLng myHouse = new LatLng(myLat[0], myLng[0]);
                mMap.addMarker(new MarkerOptions().position(myHouse).title("Você")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(myHouse));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myHouse,15f));



                LatLng amigo = new LatLng(finalAlertLatitude, finalAlertLongitude);
                if (finalAlertLatitude != 0.0 || finalAlertLongitude != 0.0) {
                    mMap.addMarker(new MarkerOptions()
                            .position(amigo)
                            .title(name)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myHouse));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(amigo,11.5f));
                }

//        mMap.isMyLocationEnabled();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // Add a marker in Sydney and move the camera

//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

//        getLocation();


//        mMap.setMaxZoomPreference(14.0f);

//        Log.d(TAG, "Mapa: "+ myHouse);

    }
}
