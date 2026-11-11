package barchello_api.repositories;

import barchello_api.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    // Útil para el futuro login o validación de duplicados
    Optional<Usuario> findByUsuarioLogin(String usuarioLogin);
}