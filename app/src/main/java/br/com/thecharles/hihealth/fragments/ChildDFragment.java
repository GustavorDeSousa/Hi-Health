package br.com.thecharles.hihealth.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import br.com.thecharles.hihealth.activity.MapsActivity;
import br.com.thecharles.hihealth.R;
import br.com.thecharles.hihealth.activity.WalkMyAndroidActivity;
import br.com.thecharles.hihealth.config.SettingsFirebase;

public class ChildDFragment extends Fragment {

    private static final String TAG = ChildDFragment.class.getSimpleName();
    private Button btnMap, btnGitbook;
    private ImageView address;

    private Location mLastLocation;

    // Constants
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String TRACKING_LOCATION_KEY = "tracking_location";

    //Location Classes
    private boolean mTrackingLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    DatabaseReference firebaseRef = SettingsFirebase.getFirebaseDatabase();
    DatabaseReference firebaseRefDebug = firebaseRef.child("debug");
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_child_d, container, false);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
//        btnMap = view.findViewById(R.id.btnMapa);
//        btnMap.setOnClickListener(openMap());

//        btnGitbook = view.findViewById(R.id.btnGitbook);
//        btnGitbook.setOnClickListener(openWalkMyAndroid());

        address = view.findViewById(R.id.ivAddress);
        address.setOnClickListener(openMap());

        return view;
    }

    private View.OnClickListener openWalkMyAndroid() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WalkMyAndroidActivity.class);
                startActivity(intent);
            }
        };
    }

        private View.OnClickListener openMap() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent it = new Intent(getActivity(), MapsActivity.class);
//                startActivity(it);
                getLocation();
            }
        };
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            Log.d(TAG, "getLocation: permissions granted");
            mFusedLocationClient.getLastLocation().addOnSuccessListener(
                    new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                mLastLocation = location;
                                Log.d(TAG, "Location:" +  getString(R.string.location_text,
                                        mLastLocation.getLatitude(),
                                        mLastLocation.getLongitude(),
                                        mLastLocation.getTime()));


                                double latitude = mLastLocation.getLatitude();
                                double longitude = mLastLocation.getLongitude();

                                LatLng myLastocation = new LatLng(latitude, longitude);
                                firebaseRefDebug.child("users")
                                        .child(firebaseAuth.getCurrentUser().getUid())
                                        .child("location")
                                        .child("latLng").setValue(myLastocation);

                                Intent it = new Intent(getActivity(), MapsActivity.class);
                                it.putExtra("lat", latitude);
                                it.putExtra("lgn", longitude);
                                startActivity(it);


//                                LatLng myHouse = new LatLng(latitude, longitude);
//        LatLng myHouse = new LatLng(-23.533773, -46.62529);


//                                mLocationTextView.setText(
//                                        getString(R.string.location_text,
//                                                mLastLocation.getLatitude(),
//                                                mLastLocation.getLongitude(),
//                                                mLastLocation.getTime()));
                            } else {
//                                mLocationTextView.setText(R.string.no_location);
                                Log.d(TAG, "Location:" + R.string.no_location);
                            }
                        }
                    }
            );

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                // If the permission is granted, get the location,
                // otherwise, show a Toast
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Toast.makeText(getActivity(),
                            R.string.location_permission_denied,
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

//
//    public void openMap(View view) {
//        Intent it = new Intent(getActivity(), MapsActivity.class);
//        startActivity(it);
//    }

}
