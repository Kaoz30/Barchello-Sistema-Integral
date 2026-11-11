package barchello_api.repositories;

import barchello_api.models.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    // Método para buscar por nombre (útil para validar duplicados)
    Optional<Categoria> findByNombre(String nombre);
}