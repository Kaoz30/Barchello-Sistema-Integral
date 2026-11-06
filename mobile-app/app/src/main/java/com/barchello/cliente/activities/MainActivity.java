package com.barchello.cliente.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
//import android.os.Handler;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.barchello.cliente.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String PREF_NAME = "BarchelloPrefs";
    private static final String KEY_MODO = "modo_usuario";

    // Variables de clase para el BottomNavigationView y NavController
    private BottomNavigationView navView;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Recibir el modo (invitado/registrado)
        String modo = getIntent().getStringExtra("modo");
        if (modo == null || modo.isEmpty()) {
            modo = "invitado";
        }
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_MODO, modo).apply();

        // 2. Configurar navegación (BottomNav + NavHost)
        navView = findViewById(R.id.bottom_navigation);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(navView, navController);

        // 3. (NUEVO) Navegar al fragmento indicado desde el extra (primer inicio)
        int targetFragmentId = getIntent().getIntExtra("targetFragment", -1);
        if (targetFragmentId != -1) {
            navigateToTargetFragment(targetFragmentId);  // ← Ya no tiene delay
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // Guarda el nuevo intent como el actual

        int targetFragmentId = intent.getIntExtra("targetFragment", -1);
        if (targetFragmentId != -1) {
            navigateToTargetFragment(targetFragmentId); // ← Ya no tiene delay
        }
    }

    // Método privado para navegar a un fragmento específico (SIN Handler.postDelayed)
    private void navigateToTargetFragment(int targetFragmentId) {
        // La vista ya está lista, la navegación es inmediata.
        navView.setSelectedItemId(targetFragmentId);
        // CORREGIDO: popUpTo apunta al mismo destino, no al gráfico de navegación
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(targetFragmentId, true)  // ← AHORA APUNTA AL DESTINO MISMO
                .build();
        navController.navigate(targetFragmentId, null, navOptions);
    }
}
