package com.api.biblioteca.services;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.api.biblioteca.models.*;
import com.api.biblioteca.repositories.*;

@Service
public class PrestamoService {
    private final PrestamoRepository prestamos;
    private final LibroRepository libros;
    private final UsuarioRepository usuarios;
    private final LibroService libroService;

    public PrestamoService(PrestamoRepository prestamos, LibroRepository libros, UsuarioRepository usuarios, LibroService libroService) {
        this.prestamos = prestamos;
        this.libros = libros;
        this.usuarios = usuarios;
        this.libroService = libroService;
    }

    public List<PrestamoModel> listar() {
        return completar(prestamos.findAll());
    }

    public List<PrestamoModel> activos() {
        return completar(prestamos.findByFechaDevolucionIsNull());
    }

    public List<PrestamoModel> porUsuario(Long id) {
        if (!usuarios.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no existe");
        return completar(prestamos.findByUsuario_Id(id));
    }

    public PrestamoModel obtener(Long id) {
        PrestamoModel prestamo = prestamos.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El préstamo no existe"));
        libroService.calcularDisponibles(prestamo.getLibro());
        return prestamo;
    }

    @Transactional
    public PrestamoModel crear(PrestamoModel datos) {
        if (datos.getLibro() == null || datos.getLibro().getIsbn() == null) throw new IllegalArgumentException("El libro es obligatorio");
        if (datos.getUsuario() == null || datos.getUsuario().getId() == null) throw new IllegalArgumentException("El usuario es obligatorio");
        LibroModel libro = libros.buscarParaActualizar(LibroService.normalizarIsbn(datos.getLibro().getIsbn()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El libro no existe"));
        UsuarioModel usuario = usuarios.buscarParaActualizar(datos.getUsuario().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no existe"));
        if (usuario.getEstado() != UsuarioModel.Estado.ACTIVO) throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario no está activo");
        if (prestamos.existsByLibro_IsbnAndUsuario_IdAndFechaDevolucionIsNull(libro.getIsbn(), usuario.getId())) throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario ya tiene un préstamo activo de este libro");
        long activos = prestamos.countByLibro_IsbnAndFechaDevolucionIsNull(libro.getIsbn());
        if (activos >= libro.getNumeroEjemplares()) throw new ResponseStatusException(HttpStatus.CONFLICT, "No hay ejemplares disponibles");
        PrestamoModel prestamo = new PrestamoModel();
        prestamo.setLibro(libro);
        prestamo.setUsuario(usuario);
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaDevolucion(null);
        prestamos.saveAndFlush(prestamo);
        libroService.calcularDisponibles(libro);
        return prestamo;
    }

    @Transactional
    public PrestamoModel devolver(Long id) {
        PrestamoModel prestamo = prestamos.buscarParaActualizar(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El préstamo no existe"));
        if (prestamo.getFechaDevolucion() != null) throw new ResponseStatusException(HttpStatus.CONFLICT, "El préstamo ya fue devuelto");
        prestamo.setFechaDevolucion(LocalDate.now());
        prestamos.saveAndFlush(prestamo);
        libroService.calcularDisponibles(prestamo.getLibro());
        return prestamo;
    }

    @Transactional
    public void eliminar(Long id) {
        PrestamoModel prestamo = prestamos.buscarParaActualizar(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El préstamo no existe"));
        if (prestamo.getFechaDevolucion() == null) throw new ResponseStatusException(HttpStatus.CONFLICT, "Primero debes devolver el libro");
        prestamos.delete(prestamo);
    }

    private List<PrestamoModel> completar(List<PrestamoModel> lista) {
        lista.forEach(prestamo -> libroService.calcularDisponibles(prestamo.getLibro()));
        return lista;
    }
}
