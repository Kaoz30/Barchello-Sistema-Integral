package barchello_api.repositories;

import barchello_api.models.Categoria;
import barchello_api.models.Producto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional; // IMPORTANTE

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Transactional // MAGIA: Esto hace que al terminar el test, la BD vuelva a como estaba
public class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    public void testGuardarYListarProductos() {
        // 1. Crear Categoría con un nombre único usando el tiempo actual
        Categoria categoriaPrueba = new Categoria();
        categoriaPrueba.setNombre("Cat Test " + System.currentTimeMillis());
        categoriaPrueba.setDescripcion("Solo para probar JUnit");
        categoriaPrueba = categoriaRepository.save(categoriaPrueba);

        // 2. Crear Producto
        Producto p = new Producto();
        p.setNombre("Prod Test " + System.currentTimeMillis());
        p.setPrecioVenta(new BigDecimal("15000"));
        p.setEstado("activo");
        p.setEsBebida(false);
        p.setCategoria(categoriaPrueba);

        // 3. Guardar en BD
        productoRepository.save(p);

        // 4. Consultar
        List<Producto> productos = productoRepository.findAll();
        assertFalse(productos.isEmpty(), "La lista de productos no debería estar vacía");
    }
}