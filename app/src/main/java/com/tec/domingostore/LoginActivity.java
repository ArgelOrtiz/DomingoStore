package com.tec.domingostore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.tec.domingostore.DB.AuthFirebase;
import com.tec.domingostore.MainActivity;
import com.tec.domingostore.R;

public class LoginActivity extends AppCompatActivity {

    ConstraintLayout chargeConstraintLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initComponents();
    }

    private void initComponents(){
        final TextInputEditText emailTextInputEditText      = findViewById(R.id.emailLoginTextInputEditText);
        final TextInputEditText passwordTextInputEditText   = findViewById(R.id.passwordLoginTextInputEditText);
        Button loginButton                                  = findViewById(R.id.loginButton);
        chargeConstraintLayout                              = findViewById(R.id.chargeConstraintLayout);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email    = emailTextInputEditText.getText().toString();
                String passord  = passwordTextInputEditText.getText().toString();

                if (email.isEmpty()){
                    Snackbar.make(v,"Ingrese su correo",Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (passord.isEmpty()){
                    Snackbar.make(v,"Ingrese su contraseña",Snackbar.LENGTH_LONG).show();
                    return;
                }

                chargeConstraintLayout.setVisibility(View.VISIBLE);
                AuthFirebase.signIn(getApplicationContext(),email,passord);

            }
        });

        if (getApplicationContext() != null)
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(message,
                    new IntentFilter(AuthFirebase.TAG));
    }

    private BroadcastReceiver message = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            chargeConstraintLayout.setVisibility(View.GONE);

            if (intent.getBooleanExtra("login",false)){
                intent  = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }

        }
    };
}
