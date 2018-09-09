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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.thecharles.hihealth.R;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "User: ";

    private EditText edtEmail, edtPassword;
    private Button btnLogin, fab;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


//    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        final DatabaseReference reference = databaseReference.child("users");

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(onLogin(reference));




//        FirebaseUser user = firebaseAuth.getCurrentUser();
//        userID = user.getUid();




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

    private View.OnClickListener onLogin(final DatabaseReference reference) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginUser(reference);
                //Verifica usuario logado
//                if (firebaseAuth.getCurrentUser() != null) {
//
//                    Log.i("CurrentUser", "Usuario logado !");
//                } else {
//                    Log.i("CurrentUser", "Usuario não logado !");
//                }
            }
        };
    }

    private void loginUser(final DatabaseReference reference) {
        final String user = edtEmail.getText().toString();
        String pass = edtPassword.getText().toString();

        //Logar usuario
        firebaseAuth.signInWithEmailAndPassword(user, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                            DatabaseReference users = databaseReference.child("users");
//                            FirebaseUser userAtual = firebaseAuth.getCurrentUser();
//                            Toast.makeText(LoginActivity.this,
//                                    "Seja bem vindo: " + userAtual.getDisplayName(), Toast.LENGTH_SHORT).show();
                            if (firebaseAuth.getCurrentUser() != null) {
                                Intent intent = new Intent(LoginActivity.this, BottomNavigationActivity.class);
                                startActivity(intent);
                            }


                        /*    reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User usuario = new User();
                                    usuario.setName(dataSnapshot.child(userID).child("registered").getValue(User.class).getName()); //set the name
                                    usuario.setEmail(dataSnapshot.child(userID).child("registered").getValue(User.class).getEmail()); //set the email
                                    usuario.setPhone(dataSnapshot.child(userID).child("registered").getValue(User.class).getPhone()); //set the phone_num

                                    //display all the information
                                    Log.d(TAG, "showData: Nome: " + usuario.getName());
                                    Log.d(TAG, "showData: Email: " + usuario.getEmail());
                                    Log.d(TAG, "showData: Telefone: " + usuario.getPhone());
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

*/
//
//                            String chave = users.child("registred").getKey();
//                            Log.i("signIn", "Chave: " + chave );



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
