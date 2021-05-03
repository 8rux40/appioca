package com.tardin.appioca;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Checa se o usuário já está logado e joga para a tela principal ou para a tela de login
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                if (user == null)
                    startActivity(new Intent(getBaseContext(), LoginActivity.class));
                else
                    startActivity(new Intent(getBaseContext(), MainActivity.class));
                finish();
            }
        }, 3000);
    }
}