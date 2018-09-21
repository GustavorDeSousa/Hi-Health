package br.com.thecharles.hihealth.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import br.com.thecharles.hihealth.R;
import br.com.thecharles.hihealth.config.SettingsFirebase;


public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = WelcomeActivity.class.getName();

    public static final int RC_SIGN_IN = 123;

    // Firebase instance variables
//    private FirebaseAuth mFirebaseAuth;
//    private FirebaseAuth.AuthStateListener mAuthStateListener;
//    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    DatabaseReference firebaseRef = SettingsFirebase.getFirebaseDatabase();
    DatabaseReference firebaseRefDebug = firebaseRef.child("debug");

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);




        // Initialize Firebase components
//        mFirebaseAuth = FirebaseAuth.getInstance();



    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

//        Log.d(TAG, " - " + currentUser + " - "  + " - " +  firebaseUser.getEmail() + " - " + firebaseUser.getProviderId() );
//

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if ( user != null ) {
            if ( currentUser != null ){
//            Log.d(TAG, " - " + currentUser + " - "  + " - " +  mFirebaseAuth.getCurrentUser().getEmail() + " - " + mFirebaseAuth.getCurrentUser().getProviderId() );
                openIntent(MainActivity.class);
        }
        }

    }

    private void openIntent(Class classOpen) {
        Intent intent = new Intent(WelcomeActivity.this, classOpen);
        startActivity(intent);
        finish();
    }

//    private View.OnClickListener onLogin() {
//        return new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openIntent(LoginActivity.class);
//            }
//        };
//    }
//
//    private View.OnClickListener onSignin() {
//        return new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openIntent(RegisterActivity.class);
//            }
//        };
//    }

    public void register(View view) {
        openIntent(RegisterActivity.class);
    }

    public void onLogin(View view) {
        openIntent(LoginActivity.class);
    }
//
//    public void onLogin(View view) {
//        if (firebaseAuth.getCurrentUser() != null) {
//
//            Log.i("CurrentUser", "Usuario logado !");
//            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
//            startActivity(intent);
//            Toast.makeText(WelcomeActivity.this, "Bem Vindo de Volta", Toast.LENGTH_SHORT).show();
//        } else {
//            Log.i("CurrentUser", "Usuario não logado !");
//            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
//            startActivity(intent);
//        }
//
//    }


//        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    // User is signed in
//                    Toast.makeText(WelcomeActivity.this, "You're now signed in. Welcome to Hi-Health.", Toast.LENGTH_SHORT).show();
//                } else {
//                    // User is signed out
//                    startActivityForResult(
//                            AuthUI.getInstance()
//                                    .createSignInIntentBuilder()
//                                    .setIsSmartLockEnabled(false)
//                                    .setTheme(R.style.GreenTheme)
//                                    .setLogo(R.drawable.logo)
//                                    .setAvailableProviders(Arrays.asList(
//                                            new AuthUI.IdpConfig.EmailBuilder().build(),
//                                            new AuthUI.IdpConfig.GoogleBuilder().build()
////                                            TODO implementar LoginActivity pelo Facebook
////                                            ,new AuthUI.IdpConfig.FacebookBuilder().build()
//                                    ))
//                                    .build(),
//                            RC_SIGN_IN);
//                }
//            }
//        };
//    }

//
//    private View.OnClickListener OnStart() {
//        return new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
//                startActivity(intent);
//            }
//        };
//    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RC_SIGN_IN) {
//            if (resultCode == RESULT_OK) {
//                // Sign-in succeeded, set up the UI
//                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
//            } else if (resultCode == RESULT_CANCELED) {
//                // Sign in was canceled by the user, finish the activity
//                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        }
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
//    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (mAuthStateListener != null) {
//            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
//        }
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main_menu, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.sign_out_menu:
//                AuthUI.getInstance().signOut(this);
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }


}
