package barchello_api.controllers;

import barchello_api.models.Categoria;
import barchello_api.repositories.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*")
public class CategoriaController {

    @Autowired
    private CategoriaRepository categoriaRepository;

    // 1. Listar todas las categorías
    @GetMapping
    public List<Categoria> obtenerTodas() {
        return categoriaRepository.findAll();
    }

    // 2. Crear una categoría (con validación de nombre duplicado)
    @PostMapping
    public ResponseEntity<?> crearCategoria(@RequestBody Categoria categoria) {
        // Validación: si el nombre ya existe, devolvemos un error 400
        if (categoriaRepository.findByNombre(categoria.getNombre()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: La categoría ya existe.");
        }
        Categoria nueva = categoriaRepository.save(categoria);
        return ResponseEntity.ok(nueva);
    }

    // 3. Eliminar una categoría (con validación de integridad)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable Integer id) {
        // Nota: La validación de "no eliminar si tiene productos" la hará la BD
        // por la llave foránea (RESTRICT). Aquí solo capturamos el error.
        try {
            categoriaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: No se puede eliminar la categoría porque tiene productos asociados.");
        }
    }
}