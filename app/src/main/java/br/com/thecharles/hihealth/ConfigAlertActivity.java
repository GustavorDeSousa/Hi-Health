package br.com.thecharles.hihealth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;

public class ConfigAlertActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_alert);
    }

    public void enviarSms(View view) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(
                "011995083035",
                null,
                "Teste Aplicativo Hi-Health",
                null,
                null
        );
    }
}
