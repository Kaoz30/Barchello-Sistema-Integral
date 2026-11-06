package com.barchello.cliente.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.barchello.cliente.R;
import com.barchello.cliente.activities.MenuActivity;

public class EscanerFragment extends Fragment {

    private View lineaLaser;
    private TextView btnLinterna; // cambió a TextView
    private boolean flashOn = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_escaner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View viewEscaneo = view.findViewById(R.id.view_escaneo);
        lineaLaser = view.findViewById(R.id.linea_laser);
        btnLinterna = view.findViewById(R.id.btn_linterna);

        // Animación del láser (se ajusta a la altura del marco)
        viewEscaneo.post(() -> {
            int alturaMarco = viewEscaneo.getHeight();
            if (alturaMarco > 0) {
                animateLaser(alturaMarco);
            }
        });

        // simular escaneo al tocar el marco
        viewEscaneo.setOnClickListener(v -> {
            Toast.makeText(getContext(), getString(R.string.escaner_escaneando, 5), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), MenuActivity.class);
            intent.putExtra("idMesa", 5);
            intent.putExtra("categoria", "Todos"); // ←  LÍNEA nueva
            startActivity(intent);
        });

        // Botón linterna (simulado)
        btnLinterna.setOnClickListener(v -> {
            flashOn = !flashOn;
            if (flashOn) {
                // Cambiar el drawable superior del TextView
                btnLinterna.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_flash_on, 0, 0);
                Toast.makeText(getContext(), R.string.escaner_linterna_encendida, Toast.LENGTH_SHORT).show();
            } else {
                btnLinterna.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_flash_off, 0, 0);
                Toast.makeText(getContext(), R.string.escaner_linterna_apagada, Toast.LENGTH_SHORT).show();
            }
        });

        // Botón código de barras (simulado)
        view.findViewById(R.id.btn_codigo).setOnClickListener(v ->
                Toast.makeText(getContext(), R.string.escaner_codigo_barras, Toast.LENGTH_SHORT).show()
        );

        // Botón imagen (simulado)
        view.findViewById(R.id.btn_imagen).setOnClickListener(v ->
                Toast.makeText(getContext(), R.string.escaner_imagen_galeria, Toast.LENGTH_SHORT).show()
        );
    }

    private void animateLaser(int maxTranslate) {
        final Handler handler = new Handler();
        final int[] pos = { -maxTranslate / 2 }; // Empieza en el borde superior
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                pos[0] += 8;
                if (pos[0] > maxTranslate / 2) { // Si supera el borde inferior
                    pos[0] = -maxTranslate / 2;  // Reinicia en el borde superior
                }
                lineaLaser.setTranslationY(pos[0]);
                handler.postDelayed(this, 30);
            }
        };
        handler.post(runnable);
    }
}