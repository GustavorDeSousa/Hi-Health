package br.com.thecharles.hihealth;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import br.com.thecharles.hihealth.fragments.ContactsFragment;
import br.com.thecharles.hihealth.fragments.DataFragment;
import br.com.thecharles.hihealth.fragments.ProfileFragment;

// TODO Corrigir bug de ciclo de vida
public class BottomNavigationActivity extends AppCompatActivity {


    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_data:
                    getSupportActionBar().setTitle(R.string.title_data);
                    Fragment dataFragment = DataFragment.newInstance();
                    openFragment(dataFragment);
                    return true;
                case R.id.navigation_contacts:
                    getSupportActionBar().setTitle(R.string.title_contacts);
                    Fragment contactsFragment = ContactsFragment.newInstance();
                    openFragment(contactsFragment);
                    return true;
                case R.id.navigation_profile:
                    getSupportActionBar().setTitle(R.string.title_profile);
                    Fragment profileFragment = ProfileFragment.newInstance();
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

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Fragment dataFragment = DataFragment.newInstance();
        getSupportActionBar().setTitle(R.string.title_data);
        openFragment(dataFragment);




    }


    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sign_out_menu) {
            firebaseAuth.signOut();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
