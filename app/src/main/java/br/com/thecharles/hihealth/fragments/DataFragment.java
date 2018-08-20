package br.com.thecharles.hihealth.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.thecharles.hihealth.R;

public class DataFragment extends Fragment{



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_data, container, false);
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
}
