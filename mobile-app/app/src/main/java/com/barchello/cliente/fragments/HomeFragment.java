package com.barchello.cliente.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import androidx.recyclerview.widget.PagerSnapHelper;

import com.barchello.cliente.R;
import com.barchello.cliente.activities.MenuActivity;
import com.barchello.cliente.adapters.ProductoAdaptador;  // <-- IMPORTADO
import com.barchello.cliente.models.Producto;

import java.util.Arrays;
//import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ======================== 1. BANNER PROMOCIONAL ========================
        ViewPager2 banner = view.findViewById(R.id.bannerViewPager);
        // Lista de imágenes (IDs de drawable)
        List<Integer> bannerImagenes = Arrays.asList(
                R.drawable.banner_happy_hour,
                R.drawable.banner_promo_comida,
                R.drawable.banner_evento
        );

        // Lista de textos correspondientes (mismo orden)
        List<String> bannerTextos = Arrays.asList(
                "🔥 Happy Hour 2x1 en cervezas",
                "🍔 Hamburguesa + Cerveza por $25.000",
                "🎵 Evento en vivo este viernes"
        );

        // Crear adaptador con ambas listas
        banner.setAdapter(new BannerAdapter(bannerImagenes, bannerTextos));
        // Iniciar auto-scroll
        autoScrollBanner(banner);  // <-- Llama al método (definido abajo)

        // Después de banner.setAdapter(...)
        LinearLayout indicators = view.findViewById(R.id.ll_banner_indicators);

        // Crear puntos dinámicamente
        for (int i = 0; i < bannerImagenes.size(); i++) {
            View dot = new View(getContext());
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(10, 10);

            params.setMargins(6, 0, 6, 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(R.drawable.ic_dot_inactive);
            indicators.addView(dot);
        }

        // Actualizar el punto activo cuando cambia el banner
        banner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < indicators.getChildCount(); i++) {
                    View dot = indicators.getChildAt(i);
                    if (i == position) {
                        dot.setBackgroundResource(R.drawable.ic_dot_active);
                    } else {
                        dot.setBackgroundResource(R.drawable.ic_dot_inactive);
                    }
                }
                // Mostrar indicadores solo cuando se interactúa (se puede hacer con un timeout)
                indicators.setVisibility(View.VISIBLE);
                // Ocultar después de 2 segundos de inactividad
                indicators.postDelayed(() -> indicators.setVisibility(View.INVISIBLE), 2000);
            }
        });

        // Ocultar indicadores inicialmente
        indicators.setVisibility(View.VISIBLE);

        // ======================== 2. CATEGORÍAS (6 en grid de 3) ========================
        RecyclerView rvCategorias = view.findViewById(R.id.rv_categorias);
        // Cambiar a 3 columnas (para que quepan textos largos como "Cervezas y Licores")
        rvCategorias.setLayoutManager(new GridLayoutManager(getContext(), 3));

        // Lista de 6 categorías según tu especificación
        List<String> categorias = Arrays.asList(
                "Para Comer",
                "Cervezas y Licores",
                "Bebidas",
                "Para Picar",
                "Coctelería",
                "Cafetería"
        );

        // Lista de 6 iconos (MISMO ORDEN que los nombres)
        List<Integer> iconos = Arrays.asList(
                R.drawable.ic_food,      // Para "Para Comer"
                R.drawable.ic_beer,      // Para "Cervezas y Licores"
                R.drawable.ic_drink,     // Para "Bebidas"
                R.drawable.ic_snack,     // Para "Para Picar"
                R.drawable.ic_cocktail,  // Para "Coctelería"
                R.drawable.ic_coffee     // Para "Cafetería"
        );

        // CREAR ADAPTADOR CON LOS 3 PARÁMETROS: categorias, iconos, listener
        CategoriaAdaptador adaptador = new CategoriaAdaptador(categorias, iconos, categoria -> {
            // Al hacer clic en una categoría, abrir MenuActivity con el nombre
            Intent intent = new Intent(getActivity(), MenuActivity.class);
            intent.putExtra("categoria", categoria);
            startActivity(intent);
        });
        rvCategorias.setAdapter(adaptador);

        // ======= 3. PRODUCTOS DESTACADOS =======
        // 1. Crear lista de datos (PRIMERO)
        List<Producto> productosDestacados = Arrays.asList(
                new Producto("Hamburguesa Artesanal", "Jugosa carne 200g, queso cheddar derretido, lechuga fresca, tomate y salsa especial de la casa.", 18500.0, R.drawable.hamburguesa_artesanal),
                new Producto("Cerveza IPA", "Cerveza artesanal 500ml con notas cítricas y amargor equilibrado. Ideal para acompañar carnes.", 12000.0, R.drawable.cerveza_ipa),
                new Producto("Alitas BBQ", "10 unidades bañadas en salsa BBQ ahumada, acompañadas de aderezo ranch y bastones de apio.", 22000.0, R.drawable.alitas_bbq),
                new Producto("Mojito", "Ron blanco, hierbabuena fresca, lima, azúcar de caña y soda. Refrescante y clásico.", 15000.0, R.drawable.mojito)
        );

        // 2. Configurar RecyclerView (SIN datos nulos, SIN adaptador duplicado)
        RecyclerView rvDestacados = view.findViewById(R.id.rv_destacados);
        rvDestacados.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        rvDestacados.setAdapter(new ProductoAdaptador(getContext(), productosDestacados));

        // 3. Aplicar PagerSnapHelper (para que avance UNO A UNO, como ViewPager)
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rvDestacados);


        // <-- USANDO ProductoAdaptador DEL PAQUETE adapters (correcto)
        rvDestacados.setAdapter(new ProductoAdaptador(getContext(), productosDestacados));

        // ======================== 4. BOTÓN VER MENÚ COMPLETO ========================
        Button btnVerMenuCompleto = view.findViewById(R.id.btn_ver_menu_completo);
        btnVerMenuCompleto.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MenuActivity.class);
            intent.putExtra("categoria", "Todos");  // ← Enviamos "Todos"
            startActivity(intent);
        });

    }  // <-- AQUÍ CIERRA onViewCreated

    // ======================== MÉTODO AUTO-SCROLL PARA EL BANNER (FUERA de onViewCreated) ====================
    private void autoScrollBanner(ViewPager2 viewPager) {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (viewPager.getAdapter() == null) return;
                int total = viewPager.getAdapter().getItemCount();
                if (total <= 1) return;
                int current = viewPager.getCurrentItem();
                int next = (current + 1) % total;
                viewPager.setCurrentItem(next, true); // true = animación suave
                handler.postDelayed(this, 5000); // Cambia cada 5 segundos
            }
        };
        // Inicia después de 5 segundos (para que el usuario vea el primer slide)
        handler.postDelayed(runnable, 6000);

    }

    // ======================== CLASE INTERNA BANNERADAPTER (solo para el banner) ========================
    private static class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
        private final List<Integer> imagenes; // <-- SIN inicializar, SIN final (opcional, pero la asignación se hará en constructor)
        private final List<String> textos;

        // Constructor CORRECTO: recibe ambas listas
        BannerAdapter(List<Integer> imagenes, List<String> textos) {
            this.imagenes = imagenes;
            this.textos = textos;
        }

        @NonNull
        @Override
        public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_banner, parent, false);
            return new BannerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
            // Ahora imagenes NO es vacía, tiene los ID correctos
            holder.ivImagen.setImageResource(imagenes.get(position));
            holder.tvTexto.setText(textos.get(position));
        }

        @Override
        public int getItemCount() {
            return imagenes.size(); // ambas listas tienen el mismo tamaño
        }

        static class BannerViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImagen;
            TextView tvTexto;

            BannerViewHolder(@NonNull View itemView) {
                super(itemView);
                ivImagen = itemView.findViewById(R.id.iv_banner_imagen);
                tvTexto = itemView.findViewById(R.id.tv_banner_texto);
            }
        }
    }
}   // <-- FIN DE LA CLASE HomeFragment

