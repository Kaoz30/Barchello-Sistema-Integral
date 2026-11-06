package com.barchello.cliente.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.barchello.cliente.R;
import com.barchello.cliente.activities.SeguimientoPedidoActivity;

public class CarritoFragment extends Fragment {

    // Misma clave que en MainActivity
    private static final String PREF_NAME = "BarchelloPrefs";
    private static final String KEY_MODO = "modo_usuario";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_carrito, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Leer el modo desde SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String modo = prefs.getString(KEY_MODO, "invitado");

        // El panel de domicilio tiene él, id "ll_domicilio_options" en fragment_carrito.xml
        LinearLayout llDomicilio = view.findViewById(R.id.ll_domicilio_options);

        if (modo.equals("invitado")) {
            // Sí es invitado, ocultar las opciones de domicilio
            if (llDomicilio != null) {
                llDomicilio.setVisibility(View.GONE);
            }
            // Opcional: cambiar el texto del botón principal para dejar claro que solo puede pedir a mesa
            // view.findViewById(R.id.btn_enviar_pedido).setText("Enviar pedido a mesa (modo invitado)"();
        } else {
            // Si es registrado, mostrar el panel (por si estaba oculto)
            if (llDomicilio != null) {
                llDomicilio.setVisibility(View.VISIBLE);
            }
        }

        // Dentro de onViewCreated, después de enlazar los demás elementos
        Button btnEnviar = view.findViewById(R.id.btn_enviar_pedido);
        btnEnviar.setOnClickListener(v -> {
            // 1. Validación básica (que el carrito no esté vacío)
            // (Por simplicidad, asumimos que tiene al menos 1 item)

            // 2. Mostrar mensaje de confirmación
            Toast.makeText(getContext(), "✅ Pedido enviado. ¡Sigue su estado!", Toast.LENGTH_SHORT).show();

            // 3. Navegar a la pantalla de seguimiento
            Intent intent = new Intent(getActivity(), SeguimientoPedidoActivity.class);
            intent.putExtra("tipo", "activo"); // para saber que es un pedido en curso
            startActivity(intent);
        });
    }
}