package br.com.thecharles.hihealth.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;

import java.io.IOException;

import br.com.thecharles.hihealth.R;
import br.com.thecharles.hihealth.config.SettingsFirebase;
import br.com.thecharles.hihealth.model.Location;
import br.com.thecharles.hihealth.model.Notification;
import br.com.thecharles.hihealth.model.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DataFragment extends Fragment{

    private static final String TAG = DataFragment.class.getSimpleName();

    private SwipeRefreshLayout refreshLayout;

    private String tokenDevice;
    private String userName;
    //    private LatLng latLng;
    Notification notification = new Notification();

    TextView countdownText;

    CountDownTimer countDownTimer;
    long timeLeftInMilleSeconds = 30000;
    boolean timeRunning;


    DatabaseReference firebaseRef = SettingsFirebase.getFirebaseDatabase();
    DatabaseReference firebaseRefDebug = firebaseRef.child("debug").child("users");
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private ValueEventListener valueEventListenerNotication;
    private String userID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_data, container, false);

        refreshLayout = v.findViewById(R.id.swipe);
        refreshLayout.setOnRefreshListener(OnRefreshListener());

//        DatabaseReference reference = firebaseRefDebug.child("users");

        FirebaseUser user = firebaseAuth.getCurrentUser();
        userID = user.getUid();


        FloatingActionButton fab = v.findViewById(R.id.fab_warning);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());

                //Pegando o dialog
                final View mView = getLayoutInflater().inflate(R.layout.dialog_alert, null);

                //Buttons do Dialog
                Button mEnviar =  mView.findViewById(R.id.btnEnviar);
                Button mCancelar =  mView.findViewById(R.id.btnCancelar);


                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                mEnviar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        /** TESTE REQUEST FMC */
//                        getDataFirebase();
//                        new Connection().execute();
                        DatabaseReference userRef = firebaseRefDebug.child(userID);
                        valueEventListenerNotication =  userRef.addValueEventListener(new ValueEventListener() {


                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User usuario = new User();
                                Location location =  new Location();

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

                                    sendAlert(json);
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


//                        try {
//
//                            sendAlert();
////                            sendData();
////                            sendNotification();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }

//                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
//
//                        //Pegando o dialog
//                        View mView = getLayoutInflater().inflate(R.layout.dialog_sensor, null);
//
//
//                        //Button do Dialog
//                        Button mBem = mView.findViewById(R.id.btnBem);
////                        Button mMal = mView.findViewById(R.id.btnMal);
//                        countdownText = mView.findViewById(R.id.countdown_text);
//
//                        mBuilder.setView(mView);
//                        final AlertDialog dialog = mBuilder.create();
//                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                        dialog.show();
//
//                        //Metodo para inicializar o temporizador
//                        //startTimer();
//                        startTimer();
//
//
//
//
//                        mBem.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                Toast.makeText(getActivity(), "Alerta Emitido", Toast.LENGTH_SHORT).show();
//                                dialog.dismiss();
//                            }
//                        });

//                        mMal.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                Toast.makeText(getActivity(), "Alerta Cancelado", Toast.LENGTH_SHORT).show();
//                                dialog.dismiss();
//                            }
//                        });

                        dialog.dismiss();
                        Toast.makeText(getActivity(), "Alerta enviado com Sucesso !", Toast.LENGTH_LONG).show();

//                        Snackbar.make(mView, "Mensagem enviada com Sucesso", Snackbar.LENGTH_LONG)
//                                .setAction("Action", null).show();
                    }
                });

                mCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

            }
        });

        return  v;
    }

    private void sendAlert(String json) throws IOException {
        requestPostFCM(json);
//        getDataFirebase();
//        Log.d(TAG,"Nome:"+ userName + " - " + "Token: " + tokenDevice);
//        Toast.makeText(getActivity(), "Nome:"+ userName + " - " + "Token: " + tokenDevice, Toast.LENGTH_SHORT).show();
    }

    private String getFCMAlert() {
//
//        notification = colocarDados(getDataFirebase());
////
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("to", notification.getTokenUser());

        JsonObject notificationData = new JsonObject();
        notificationData.addProperty("name", notification.getNameUser());
        notificationData.addProperty("message", notification.getMessageAlert());
//        notificationData.addProperty("latlng", notification.getLatLngUser().toString());

        jsonObj.add("data", notificationData);

        JsonObject msgObj = new JsonObject();
        msgObj.add("message", jsonObj);

        Log.d(TAG,"data  message " + jsonObj.toString());
//
        return jsonObj.toString();

    }



//    public Notification getDataFirebase() {
//        DatabaseReference userRef = firebaseRefDebug.child(userID);
////        final
//        valueEventListenerNotication =  userRef.addValueEventListener(new ValueEventListener() {
//
//
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                User usuario = new User();
//                usuario.setName(dataSnapshot.child("registered").getValue(User.class).getName()); //set the name
//                usuario.setToken(dataSnapshot.child("registered").getValue(User.class).getToken()); //set the name
//
//                userName = usuario.getName();
//                tokenDevice = usuario.getToken();
//
//                Log.d(TAG, userName + " - " + tokenDevice);
//
////                Notification notification = new Notification();
////                LatLng latLng = new LatLng(-23.500712, -46.575707);
//                notification.setTokenUser(userName);
//                notification.setNameUser(tokenDevice);
//                notification.setMessageAlert("Não está bem, precisa de sua ajuda !");
//                notification.setLatLngUser(latLng);
//
//
//
////                return notification;
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
////        colocarDados(notification);
//        return notification ;
//
//
//    }
//
//    private Notification colocarDados(Notification notification) {
////        getDataFirebase();
////        notification = new Notification();
////        LatLng latLng = new LatLng(-23.500712, -46.575707);
////        notification.setTokenUser(tokenDevice);
////        notification.setNameUser(userName);
////        notification.setMessageAlert("Não está bem, precisa de sua ajuda !");
////        notification.setLatLngUser(latLng);
//
//        return notification;
//    }
//




//    public class Connection extends AsyncTask<Void, Void, Void> {

//        @Override
//        protected Void doInBackground(String... strings) {
//
//            OkHttpClient client;
//            client = new OkHttpClient();
//
//            String url = "https://fcm.googleapis.com/fcm/send";
//
//            Request.Builder builder = new Request.Builder()
//                    .addHeader("Content-Type", "application/json")
//                    .addHeader("Authorization", "key=AIzaSyDhbrMURhxQJBqZOSRm-7kGfUckEEiNpXg");
//
//            builder.url(url);
//
//            MediaType mediaType =
//                    MediaType.parse("application/json; charset=utf-8");
//
//            RequestBody body = RequestBody.create(mediaType, strings);
//            builder.post(body);
//
//            Request request = builder.build();
//
//            Response response = client.newCall(request).execute();
//
//            String jsonDeResposta = response.body().string();
//
////            return jsonDeResposta;
//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    Log.e(TAG, "error sending firebase app instance token to app server");
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    ResponseBody responseBody = response.body();
//                    if (!response.isSuccessful()) {
//                        throw new IOException
//                                ("Firebase app instance token to server status " + response);
//                    }
//
//                    Log.i(TAG, "Firebase app instance token has been sent to app server "
//                            +responseBody.string());
//                }
//            });
//
//            return null;
//        }



    private void requestPostFCM(String json) throws IOException{

        OkHttpClient client;
        client = new OkHttpClient();

        String url = "https://fcm.googleapis.com/fcm/send";

        Request.Builder builder = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "key=AIzaSyDhbrMURhxQJBqZOSRm-7kGfUckEEiNpXg");

        builder.url(url);

        MediaType mediaType =
                MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(mediaType, json);
        builder.post(body);

        Request request = builder.build();

//        Response response = client.newCall(request).execute();

//        String jsonDeResposta = response.body().string();

//            return jsonDeResposta;
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "error sending firebase app instance token to app server");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {
                    throw new IOException
                            ("Firebase app instance token to server status " + response);
                }

                Log.i(TAG, "Firebase app instance token has been sent to app server "
                        +responseBody.string());
            }
        });

    }

    private String getFCMDataMessage() {

        Item item = getClientTokenAndData();

        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("to", item.getToken());

        JsonObject itemInfo = new JsonObject();
        itemInfo.addProperty("itemName", item.getName());
        itemInfo.addProperty("itemPrice", item.getPrice());
        itemInfo.addProperty("location", item.getLocation());

        jsonObj.add("data", itemInfo);

//        JsonObject msgObj = new JsonObject();
//        msgObj.add("message", jsonObj);

        Log.d(TAG,"data  message " + jsonObj.toString());

        return jsonObj.toString();
    }

    private void sendNotification() throws IOException {
        String notificationTitle = "SOS";
        String notificationBody = "Caio está passando mal e precisa de sua ajuda !";

        requestPostFCM(getFCMNotificationMessage(notificationTitle, notificationBody));
    }

    private Item getClientTokenAndData() {
        Item item = new Item();
        item.setToken(getClientTokenDevice());
        item.setName("SeuCaiOo, ");
        item.setPrice("Não está bem, precisa de sua ajuda !");
        item.setLocation("SP");
        return item;
    }


    private String getFCMNotificationMessage(String title, String msg) {
        JsonObject jsonObj = new JsonObject();
        // client registration key is sent as token in the message to FCM server
//        jsonObj.addProperty("to", getClientToken());

        JsonObject notification = new JsonObject();
        notification.addProperty("body", msg);
        notification.addProperty("title", title);

//        JsonObject to = new JsonObject();


//        jsonObj.add("to", to);
        jsonObj.add("notification", notification);
        jsonObj.addProperty("to", getClientTokenDevice());
//        jsonObj.add("to", to);


//        JsonObject message = new JsonObject();
//        message.add("notication", notification);
//        message.add(, "to", to);

        Log.d(TAG,"notification message " + jsonObj.toString());

        return jsonObj.toString();
    }

    private String getClientTokenDevice() {

//            firebaseRefDebug.addValueEventListener(valueEventListenerNotication);


        //https://iid.googleapis.com/notification?notification_key_name=Hi-Health1
        return "APA91bETZ4mQB36PSj6w7QQFIE6vJm8YXtrwM4FBBWyR3kdTe_nyT4qoZimj0SYGQm4KWXhsT4nYRM6iBQHIcvEzCR4GxTzbMU-H27RAprF_CX-T7K3Ngx0";
//        return "APA91bH1EsOjP_6VN_khTaDF1YDhWv88LwRF8-hUKCKZSqwkylnyekW36qoLsgAhs2QVN98sS-Oqxy7lYbeYd5cEISDw_UKd3Edw_NxWIIHVbglDsEhiI-E";
    }

    private void sendData() throws IOException {
        requestPostFCM(getFCMDataMessage());
    }


    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMilleSeconds, 1000) {
            @Override
            public void onTick(long l) {
                timeLeftInMilleSeconds = l;
                updateTimer();
            }

            @Override
            public void onFinish() {

            }
        }.start();
        timeRunning = true;
    }

    private void updateTimer() {
        int minutes = (int) timeLeftInMilleSeconds / 60000;
        int seconds = (int) timeLeftInMilleSeconds % 60000 / 1000;

        String timeLeftText;

        timeLeftText = "" + minutes;
        timeLeftText += ":";

        if (seconds < 10 ) timeLeftText += "0";

        timeLeftText += seconds;

        countdownText.setText(timeLeftText);
    }

    private SwipeRefreshLayout.OnRefreshListener OnRefreshListener() {
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Fragment childAFragment = new ChildAFragment();
                Fragment childBFragment = new ChildBFragment();
                Fragment childCFragment = new ChildCFragment();
                Fragment childDFragment = new ChildDFragment();

                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

//        transaction.replace(R.id.child_fragment_container, childFragment);
                transaction.replace(R.id.child_fragment_a_container, childAFragment);
                transaction.replace(R.id.child_fragment_b_container, childBFragment);
                transaction.replace(R.id.child_fragment_c_container, childCFragment);
                transaction.replace(R.id.child_fragment_d_container, childDFragment);
                transaction.commit();

                refreshLayout.setRefreshing(false);
            }
        };
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
//        Fragment childFragment = new ChildFragment();
        Fragment childAFragment = new ChildAFragment();
        Fragment childBFragment = new ChildBFragment();
        Fragment childCFragment = new ChildCFragment();
        Fragment childDFragment = new ChildDFragment();

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

//        transaction.replace(R.id.child_fragment_container, childFragment);
        transaction.replace(R.id.child_fragment_a_container, childAFragment);
        transaction.replace(R.id.child_fragment_b_container, childBFragment);
        transaction.replace(R.id.child_fragment_c_container, childCFragment);
        transaction.replace(R.id.child_fragment_d_container, childDFragment);
        transaction.commit();


    }


    public static DataFragment newInstance() {
        return new DataFragment();
    }


    public void onDialog(View view) {

    }
}
