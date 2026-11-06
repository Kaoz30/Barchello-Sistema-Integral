package com.barchello.cliente.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.barchello.cliente.R;

import java.util.List;

public class CategoriaAdaptador extends RecyclerView.Adapter<CategoriaAdaptador.ViewHolder> {

    // 1. Ahora guardamos DOS listas: nombres e iconos
    private final List<String> categorias;
    private final List<Integer> iconos;
    private final OnCategoriaClickListener listener;

    public interface OnCategoriaClickListener {
        void onCategoriaClick(String categoria);
    }

    // 2. CONSTRUCTOR MODIFICADO: recibe 3 parámetros
    public CategoriaAdaptador(List<String> categorias, List<Integer> iconos, OnCategoriaClickListener listener) {
        this.categorias = categorias;
        this.iconos = iconos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_categoria, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String categoria = categorias.get(position);

        // 3. ASIGNAR ICONO CORRESPONDIENTE (en lugar de ic_home fijo)
        //    IMPORTANTE: usamos iconos.get(position) para obtener el drawable correcto
        holder.ivIcono.setImageResource(iconos.get(position));

        holder.tvNombre.setText(categoria);

        holder.itemView.setOnClickListener(v -> {
            //  Toast para depuración
            Toast.makeText(v.getContext(), "Click en: " + categoria, Toast.LENGTH_SHORT).show();

            if (listener != null) {
                listener.onCategoriaClick(categoria);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }

    // Ahora es prívate static (mejor encapsulamiento)
     public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcono;
        TextView tvNombre;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcono = itemView.findViewById(R.id.iv_categoria_icon);
            tvNombre = itemView.findViewById(R.id.tv_categoria_nombre);
        }
    }
}