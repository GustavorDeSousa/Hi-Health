package br.com.thecharles.hihealth;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import br.com.thecharles.hihealth.fragments.ContactsFragment;
import br.com.thecharles.hihealth.fragments.DataFragment;

// TODO Corrigir bug de ciclo de vida
public class BottomNavigationActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_data:
                    mTextMessage.setText(R.string.title_data);
                    getSupportActionBar().setTitle(R.string.title_data);
                    Fragment dataFragment = DataFragment.newInstance();
                    openFragment(dataFragment);
                    return true;
                case R.id.navigation_contacts:
                    mTextMessage.setText(R.string.title_contacts);
                    getSupportActionBar().setTitle(R.string.title_contacts);
                    Fragment contactsFragment = ContactsFragment.newInstance();
                    openFragment(contactsFragment);
                    return true;
                case R.id.navigation_profile:
                    mTextMessage.setText(R.string.title_profile);
                    getSupportActionBar().setTitle(R.string.title_profile);
                    Fragment profileFragment = DataFragment.newInstance();
                    openFragment(profileFragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
