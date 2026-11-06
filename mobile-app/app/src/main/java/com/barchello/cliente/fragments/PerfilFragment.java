package com.barchello.cliente.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.barchello.cliente.R;
import com.barchello.cliente.activities.LoginActivity;
import com.barchello.cliente.activities.SeguimientoPedidoActivity;

public class PerfilFragment extends Fragment {

    private static final String PREF_NAME = "BarchelloPrefs";
    private static final String KEY_MODO = "modo_usuario";
    private static final String KEY_PUNTOS = "puntos_usuario";  // Clave para guardar puntos

    private TextView tvPuntos;
    private SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String modo = prefs.getString(KEY_MODO, "invitado");
        boolean esRegistrado = modo.equals("registrado");

        // Referencias a elementos visuales
        View cardPuntos = view.findViewById(R.id.card_puntos);
        Button btnEliminarCuenta = view.findViewById(R.id.btn_eliminar_cuenta);
        tvPuntos = view.findViewById(R.id.tv_puntos);
        Button btnCanjear = view.findViewById(R.id.btn_canjear);

        // --- Ocultar/mostrar según modo ---
        if (!esRegistrado) {
            if (cardPuntos != null) cardPuntos.setVisibility(View.GONE);
            if (btnEliminarCuenta != null) btnEliminarCuenta.setVisibility(View.GONE);
        } else {
            if (cardPuntos != null) cardPuntos.setVisibility(View.VISIBLE);
            if (btnEliminarCuenta != null) btnEliminarCuenta.setVisibility(View.VISIBLE);
            // Cargar puntos guardados o inicializar con 1250 si no existen
            cargarPuntos();
        }

        // --- Botón Cerrar sesión ---
        Button btnCerrarSesion = view.findViewById(R.id.btn_cerrar_sesion);
        if (btnCerrarSesion != null) {
            btnCerrarSesion.setOnClickListener(v -> {
                if (esRegistrado) {
                    prefs.edit().remove(KEY_MODO).remove(KEY_PUNTOS).apply();
                }
                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            });
        }

        // --- Botón Historial ---
        Button btnHistorial = view.findViewById(R.id.btn_historial);
        if (btnHistorial != null) {
            btnHistorial.setOnClickListener(v -> {
                if (esRegistrado) {
                    startActivity(new Intent(requireActivity(), SeguimientoPedidoActivity.class));
                } else {
                    Toast.makeText(requireContext(), "Debes registrarte para ver tu historial", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // --- Botón Soporte ---
        Button btnSoporte = view.findViewById(R.id.btn_soporte);
        if (btnSoporte != null) {
            btnSoporte.setOnClickListener(v ->
                    Toast.makeText(requireContext(), "Abrir soporte (WhatsApp, teléfono)", Toast.LENGTH_SHORT).show()
            );
        }

        // --- Botón Configuración ---
        Button btnConfiguracion = view.findViewById(R.id.btn_configuracion);
        if (btnConfiguracion != null) {
            btnConfiguracion.setOnClickListener(v ->
                    Toast.makeText(requireContext(), "Configuración (notificaciones, temas)", Toast.LENGTH_SHORT).show()
            );
        }

        // --- Botón Eliminar cuenta ---
        if (btnEliminarCuenta != null) {
            btnEliminarCuenta.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                    .setTitle("Eliminar cuenta")
                    .setMessage("¿Estás seguro? Esta acción es irreversible y perderás tus puntos e historial.")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        Toast.makeText(requireContext(), "Cuenta eliminada (simulado)", Toast.LENGTH_SHORT).show();
                        prefs.edit().clear().apply();
                        Intent intent = new Intent(requireActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        requireActivity().finish();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show());
        }

        // --- Botón Canjear puntos (mejorado) ---
        if (btnCanjear != null) {
            btnCanjear.setOnClickListener(v -> {
                if (!esRegistrado) {
                    Toast.makeText(requireContext(), "Regístrate para acumular puntos", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Obtener puntos actuales
                int puntosActuales = prefs.getInt(KEY_PUNTOS, 0);
                if (puntosActuales == 0) {
                    Toast.makeText(requireContext(), "No tienes puntos suficientes para canjear", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Definir productos canjeables (nombre y costo en puntos)
                final String[] productos = {"Cerveza artesanal", "Hamburguesa", "Postre especial"};
                final int[] costos = {500, 1000, 300};

                // Crear lista de opciones con formato "nombre - XXX pts"
                String[] opciones = new String[productos.length];
                for (int i = 0; i < productos.length; i++) {
                    opciones[i] = productos[i] + " - " + costos[i] + " pts";
                }

                new AlertDialog.Builder(requireContext())
                        .setTitle("Canjear puntos")
                        .setItems(opciones, (dialog, which) -> {
                            int costo = costos[which];
                            if (puntosActuales >= costo) {
                                // Restar puntos
                                int nuevosPuntos = puntosActuales - costo;
                                prefs.edit().putInt(KEY_PUNTOS, nuevosPuntos).apply();
                                // Actualizar TextView
                                tvPuntos.setText(String.format(getString(R.string.formato_puntos), nuevosPuntos));
                                Toast.makeText(requireContext(), "Has canjeado: " + productos[which], Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireContext(), "No tienes suficientes puntos para canjear " + productos[which], Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
            });
        }
    }

    /**
     * Carga los puntos desde SharedPreferences. Si no existen, inicializa con 1250 puntos de ejemplo.
     * Actualiza el TextView tvPuntos.
     */
    private void cargarPuntos() {
        int puntos = prefs.getInt(KEY_PUNTOS, 0);
        if (puntos == 0) {
            // Puntos iniciales de ejemplo (simula que ya tiene 1250)
            puntos = 1250;
            prefs.edit().putInt(KEY_PUNTOS, puntos).apply();
        }
        tvPuntos.setText(String.format(getString(R.string.formato_puntos), puntos));
    }
}