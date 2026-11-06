package com.barchello.cliente.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.barchello.cliente.R;

import java.util.ArrayList;
import java.util.List;

public class NotificacionesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notificaciones, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvNotificaciones = view.findViewById(R.id.rv_notificaciones);
        rvNotificaciones.setLayoutManager(new LinearLayoutManager(getContext()));

        // Datos simulados
        List<String> titulos = new ArrayList<>();
        List<String> descripciones = new ArrayList<>();
        List<String> fechas = new ArrayList<>();

        titulos.add("Pedido confirmado");
        descripciones.add("Tu pedido #1024 ha sido confirmado por el mesero.");
        fechas.add("Hace 5 minutos");

        titulos.add("En preparación");
        descripciones.add("Tu pedido está siendo preparado en la cocina.");
        fechas.add("Hace 2 minutos");

        titulos.add("¡Promoción especial!");
        descripciones.add("2x1 en cervezas artesanales este viernes.");
        fechas.add("Ayer");

        // Adaptador tipado correctamente
        rvNotificaciones.setAdapter(new RecyclerView.Adapter<NotificacionesFragment.ViewHolder>() {

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notificacion, parent, false);
                return new ViewHolder(v);
            }

            @Override
            public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                holder.tvTitulo.setText(titulos.get(position));
                holder.tvDesc.setText(descripciones.get(position));
                holder.tvFecha.setText(fechas.get(position));
            }

            @Override
            public int getItemCount() {
                return titulos.size();
            }
        });
    }

    // ViewHolder como clase interna estática para mejor rendimiento
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDesc, tvFecha;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tv_notificacion_titulo);
            tvDesc = itemView.findViewById(R.id.tv_notificacion_descripcion);
            tvFecha = itemView.findViewById(R.id.tv_notificacion_fecha);
        }
    }
}