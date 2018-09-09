package br.com.thecharles.hihealth.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.thecharles.hihealth.R;
import br.com.thecharles.hihealth.model.User;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "Chat";
    private TextView tvName;
    private ImageView ivPhoto;
    private User recipientUser;

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


        //TODO Recuperar dados do usuário destinatario
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            recipientUser = (User) bundle.getSerializable("chatContact");

            Log.d(TAG, "Chat: " + recipientUser.getName() +
                    " - " + recipientUser.getEmail() + "\n");

            tvName.setText( recipientUser.getName());
            String photo = recipientUser.getPhoto();
            if (photo != null) {

            } else {
                ivPhoto.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
            }
        }
    }

}
