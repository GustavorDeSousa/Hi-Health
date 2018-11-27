package br.com.thecharles.hihealth.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;

import br.com.thecharles.hihealth.MyFirebaseInstanceIDService;
import br.com.thecharles.hihealth.R;
import br.com.thecharles.hihealth.config.SettingsFirebase;
import br.com.thecharles.hihealth.model.Location;
import br.com.thecharles.hihealth.model.Sensor;
import br.com.thecharles.hihealth.model.User;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private EditText edtName, edtEmail, edtPassword, edtPhone,
            edtAddress, edtHeight, edtWeight;

//    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    DatabaseReference firebaseRef = SettingsFirebase.getFirebaseDatabase();
    DatabaseReference firebaseRefDebug = firebaseRef.child("debug");
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

//    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        FirebaseUser user = firebaseAuth.getCurrentUser();
//        userID = user.getUid();


        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        edtHeight = findViewById(R.id.edtHeight);
        edtWeight = findViewById(R.id.edtWeight);

        btnRegister = findViewById(R.id.btnCadastrar);
        btnRegister.setOnClickListener(onRegister());


    }

    private View.OnClickListener onRegister() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateUser();
            }
        };
    }

    public void registerUserFirebase(final User user, final Sensor sensor, final Location location) {

        firebaseAuth = SettingsFirebase.getFirebaseAutenticacao();
        firebaseAuth.createUserWithEmailAndPassword(
                user.getEmail(), user.getPassword()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    try {
                        String userId = firebaseAuth.getCurrentUser().getUid();
                        user.setId(userId);
                        user.save(userId);
                        sensor.save(userId);
                        location.save(userId);

                        openApp();

                        Toast.makeText(RegisterActivity.this, "Usuário cadastrado com Sucesso",
                                Toast.LENGTH_SHORT).show();
                        finish();

                    } catch (Exception e ) {
                        e.printStackTrace();
                    }

                } else {

                    String exception = "";
                    try {
                        throw task.getException();
                    }catch ( FirebaseAuthWeakPasswordException e){
                        exception = "Digite uma senha mais forte!";
                    }catch ( FirebaseAuthInvalidCredentialsException e){
                        exception= "Por favor, digite um e-mail válido";
                    }catch ( FirebaseAuthUserCollisionException e){
                        exception = "Este conta já foi cadastrada";
                    }catch (Exception e){
                        exception = "Erro ao cadastrar usuário: "  + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(RegisterActivity.this,
                            exception,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//

    public void validateUser() {
//        DatabaseReference users = databaseReference.child("users");

        //Recuperar textos dos campos
        String email = edtEmail.getText().toString();
        String pass = edtPassword.getText().toString();
        String name = edtName.getText().toString();
        String phone = edtPhone.getText().toString();
        String address = edtAddress.getText().toString();
        String height = edtHeight.getText().toString();
        String weight = edtWeight.getText().toString();

        if( !name.isEmpty() ){//verifica nome
            if( !email.isEmpty() ){//verifica e-mail
                if ( !pass.isEmpty() ){

                    MyFirebaseInstanceIDService myFirebaseInstanceIDService = new MyFirebaseInstanceIDService();
                    String token = myFirebaseInstanceIDService.getToken();
                    Log.d(TAG, "Token gerado: " + token);
//                    Toast.makeText(RegisterActivity.this, "", Toast.LENGTH_SHORT).show();

                    User user = new User();
//        user.setId(firebaseAuth.getCurrentUser().getUid());
                    user.setName(name);
                    user.setEmail(email);
                    user.setPassword(pass);
                    user.setPhone(phone);
                    user.setAddress(address);
                    user.setHeight(height);
                    user.setWeight(weight);
                    user.setToken(token);


                    Sensor sensor = new Sensor();
                    sensor.setHeartRate("0.0");
                    sensor.setHeartRateMax("0.0");
                    sensor.setHeartRateMin("0.0");
                    sensor.setStepCount("0");


                    Location location = new Location();
                    LatLng latLng = new LatLng(-23.533773, -46.625290);
                    location.setLatLng(latLng);

                    registerUserFirebase(user, sensor, location);


                }else {
                    Toast.makeText(RegisterActivity.this,
                            "Preencha a senha!",
                            Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(RegisterActivity.this,
                        "Preencha o email!",
                        Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(RegisterActivity.this,
                    "Preencha o nome!",
                    Toast.LENGTH_SHORT).show();
        }






//        users.push().child("registered").setValue(user);

        //Cadastrar usuario
//        firebaseAuth.createUserWithEmailAndPassword(
//                email, pass)
//                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
////                            DatabaseReference users = firebaseRefDebug.child("users");
//
////                            String pushKey = users.push().getKey();
//
//
//
////                            users.child(firebaseAuth.getCurrentUser().getUid()).child("sensor").setValue(sensor);
//
////
//
////                            users.child(firebaseAuth.getCurrentUser().getUid()).child("registered").setValue(user);
////                            users.child(pushKey).child("registered").setValue(user);
//
//
//
//
//
//                            Log.i("CreateUser", "Sucesso ao cadastar usuario !");
//                            openApp();
//                            Toast.makeText(RegisterActivity.this, "Usuário cadastrado com Sucesso",
//                                    Toast.LENGTH_SHORT).show();
//                        } else {
//
//                            Log.i("CreateUser", "Erro ao cadastar usuario !");
//                            finish();
//                            Toast.makeText(RegisterActivity.this, "Erro ao cadastrar Usuário",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });

//        authUser();

//

    }

    private void openApp() {

        Intent intent = new Intent(
                RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        //Verifica usuario logado
        if (firebaseAuth.getCurrentUser() != null) {
            Log.i("CurrentUser", "Usuario logado !");
        } else {
            Log.i("CurrentUser", "Usuario não logado !");
        }

    }
}
