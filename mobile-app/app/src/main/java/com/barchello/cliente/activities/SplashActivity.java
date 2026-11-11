package com.barchello.cliente.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast; // Importación necesaria para el mensaje

import com.barchello.cliente.R;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Muestra un mensaje flotante al iniciar la aplicación
        Toast.makeText(this, "Bienvenido a Barchello Bávaro Pub", Toast.LENGTH_LONG).show();
        // -----------------------------------------

        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }, 2000);
    }
}