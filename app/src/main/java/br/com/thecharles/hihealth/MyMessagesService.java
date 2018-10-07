package br.com.thecharles.hihealth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import br.com.thecharles.hihealth.config.SettingsFirebase;
import br.com.thecharles.hihealth.helper.UserFirebase;
import br.com.thecharles.hihealth.model.Message;
import br.com.thecharles.hihealth.model.Notification;
import br.com.thecharles.hihealth.model.User;

public class MyMessagesService extends Service {


    private static final String TAG = MyMessagesService.class.getSimpleName();
    private User receiverUser;
    private DatabaseReference messagesRef;
    private ChildEventListener childEventListenerMessages;

    private String idUserSender;
    private String idUserReceiver;

    DatabaseReference database = SettingsFirebase.getFirebaseDatabase();
    DatabaseReference firebaseRefDebug = database.child("debug");
    //    DatabaseReference messageRef = firebaseRefDebug.child("messages");
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    ValueEventListener valueEventListenerUser;
    Notification notification = new Notification();
    private String userID;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void getMessagesFCM() {
        userID = UserFirebase.getUId();
        DatabaseReference messageRef = firebaseRefDebug.child("messages").child(userID);

//        messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.e(TAG, dataSnapshot.getValue().toString());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


        valueEventListenerUser = messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot node : dataSnapshot.getChildren()) {
//                    node.child("gOVk1Rt0UWdpN43xSLExwRKiLZA3").

                    String idSender = node.getKey();

//                    Iterable<DataSnapshot> idMsg = node.child(idSender).getChildren();
//
//                    DataSnapshot data = idMsg.iterator().next();
//
//                    data.getValue();
//
//                    String msg = node.child(idSender).getValue(String.class);

//                    node.child(idMsg)
//                    Log.e(TAG, node.getKey());
                    Log.e(TAG, "Value >" + node.getValue().toString());
                    Log.e(TAG, "Key Sender >" + idSender);
//                    Log.e(TAG, "Key msg >" + idMsg);
//                    Log.e(TAG, "Teste > " + msg);



//                    Log.e(TAG, node.getChildren().toString());
                    Log.e(TAG, String.valueOf(node.getChildrenCount()));

                }
//                Message message = dataSnapshot.getValue(Message.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        childEventListenerMessages = messagesRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
////                Message message = dataSnapshot.getValue(Message.class);
//                Log.e(TAG, dataSnapshot.getValue(String.class));
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

    }



}
