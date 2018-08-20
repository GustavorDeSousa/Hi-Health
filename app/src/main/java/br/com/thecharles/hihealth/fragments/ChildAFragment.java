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

import java.text.DateFormat;
import java.util.concurrent.TimeUnit;

import br.com.thecharles.hihealth.R;

public class ChildAFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener{

    private TextView tvRateHeart;

    private GoogleApiClient mGoogleApiClient;

    private Context context = getContext();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_child_a, container, false);

        tvRateHeart = v.findViewById(R.id.tvRateHeart);

        // Todo - Fazer os dados retornar corretamente

//        mGoogleApiClient = new GoogleApiClient.Builder(ChildAFragment.this)
//                .addApi(Fitness.HISTORY_API)
//                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
//                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
//                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
//                .addConnectionCallbacks(this)
//                .build();

        return v;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.fragmentA: {
                new ViewTodaysRateHeartTask().execute();
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("HistoryAPI", "onConnected");

    }

    private void displayRateHeartDataForToday() {
        DailyTotalResult result = Fitness.HistoryApi.readDailyTotal(
                mGoogleApiClient, DataType.TYPE_HEART_RATE_BPM).await(1, TimeUnit.MINUTES);
        showDataSet(result.getTotal());
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

                tvRateHeart.setText(String.valueOf(dp.getValue(field)) + " BPM");

//                if(field.getName().equals(F)){
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        if (dp.getDataType().getName().equals("com.google.heart_rate.summary")) {
//                            tvRateHeart.setText(String.valueOf(dp.getValue(field)) + " BPM");
//                        }

//                    }
//                });

//                }

            }
        }
    }





    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /** AssyncTasks */
    private class ViewTodaysRateHeartTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            displayRateHeartDataForToday();
            return null;
        }
    }
}
