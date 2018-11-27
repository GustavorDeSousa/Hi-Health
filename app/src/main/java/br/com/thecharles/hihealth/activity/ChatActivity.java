package br.com.thecharles.hihealth.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import br.com.thecharles.hihealth.MyFirebaseMessagingService;
import br.com.thecharles.hihealth.NotificationFCM;
import br.com.thecharles.hihealth.R;
import br.com.thecharles.hihealth.adapter.MessagesAdapter;
import br.com.thecharles.hihealth.config.SettingsFirebase;
import br.com.thecharles.hihealth.helper.UserFirebase;
import br.com.thecharles.hihealth.model.Message;
import br.com.thecharles.hihealth.model.User;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "Chat";
    private TextView tvName;
    private ImageView ivPhoto;
    private EditText editMsg;
    private User receiverUser;
    private DatabaseReference database;
    private DatabaseReference messagesRef;
    private ChildEventListener childEventListenerMessages;

    private String idUserSender;
    private String idUserReceiver;

    private RecyclerView recyclerMessages;
    private MessagesAdapter adapter;
    private List<Message> messages = new ArrayList<>();

    String name;
    String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvName = findViewById(R.id.tvNameChat);
        ivPhoto = findViewById(R.id.ivPhotoChat);
        editMsg = findViewById(R.id.editMsg);
        recyclerMessages = findViewById(R.id.recyclerMessages);


        //TODO Recuperar dados do usario remetente
        idUserSender = UserFirebase.getUId();



        //TODO Recuperar dados do usuário destinatario
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            receiverUser = (User) bundle.getSerializable("chatContact");


            Log.d(TAG, "Chat: " + receiverUser.getName() +
                    " - " + receiverUser.getEmail() + "\n");

            tvName.setText( receiverUser.getName());
            String photo = receiverUser.getPhoto();
            if (photo != null) {

            } else {
                ivPhoto.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
            }

            //recuperar dados usario destinatario
            idUserReceiver = receiverUser.getId();
        }

        //Config Adapter
        adapter = new MessagesAdapter(messages, getApplicationContext());

        //Config RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMessages.setLayoutManager(layoutManager);
        recyclerMessages.setHasFixedSize(true);
        recyclerMessages.setAdapter(adapter);

        database = SettingsFirebase.getFirebaseDatabase();
        DatabaseReference firebaseRefDebug = database.child("debug");
        messagesRef = firebaseRefDebug.child("messages")
                .child(idUserSender)
                .child(idUserReceiver);
    }

    public void sendMessage(View view) {
        String stringMessage = editMsg.getText().toString();


        if (!stringMessage.isEmpty()) {
            Message message = new Message();
            message.setIdSender(idUserSender);
            message.setMessage(stringMessage);

            //TODO Salvar menssagem para o remetente
            saveMessage(idUserSender,idUserReceiver, message);
            //TODO Salvar menssagem para o destinatário
            saveMessage(idUserReceiver,idUserSender, message);

            NotificationFCM messageNotification = new NotificationFCM();
            messageNotification.getMessageChat(stringMessage, idUserReceiver);
        } else {
            Toast.makeText(ChatActivity.this,
                    "Digite uma mensagem para enviar.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveMessage(String idSender, String idReceiver, Message msg) {
        DatabaseReference database = SettingsFirebase.getFirebaseDatabase();
        DatabaseReference firebaseRefDebug = database.child("debug");
        DatabaseReference messageRef = firebaseRefDebug.child("messages");

        messageRef.child(idSender)
                .child(idReceiver)
                .push()
                .setValue(msg);

        //Limpar texto
        editMsg.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        getMessages();
    }

    @Override
    protected void onStop() {
        super.onStop();
        messagesRef.removeEventListener(childEventListenerMessages);
    }

    private void getMessages() {

        childEventListenerMessages = messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                messages.add(message);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
