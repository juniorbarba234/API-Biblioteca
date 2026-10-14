package com.api.biblioteca.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import com.api.biblioteca.models.UsuarioModel;

public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from UsuarioModel u where u.id = :id")
    Optional<UsuarioModel> buscarParaActualizar(@Param("id") Long id);
}
