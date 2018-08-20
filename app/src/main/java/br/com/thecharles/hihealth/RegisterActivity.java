package br.com.thecharles.hihealth;

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

import br.com.thecharles.hihealth.model.User;

public class RegisterActivity extends AppCompatActivity {

    private Button btnRegister;
    private EditText edtName, edtEmail, edtPassword, edtPhone, edtDocument, edtAddress, edtHeight, edtWeight;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
        DatabaseReference users = databaseReference.child("users");

        String name = edtName.getText().toString();
        String email = edtEmail.getText().toString();
        String phone = edtPhone.getText().toString();
        String document = edtDocument.getText().toString();
        String address = edtAddress.getText().toString();
        String height = edtHeight.getText().toString();
        String weight = edtWeight.getText().toString();

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setAddress(address);
        user.setHeight(height);
        user.setWeight(weight);
        user.setDocument(document);
        users.push().child("registered").setValue(user);

        authUser();

//       String chave = users.child("registred").getKey();

    }

    private void authUser() {
        String user = edtEmail.getText().toString();
        String pass = edtPassword.getText().toString();
        //Cadastrar usuario
        firebaseAuth.createUserWithEmailAndPassword(
                user, pass)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            DatabaseReference users = databaseReference.child("users");

                            String chave = users.child("registred").getKey();
                            Log.i("signIn", "Chave: " + chave );

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
