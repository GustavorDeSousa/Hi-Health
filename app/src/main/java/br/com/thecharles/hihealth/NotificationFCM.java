package br.com.thecharles.hihealth;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;

import java.io.IOException;

import br.com.thecharles.hihealth.config.SettingsFirebase;
import br.com.thecharles.hihealth.helper.UserFirebase;
import br.com.thecharles.hihealth.model.Location;
import br.com.thecharles.hihealth.model.Notification;
import br.com.thecharles.hihealth.model.User;

public class NotificationFCM {

    private static final String TAG = NotificationFCM.class.getSimpleName();

    User usuario = new User();
    Location location =  new Location();
    String userName;
    String tokenDevice;
    ValueEventListener valueEventListenerUser;
    Notification notification = new Notification();
    private String userID;


    /**
     * TESTE REQUEST FMC
     */
    DatabaseReference reference = SettingsFirebase.getFirebaseDatabase();
    DatabaseReference firebaseRefDebug = reference.child("debug").child("users");
    DatabaseReference userRef = firebaseRefDebug.child(UserFirebase.getUId());


    public void getDataNotification() {
        userID = UserFirebase.getUId();
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                usuario.setName(dataSnapshot.child("registered").getValue(User.class).getName()); //set the name
                usuario.setToken(dataSnapshot.child("registered").getValue(User.class).getToken()); //set the name
                Float lat = dataSnapshot.child("location").child("latLng").child("latitude").getValue(Float.class);
                Float lng = dataSnapshot.child("location").child("latLng").child("longitude").getValue(Float.class);

                LatLng latLng = new LatLng(lat, lng);
                location.setLatLng(latLng);

                userName = usuario.getName();
                tokenDevice = usuario.getToken();
                latLng = location.getLatLng();

//                                Log.d(TAG, userName + " - " + tokenDevice);

//                Notification notification = new Notification();
//                                LatLng latLng = new LatLng(-23.500712, -46.575707);
                notification.setTokenUser(tokenDevice);
                notification.setNameUser(userName);
                notification.setMessageAlert(" pode não estar passando bem. Que tal ajudar?!");
                notification.setLatLngUser(latLng);
                notification.setIdUser(userID);


                JsonObject jsonObj = new JsonObject();
                jsonObj.addProperty("to", getClientTokenDevice());

                JsonObject notificationData = new JsonObject();
                notificationData.addProperty("id", notification.getIdUser());
                notificationData.addProperty("name", notification.getNameUser());
                notificationData.addProperty("message", notification.getMessageAlert());
                notificationData.addProperty("latlng", notification.getLatLngUser().toString());


                jsonObj.add("data", notificationData);

                JsonObject msgObj = new JsonObject();
                msgObj.add("message", jsonObj);

                Log.d(TAG,"data  message " + jsonObj.toString());
//
                String json = jsonObj.toString();
//                return notification;



                try {

//                    sendAlert(json);
                    RequestFCM request = new RequestFCM();
                    request.sendAlert(json);
//                            sendData();
//                            sendNotification();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String getClientTokenDevice() {

//            firebaseRefDebug.addValueEventListener(valueEventListenerNotication);


        //https://iid.googleapis.com/notification?notification_key_name=Hi-Health1
//        return "APA91bETZ4mQB36PSj6w7QQFIE6vJm8YXtrwM4FBBWyR3kdTe_nyT4qoZimj0SYGQm4KWXhsT4nYRM6iBQHIcvEzCR4GxTzbMU-H27RAprF_CX-T7K3Ngx0";
        //meu token
//        return "eHK1s3BDQQs:APA91bGhZ1Sk9dLpGlvz-riUV9qMrTlZ_WfKG-p6NJdkJLyRWeOFPXRFN43R066QAhR9MenVqhrxjJb9aPFUHVssUsQRPaHpGvyeM0R6x3XXsfFMtgFaJEJ-4qHKUeLnWlzc-8-UYUol";

        //token emulador api 25
        return "dUK_SbPXRI4:APA91bE1WG-yQ2Z3-Z-PQEG6lwglkK08k3mM6WZ9Qj5eZcdrTeI3T8PN70NNYpPEB-okfocSg6vzD4IlVunGw7bLY3byhF8rnvCoHn8rASmoi62BiBqRJn_R6EIwgE4ZHwmsOiglAkL-";

//        return "APA91bH1EsOjP_6VN_khTaDF1YDhWv88LwRF8-hUKCKZSqwkylnyekW36qoLsgAhs2QVN98sS-Oqxy7lYbeYd5cEISDw_UKd3Edw_NxWIIHVbglDsEhiI-E";
    }


}

