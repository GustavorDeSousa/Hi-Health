package br.com.thecharles.hihealth;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import br.com.thecharles.hihealth.config.SettingsFirebase;
import br.com.thecharles.hihealth.helper.UserFirebase;
import br.com.thecharles.hihealth.model.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    DatabaseReference firebaseRef = SettingsFirebase.getFirebaseDatabase();
    DatabaseReference firebaseRefDebug = firebaseRef.child("debug");
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private static final String TAG = FirebaseInstanceIdService.class.getSimpleName();
    private static final String SEND_TOKEN_SERVICE_URL =
            "http://yourAppServer/instanceTokenService";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);

        registerToken(refreshedToken);
    }

    private void registerToken(String refreshedToken) {
        String idUserSender = UserFirebase.getUId();
        DatabaseReference reference =
                firebaseRefDebug.child("users").child(idUserSender);
        User user = new User();
        user.setToken(refreshedToken);

        reference.child("registered").child("token").setValue(refreshedToken);

    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }


    private void sendInstanceIdTokenToServer(String token) {
        OkHttpClient httpClient = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("token", token)
                .build();
        Request request = new Request.Builder()
                .url(SEND_TOKEN_SERVICE_URL)
                .post(formBody)
                .build();


        httpClient.newCall(request).enqueue(new Callback() {
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
}

