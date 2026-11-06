package com.barchello.cliente.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;

import com.barchello.cliente.R;
import com.barchello.cliente.activities.DetalleProductoActivity;
import com.barchello.cliente.models.Producto;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Locale;

public class ProductoAdaptador extends RecyclerView.Adapter<ProductoAdaptador.ViewHolder> {

    private final List<Producto> productos;
    private final Context context;

    public ProductoAdaptador(Context context, List<Producto> productos) {
        this.context = context;
        this.productos = productos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_producto, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producto producto = productos.get(position);

        // Asignar datos
        holder.tvNombre.setText(producto.getNombre());
        holder.tvDescripcion.setText(producto.getDescripcion());

        // ✅ CORREGIDO: Usar Locale.getDefault() para evitar warnings
        holder.tvPrecio.setText("$" + String.format(Locale.getDefault(), "%,.0f", producto.getPrecio()));
        holder.ivImagen.setImageResource(producto.getImagenResId());

        // 1. CLIC EN TARJETA → Abrir Detalle (pasa todos los datos)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalleProductoActivity.class);
            intent.putExtra("nombre", producto.getNombre());
            intent.putExtra("descripcion", producto.getDescripcion());
            intent.putExtra("precio", producto.getPrecio());
            intent.putExtra("imagen", producto.getImagenResId());
            context.startActivity(intent);
        });

        // 2. BOTÓN "+" → Simular agregar al carrito (sin backend)
        holder.btnAgregar.setOnClickListener(v -> {
            String mensaje = producto.getNombre() + " agregado al carrito";
            Snackbar snackbar = Snackbar.make(v, "✅ " + mensaje, Snackbar.LENGTH_SHORT);

            // Personalizar el fondo del Snackbar
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.cuero));

            // Personalizar el texto
            TextView tvSnackbar = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
            if (tvSnackbar != null) {
                tvSnackbar.setTextColor(ContextCompat.getColor(context, R.color.blanco));
                tvSnackbar.setTextSize(14);
            }

            // Personalizar la acción "Ver carrito"
            snackbar.setAction("Ver carrito", view -> {
                // Aquí después navegarás al fragment Carrito
                // Por ahora solo mostramos otro mensaje
                Snackbar.make(v, "Navegando al carrito (próximamente)", Snackbar.LENGTH_SHORT)
                        .show();
            }).setActionTextColor(ContextCompat.getColor(context,R.color.roble_disabled));

            snackbar.show();

        });
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImagen;
        TextView tvNombre, tvDescripcion, tvPrecio;
        Button btnAgregar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImagen = itemView.findViewById(R.id.iv_producto_imagen);
            tvNombre = itemView.findViewById(R.id.tv_producto_nombre);
            tvDescripcion = itemView.findViewById(R.id.tv_producto_descripcion);
            tvPrecio = itemView.findViewById(R.id.tv_producto_precio);
            btnAgregar = itemView.findViewById(R.id.btn_agregar_item);
        }
    }
}