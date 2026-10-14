package com.api.biblioteca.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import com.api.biblioteca.models.LibroModel;

public interface LibroRepository extends JpaRepository<LibroModel, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from LibroModel l where l.isbn = :isbn")
    Optional<LibroModel> buscarParaActualizar(@Param("isbn") String isbn);
}
