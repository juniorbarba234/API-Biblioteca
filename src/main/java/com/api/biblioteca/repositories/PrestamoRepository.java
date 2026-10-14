package com.api.biblioteca.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import com.api.biblioteca.models.PrestamoModel;

public interface PrestamoRepository extends JpaRepository<PrestamoModel, Long> {
    long countByLibro_IsbnAndFechaDevolucionIsNull(String isbn);
    boolean existsByLibro_Isbn(String isbn);
    boolean existsByUsuario_Id(Long id);
    boolean existsByLibro_IsbnAndUsuario_IdAndFechaDevolucionIsNull(String isbn, Long id);
    List<PrestamoModel> findByFechaDevolucionIsNull();
    List<PrestamoModel> findByUsuario_Id(Long id);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PrestamoModel p where p.id = :id")
    java.util.Optional<PrestamoModel> buscarParaActualizar(@Param("id") Long id);
}
