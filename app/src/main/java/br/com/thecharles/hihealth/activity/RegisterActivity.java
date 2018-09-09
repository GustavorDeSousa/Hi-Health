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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.thecharles.hihealth.R;
import br.com.thecharles.hihealth.model.Sensor;
import br.com.thecharles.hihealth.model.User;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "User: ";
    private Button btnRegister;
    private EditText edtName, edtEmail, edtPassword, edtPhone, edtDocument,
            edtAddress, edtHeight, edtWeight;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
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
        edtDocument = findViewById(R.id.edtDocument);
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
                saveUserData();
            }
        };
    }

    private void saveUserData() {
//        DatabaseReference users = databaseReference.child("users");

        String email = edtEmail.getText().toString();
        String pass = edtPassword.getText().toString();


//        users.push().child("registered").setValue(user);

        //Cadastrar usuario
        firebaseAuth.createUserWithEmailAndPassword(
                email, pass)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            DatabaseReference users = databaseReference.child("users");


                            String name = edtName.getText().toString();
                            String phone = edtPhone.getText().toString();
                            String document = edtDocument.getText().toString();
                            String address = edtAddress.getText().toString();
                            String height = edtHeight.getText().toString();
                            String weight = edtWeight.getText().toString();


                            Sensor sensor = new Sensor();
                            sensor.setHeartRate("0.0");
                            sensor.setHeartRateMax("0.0");
                            sensor.setHeartRateMin("0.0");
                            sensor.setStepCount("0");

                            users.child(firebaseAuth.getCurrentUser().getUid()).child("sensor").setValue(sensor);


                            User user = new User();
                            user.setName(name);
                            user.setEmail(firebaseAuth.getCurrentUser().getEmail());
                            user.setPhone(phone);
                            user.setAddress(address);
                            user.setHeight(height);
                            user.setWeight(weight);
                            user.setDocument(document);
                            users.child(firebaseAuth.getCurrentUser().getUid()).child("registered").setValue(user);






                            Log.i("CreateUser", "Sucesso ao cadastar usuario !");
                            openApp();
                            Toast.makeText(RegisterActivity.this, "Usuário cadastrado com Sucesso",
                                    Toast.LENGTH_SHORT).show();
                        } else {

                            Log.i("CreateUser", "Erro ao cadastar usuario !");
                            finish();
                            Toast.makeText(RegisterActivity.this, "Erro ao cadastrar Usuário",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

//        authUser();

//

    }

    private void openApp() {

        Intent intent = new Intent(
                RegisterActivity.this, BottomNavigationActivity.class);
        startActivity(intent);
        finish();
        //Verifica usuario logado
        if (firebaseAuth.getCurrentUser() != null) {
            Log.i("CurrentUser", "Usuario logado !");
        } else {
            Log.i("CurrentUser", "Usuario não logado !");
        }

    }
}
