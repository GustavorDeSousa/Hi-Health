package br.com.thecharles.hihealth.fragments;

import android.app.Dialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import br.com.thecharles.hihealth.R;
import br.com.thecharles.hihealth.activity.MainActivity;

public class DataFragment extends Fragment{

    private SwipeRefreshLayout refreshLayout;

    TextView countdownText;

    CountDownTimer countDownTimer;
    long timeLeftInMilleSeconds = 30000;
    boolean timeRunning;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_data, container, false);

        refreshLayout = v.findViewById(R.id.swipe);
        refreshLayout.setOnRefreshListener(OnRefreshListener());

        FloatingActionButton fab = v.findViewById(R.id.fab_warning);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());

                //Pegando o dialog
                View mView = getLayoutInflater().inflate(R.layout.dialog_alert, null);

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
                        Toast.makeText(getActivity(), "Alerta enviada com sucesso", Toast.LENGTH_SHORT).show();

                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());

                        //Pegando o dialog
                        View mView = getLayoutInflater().inflate(R.layout.dialog_sensor, null);


                        //Button do Dialog
                        Button mBem = mView.findViewById(R.id.btnBem);
//                        Button mMal = mView.findViewById(R.id.btnMal);
                        countdownText = mView.findViewById(R.id.countdown_text);

                        mBuilder.setView(mView);
                        final AlertDialog dialog = mBuilder.create();
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();

                        //Metodo para inicializar o temporizador
                        //startTimer();
                        startTimer();




                        mBem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(getActivity(), "Alerta Emitido", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });

//                        mMal.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                Toast.makeText(getActivity(), "Alerta Cancelado", Toast.LENGTH_SHORT).show();
//                                dialog.dismiss();
//                            }
//                        });

                    }
                });

                mCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        return  v;
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
