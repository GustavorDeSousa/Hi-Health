package br.com.thecharles.hihealth.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import br.com.thecharles.hihealth.R;
import br.com.thecharles.hihealth.config.SettingsFirebase;
import br.com.thecharles.hihealth.model.User;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    private EditText edtEmail, edtPassword;
    private Button btnLogin, fab;

//    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    DatabaseReference firebaseRef = SettingsFirebase.getFirebaseDatabase();
    DatabaseReference firebaseRefDebug = firebaseRef.child("debug");

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


//    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        final DatabaseReference reference = firebaseRefDebug.child("users");

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(onLogin());




//        FirebaseUser user = firebaseAuth.getCurrentUser();
//        userID = user.getUid();




    }

//
//    private View.OnClickListener onRegister() {
//        return new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                registerUser();
//            }
//        };
//    }
//
//    private void registerUser() {
//        String user = edtEmail.getText().toString();
//        String pass = edtPassword.getText().toString();
//        //Cadastrar usuario
//        firebaseAuth.createUserWithEmailAndPassword(
//                user, pass)
//                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            Log.i("CreateUser", "Sucesso ao cadastar usuario !");
//                        } else {
//
//                            Log.i("CreateUser", "Erro ao cadastar usuario !");
//
//                        }
//                    }
//                });
//    }

    private View.OnClickListener onLogin() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validateUser();
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

    private void validateUser() {
        String userEmail = edtEmail.getText().toString();
        String userPass = edtPassword.getText().toString();

        if( !userEmail.isEmpty() ){//verifica e-mail
            if ( !userPass.isEmpty() ){

                User user = new User();
                user.setEmail(userEmail);
                user.setPassword(userPass);

                loginUserFirebase(user);

            }else {
                Toast.makeText(LoginActivity.this,
                        "Preencha a senha!",
                        Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(LoginActivity.this,
                    "Preencha o email!",
                    Toast.LENGTH_SHORT).show();
        }
    }


    public void loginUserFirebase(final User user) {
        firebaseAuth = SettingsFirebase.getFirebaseAutenticacao();
        firebaseAuth.signInWithEmailAndPassword(
                user.getEmail(), user.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    openIntent(MainActivity.class);
//                    Toast.makeText(MainActivity.this,
//                                    "Seja bem vindo: " + user.getName(), Toast.LENGTH_SHORT).show();

                } else {
                    String exception = "";
                    try {
                        throw task.getException();
                    }catch ( FirebaseAuthInvalidUserException e ) {
                        exception = "Usuário não está cadastrado.";
                        openIntent(RegisterActivity.class);
                    }catch ( FirebaseAuthInvalidCredentialsException e ){
                        exception = "E-mail e senha não correspondem a um usuário cadastrado";
                    }catch (Exception e){
                        exception = "Erro ao cadastrar usuário: "  + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(LoginActivity.this,
                            exception,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openIntent(Class classOpen) {
        Intent intent = new Intent(LoginActivity.this, classOpen);
        startActivity( intent );
        finish();
    }


}


//        //Logar usuario
//        firebaseAuth.signInWithEmailAndPassword(user, pass)
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
////                            DatabaseReference users = databaseReference.child("users");
////                            FirebaseUser userAtual = firebaseAuth.getCurrentUser();
////                            Toast.makeText(LoginActivity.this,
////                                    "Seja bem vindo: " + userAtual.getDisplayName(), Toast.LENGTH_SHORT).show();
//                            if (firebaseAuth.getCurrentUser() != null) {
//                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                                startActivity(intent);
//                            }
//

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



//                            Log.i("signIn", "Sucesso ao logar usuario !");
//                        } else {
//
//                            Log.i("signIn", "Erro ao logar usuario !");
//                            Toast.makeText(LoginActivity.this,
//                                    "Usuário não Encontrado !", Toast.LENGTH_SHORT).show();
//                            finish();
//
//
//                        }
//                    }
//                });
//
//    }
//    }
//}
