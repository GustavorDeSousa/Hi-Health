package br.com.thecharles.hihealth;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RequestFCM {

    private static final String TAG = RequestFCM.class.getSimpleName();
//    public static final BaseApi REST_API_INSTANCE = TwitterApi.instance();
//    public static final String REST_URL = "https://api.twitter.com/1.1";
//    public static final String REST_CONSUMER_KEY = "57fdgdfh345195e071f9a761d763ca0";
//    public static final String REST_CONSUMER_SECRET = "d657sdsg34435435";


    public void sendAlert(String json) throws IOException {
        requestPostFCM(json);
//        getDataFirebase();
//        Log.d(TAG,"Nome:"+ userName + " - " + "Token: " + tokenDevice);
//        Toast.makeText(getActivity(), "Nome:"+ userName + " - " + "Token: " + tokenDevice, Toast.LENGTH_SHORT).show();
    }

    public void requestPostFCM(String json) throws IOException{

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


}
