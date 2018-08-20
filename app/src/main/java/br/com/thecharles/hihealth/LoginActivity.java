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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(onLogin());

        DatabaseReference users =  databaseReference.child("users");


    }

    private View.OnClickListener onRegister() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        };
    }

    private void registerUser() {
        String user = edtEmail.getText().toString();
        String pass = edtPassword.getText().toString();
        //Cadastrar usuario
        firebaseAuth.createUserWithEmailAndPassword(
                user, pass)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i("CreateUser", "Sucesso ao cadastar usuario !");
                        } else {

                            Log.i("CreateUser", "Erro ao cadastar usuario !");

                        }
                    }
                });
    }

    private View.OnClickListener onLogin() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
                //Verifica usuario logado
                if (firebaseAuth.getCurrentUser() != null) {
                    Log.i("CurrentUser", "Usuario logado !");
                } else {
                    Log.i("CurrentUser", "Usuario não logado !");
                }
            }
        };
    }

    private void loginUser() {
        final String user = edtEmail.getText().toString();
        String pass = edtPassword.getText().toString();

        //Logar usuario
        firebaseAuth.signInWithEmailAndPassword(user, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            DatabaseReference users = databaseReference.child("users");

                            String chave = users.child("registred").getKey();
                            Log.i("signIn", "Chave: " + chave );


                                Intent intent = new Intent(LoginActivity.this, BottomNavigationActivity.class);
                                startActivity(intent);
                                finish();

                            Log.i("signIn", "Sucesso ao logar usuario !");
                        } else {

                            Log.i("signIn", "Erro ao logar usuario !");
                            Toast.makeText(LoginActivity.this,
                                    "Usuário não Encontrado !", Toast.LENGTH_SHORT).show();
                            finish();


                        }
                    }
                });

    }
}
