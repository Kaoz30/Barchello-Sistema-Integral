package com.barchello.cliente.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.barchello.cliente.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DetalleProductoActivity extends AppCompatActivity {

    private TextView tvCantidad;
    private int cantidad = 1;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_producto);

        // 1: CONFIGURAR LA TOOLBAR (BARRA SUPERIOR)
        // 1. Buscar la Toolbar en el layout
        Toolbar toolbar = findViewById(R.id.toolbar);
        // 2. Decirle a la actividad que use esa Toolbar como ActionBar
        setSupportActionBar(toolbar);
        // 3. Si se asignó correctamente, configurar el botón "atrás" y el título
        if (getSupportActionBar() != null) {
            // Muestra la flecha de retroceso
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // Cambia el título de la barra (en lugar de "DetalleProductoActivity")
            getSupportActionBar().setTitle("Detalle");
        }

        // 1. Recibir datos
        String nombre = getIntent().getStringExtra("nombre");
        String descripcion = getIntent().getStringExtra("descripcion");
        double precio = getIntent().getDoubleExtra("precio", 0);
        int imagenResId = getIntent().getIntExtra("imagen", R.drawable.placeholder_food);

        // 2. Enlazar vistas
        ImageView ivImagen = findViewById(R.id.iv_producto);
        TextView tvNombre = findViewById(R.id.tv_nombre_producto);
        TextView tvDescripcion = findViewById(R.id.tv_descripcion);
        TextView tvPrecio = findViewById(R.id.tv_precio);
        tvCantidad = findViewById(R.id.tv_cantidad);
        Button btnMenos = findViewById(R.id.btn_menos);
        Button btnMas = findViewById(R.id.btn_mas);
        Button btnAgregar = findViewById(R.id.btn_agregar);

        // 3. Mostrar datos
        ivImagen.setImageResource(imagenResId);
        tvNombre.setText(nombre);
        tvDescripcion.setText(descripcion);
        tvPrecio.setText("$" + String.format("%,.0f", precio));
        tvCantidad.setText(String.valueOf(cantidad));

        // 4. Selector de cantidad (- / +)
        btnMenos.setOnClickListener(v -> {
            if (cantidad > 1) {
                cantidad--;
                tvCantidad.setText(String.valueOf(cantidad));
            }
        });

        btnMas.setOnClickListener(v -> {
            cantidad++;
            tvCantidad.setText(String.valueOf(cantidad));
        });

        // 5. Botón "Agregar al carrito" (simulado)
        btnAgregar.setOnClickListener(v -> {
            Toast.makeText(this, "✅ " + cantidad + " x " + nombre + " agregado al carrito",
                    Toast.LENGTH_LONG).show();
            // Aquí después se conectará con el carrito real
        });

        // 6. Configurar BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.navigation_home || id == R.id.navigation_escaner ||
                id == R.id.navigation_carrito || id == R.id.navigation_notificaciones ||
                id == R.id.navigation_perfil) {

                Intent intent = new Intent(DetalleProductoActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("targetFragment", id);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            }
            return false;
        });

    }

    //  2: MÉTODO PARA LA FLECHA DE RETROCESO
    // ESTE MÉTODO VA FUERA DE onCreate, PERO DENTRO DE LA CLASE
    @Override
    public boolean onSupportNavigateUp() {
        // finish() cierra la actividad actual y vuelve a la anterior (MenuActivity)
        finish();
        return true;
    }
}