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
import java.util.Map;

public class SectionedProductoAdaptador extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Tipos de vista: 0 = header (encabezado), 1 = item (producto)
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private final Context context;
    private final List<Map.Entry<String, List<Producto>>> secciones;
    private final boolean mostrarHeaders; // ← controla si se muestran los encabezados

    // Constructor modificado: recibe el flag
    public SectionedProductoAdaptador(Context context, List<Map.Entry<String, List<Producto>>> secciones, boolean mostrarHeaders) {
        this.context = context;
        this.secciones = secciones;
        this.mostrarHeaders = mostrarHeaders;
    }

    @Override
    public int getItemViewType(int position) {
        // Si NO se muestran headers, todo es ITEM
        if (!mostrarHeaders) {
            return TYPE_ITEM;
        }

        // Si se muestran headers, calculamos si es header o item
        int acumulado = 0;
        for (Map.Entry<String, List<Producto>> seccion : secciones) {
            // El header ocupa 1 posición
            if (position == acumulado) {
                return TYPE_HEADER;
            }
            acumulado++; // saltamos el header
            // Los items de esta sección
            if (position < acumulado + seccion.getValue().size()) {
                return TYPE_ITEM;
            }
            acumulado += seccion.getValue().size();
        }
        return TYPE_ITEM; // fallback
    }

    @Override
    public int getItemCount() {
        int total = 0;
        for (Map.Entry<String, List<Producto>> seccion : secciones) {
            if (mostrarHeaders) {
                total += 1; // header
            }
            total += seccion.getValue().size(); // items
        }
        return total;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER && mostrarHeaders) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_menu_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_producto, parent, false);
            return new ProductoViewHolder(view);
        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            String nombreCategoria = obtenerCategoriaEnPosicion(position);
            ((HeaderViewHolder) holder).tvHeader.setText(nombreCategoria);
        } else if (holder instanceof ProductoViewHolder) {
            Producto producto = obtenerProductoEnPosicion(position);
            ProductoViewHolder vh = (ProductoViewHolder) holder;
            assert producto != null;
            vh.tvNombre.setText(producto.getNombre());
            vh.tvDescripcion.setText(producto.getDescripcion());
            vh.tvPrecio.setText("$" + String.format("%,.0f", producto.getPrecio()));
            vh.ivImagen.setImageResource(producto.getImagenResId());

            vh.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, DetalleProductoActivity.class);
                intent.putExtra("nombre", producto.getNombre());
                intent.putExtra("descripcion", producto.getDescripcion());
                intent.putExtra("precio", producto.getPrecio());
                intent.putExtra("imagen", producto.getImagenResId());
                context.startActivity(intent);
            });

            vh.btnAgregar.setOnClickListener(v -> {
                String mensaje = producto.getNombre() + " agregado al carrito";
                Snackbar snackbar = Snackbar.make(v, "✅ " + mensaje, Snackbar.LENGTH_SHORT);
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.cuero));
                TextView tvSnackbar = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
                if (tvSnackbar != null) {
                    tvSnackbar.setTextColor(ContextCompat.getColor(context, R.color.blanco));
                    tvSnackbar.setTextSize(14);
                }
                snackbar.setAction("Ver carrito", view -> Snackbar.make(v, "Navegando al carrito (próximamente)",
                        Snackbar.LENGTH_SHORT).show()).setActionTextColor
                        (ContextCompat.getColor(context, R.color.roble));
                snackbar.show();
            });
        }
    }

    // Métodos auxiliares (sin cambios)
    private String obtenerCategoriaEnPosicion(int position) {
        int acumulado = 0;
        for (Map.Entry<String, List<Producto>> seccion : secciones) {
            if (position == acumulado) {
                return seccion.getKey();
            }
            // 🔥 SOLO sumar 1 si se muestran los headers
            if (mostrarHeaders) {
                acumulado++; // header
            }
            acumulado += seccion.getValue().size();
        }
        return "";
    }

    private Producto obtenerProductoEnPosicion(int position) {
        int acumulado = 0;
        for (Map.Entry<String, List<Producto>> seccion : secciones) {
            // 🔥 SOLO sumar 1 si se muestran los headers
            if (mostrarHeaders) {
                acumulado++; // saltamos el header
            }
            if (position < acumulado + seccion.getValue().size()) {
                int index = position - acumulado;
                return seccion.getValue().get(index);
            }
            acumulado += seccion.getValue().size();
        }
        return null;
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;
        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tv_menu_header);
        }
    }

    static class ProductoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImagen;
        TextView tvNombre, tvDescripcion, tvPrecio;
        Button btnAgregar;
        ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImagen = itemView.findViewById(R.id.iv_producto_imagen);
            tvNombre = itemView.findViewById(R.id.tv_producto_nombre);
            tvDescripcion = itemView.findViewById(R.id.tv_producto_descripcion);
            tvPrecio = itemView.findViewById(R.id.tv_producto_precio);
            btnAgregar = itemView.findViewById(R.id.btn_agregar_item);
        }
    }
}