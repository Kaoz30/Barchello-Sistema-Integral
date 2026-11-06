package barchello_api.repositories;

import barchello_api.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    // Al heredar de JpaRepository, Spring ya te da los métodos:
    // findAll(), findById(), save(), deleteById()
}