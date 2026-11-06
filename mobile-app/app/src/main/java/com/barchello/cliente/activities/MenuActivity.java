package com.barchello.cliente.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.barchello.cliente.R;
import com.barchello.cliente.adapters.SectionedProductoAdaptador;
import com.barchello.cliente.models.Producto;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // 1. Recibir categoría
        String categoria = getIntent().getStringExtra("categoria");
        if (categoria == null) {
            categoria = "Menú";
        }

        // 2. Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Configurar Toolbar con título dinámico
        if (getSupportActionBar() != null) {
            int idMesa = getIntent().getIntExtra("idMesa", -1);
            String titulo = (idMesa != -1) ? "Menú - Mesa " + idMesa : categoria;
            getSupportActionBar().setTitle(titulo);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 3. Generar secciones (agrupadas por categoría)
        List<Map.Entry<String, List<Producto>>> secciones = generarSecciones(categoria);

        if (secciones.isEmpty()) {
            Toast.makeText(this, "No hay productos en esta categoría", Toast.LENGTH_SHORT).show();
        }

        // 4. Configurar RecyclerView con el adaptador de secciones
        RecyclerView rvProductos = findViewById(R.id.rv_productos);
        rvProductos.setLayoutManager(new LinearLayoutManager(this));

        // Si la categoría es "Todos", mostramos los headers (porque la Toolbar solo dice "Menú")
        // Si es una categoría específica, ocultamos headers (porque la Toolbar ya muestra el nombre)
        boolean mostrarHeaders = categoria.equals("Todos");
        rvProductos.setAdapter(new SectionedProductoAdaptador(this, secciones, mostrarHeaders));

        // 5. Configurar BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            // Si es el mismo ítem, no hacer nada
            if (id == R.id.navigation_home || id == R.id.navigation_escaner ||
                    id == R.id.navigation_carrito || id == R.id.navigation_notificaciones ||
                    id == R.id.navigation_perfil) {

                // Crear Intent para volver a MainActivity
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                // Limpiar pila para que no se pueda volver atrás a MenuActivity
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                // Enviar el ID del fragmento destino
                intent.putExtra("targetFragment", id);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            }
            // Si quieres que otros íconos hagan algo, agrégalos aquí
            return false;
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // ========== MÉTODO QUE GENERA SECCIONES (agrupadas por categoría) ==========
    private List<Map.Entry<String, List<Producto>>> generarSecciones(String categoria) {
        List<Map.Entry<String, List<Producto>>> secciones = new ArrayList<>();

        // Si es "Todos", mostramos TODAS las categorías como secciones separadas
        if ("Todos".equals(categoria)) {
            secciones.add(new AbstractMap.SimpleEntry<>("Para Comer", generarProductos("Para Comer")));
            secciones.add(new AbstractMap.SimpleEntry<>("Para Picar", generarProductos("Para Picar")));
            secciones.add(new AbstractMap.SimpleEntry<>("Coctelería", generarProductos("Coctelería")));
            secciones.add(new AbstractMap.SimpleEntry<>("Cervezas y Licores", generarProductos("Cervezas y Licores")));
            secciones.add(new AbstractMap.SimpleEntry<>("Bebidas", generarProductos("Bebidas")));
            secciones.add(new AbstractMap.SimpleEntry<>("Cafetería", generarProductos("Cafetería")));
        } else {
            // Si es una categoría específica, mostramos UNA sola sección
            List<Producto> productos = generarProductos(categoria);
            if (!productos.isEmpty()) {
                secciones.add(new AbstractMap.SimpleEntry<>(categoria, productos));
            }
        }
        return secciones;
    }

    // ========== MÉTODO QUE GENERA PRODUCTOS DE UNA CATEGORÍA ESPECÍFICA ==========
    private List<Producto> generarProductos(String categoria) {
        List<Producto> lista = new ArrayList<>();

        switch (categoria) {
            case "Para Comer":
                lista.add(new Producto("Hamburguesa Clásica", "Carne 200g, queso, lechuga y tomate", 18500, R.drawable.hamburguesa_artesanal));
                lista.add(new Producto("Pizza Margarita", "Mozzarella, albahaca y salsa de tomate", 22000, R.drawable.pizza_artesanal));
                lista.add(new Producto("Pasta Alfredo", "Fettuccine con crema de parmesano", 21000, R.drawable.pasta_alfredo));
                lista.add(new Producto("Salmón a la Plancha", "Con vegetales salteados", 28000, R.drawable.salmon_a_la_plancha));
                break;

            case "Para Picar":
                lista.add(new Producto("Alitas BBQ", "6 piezas con salsa barbacoa", 18000, R.drawable.alitas_bbq));
                lista.add(new Producto("Nachos con Queso", "Tortilla chips con queso derretido", 15000, R.drawable.nachos_con_queso));
                lista.add(new Producto("Dedos de Queso", "Queso mozzarella empanizado", 12000, R.drawable.dedos_de_queso));
                lista.add(new Producto("Papas Fritas", "Con salsa de la casa", 8000, R.drawable.papas_cheddar));
                break;

            case "Coctelería":
                lista.add(new Producto("Mojito", "Ron blanco, hierbabuena, lima y soda", 18000, R.drawable.mojito));
                lista.add(new Producto("Margarita", "Tequila, triple sec y jugo de limón", 20000, R.drawable.margarita));
                lista.add(new Producto("Piña Colada", "Ron, crema de coco y piña", 19000, R.drawable.pina_colada));
                lista.add(new Producto("Old Fashioned", "Whisky, amargo de Angostura y azúcar", 25000, R.drawable.old_fashioned));
                break;

            case "Cervezas y Licores":
                lista.add(new Producto("Cerveza Artesanal IPA", "Botella 500ml", 12000, R.drawable.cerveza_ipa));
                lista.add(new Producto("Cerveza Rubia", "Botella 500ml", 9000, R.drawable.cerveza_rubia));
                lista.add(new Producto("Whisky 12 años", "Botella 750ml", 35000, R.drawable.whisky));
                lista.add(new Producto("Ron Añejo", "Botella 750ml", 28000, R.drawable.ron_anejo));
                break;

            case "Bebidas":
                lista.add(new Producto("Limonada Natural", "Con hierbabuena", 7000, R.drawable.limonada));
                lista.add(new Producto("Jugo de Naranja", "Natural", 6000, R.drawable.jugo_natural));
                lista.add(new Producto("Agua con Gas", "Botella 500ml", 4000, R.drawable.agua_con_gas));
                lista.add(new Producto("Malteada de Vainilla", "Con crema batida", 10000, R.drawable.malteada_de_vainilla));
                break;

            case "Cafetería":
                lista.add(new Producto("Café Americano", "Tinto negro", 5000, R.drawable.cafe_americano));
                lista.add(new Producto("Capuchino", "Con leche vaporizada", 7000, R.drawable.capuchino));
                lista.add(new Producto("Mocaccino", "Café con chocolate y leche", 8000, R.drawable.mocaccino));
                lista.add(new Producto("Té Chai", "Con especias", 6000, R.drawable.te_chai));
                break;

            default:
                // Si no coincide, devolvemos lista vacía
                break;
        }
        return lista;
    }
}