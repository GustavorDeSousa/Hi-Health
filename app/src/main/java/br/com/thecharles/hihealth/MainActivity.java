package br.com.thecharles.hihealth;



import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final int RC_SIGN_IN = 123;


    private Button btnLogin, btnSignin;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        btnLogin = findViewById(R.id.btnLogin);
        btnSignin = findViewById(R.id.btnSignIn);


        //Iniciar App
//        btnStart = findViewById(R.id.btnStart);
//        btnStart.setOnClickListener(OnStart());


        // Initialize Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance();



    }

    @Override
    protected void onStart() {
        super.onStart();

//        btnLogin.setOnClickListener(onLogin());
        btnSignin.setOnClickListener(onSignin());
    }

    private View.OnClickListener onLogin() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        };
    }

    private View.OnClickListener onSignin() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        };
    }

    public void onLogin(View view) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
    }


//        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    // User is signed in
//                    Toast.makeText(MainActivity.this, "You're now signed in. Welcome to Hi-Health.", Toast.LENGTH_SHORT).show();
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
//                Intent intent = new Intent(MainActivity.this, BottomNavigationActivity.class);
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
