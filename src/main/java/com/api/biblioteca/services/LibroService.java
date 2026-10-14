package com.api.biblioteca.services;

import java.time.Year;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.api.biblioteca.models.LibroModel;
import com.api.biblioteca.repositories.*;

@Service
public class LibroService {
    private final LibroRepository libros;
    private final PrestamoRepository prestamos;

    public LibroService(LibroRepository libros, PrestamoRepository prestamos) {
        this.libros = libros;
        this.prestamos = prestamos;
    }

    public List<LibroModel> listar() {
        List<LibroModel> lista = libros.findAll();
        lista.forEach(this::calcularDisponibles);
        return lista;
    }

    public LibroModel obtener(String isbn) {
        LibroModel libro = libros.findById(normalizarIsbn(isbn))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El libro no existe"));
        calcularDisponibles(libro);
        return libro;
    }

    @Transactional
    public LibroModel crear(LibroModel libro) {
        libro.setIsbn(normalizarIsbn(libro.getIsbn()));
        validarAnio(libro);
        if (libros.existsById(libro.getIsbn())) throw new ResponseStatusException(HttpStatus.CONFLICT, "El ISBN ya está registrado");
        LibroModel guardado = libros.saveAndFlush(libro);
        calcularDisponibles(guardado);
        return guardado;
    }

    @Transactional
    public LibroModel actualizar(String isbn, LibroModel datos) {
        LibroModel libro = libros.buscarParaActualizar(normalizarIsbn(isbn))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El libro no existe"));
        validarAnio(datos);
        long activos = prestamos.countByLibro_IsbnAndFechaDevolucionIsNull(libro.getIsbn());
        if (datos.getNumeroEjemplares() < activos) throw new ResponseStatusException(HttpStatus.CONFLICT, "El total no puede ser menor que los ejemplares prestados");
        libro.setTitulo(datos.getTitulo());
        libro.setAutor(datos.getAutor());
        libro.setAnioPublicacion(datos.getAnioPublicacion());
        libro.setNumeroEjemplares(datos.getNumeroEjemplares());
        LibroModel guardado = libros.saveAndFlush(libro);
        calcularDisponibles(guardado);
        return guardado;
    }

    @Transactional
    public void eliminar(String isbn) {
        LibroModel libro = libros.buscarParaActualizar(normalizarIsbn(isbn))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El libro no existe"));
        if (prestamos.existsByLibro_Isbn(libro.getIsbn())) throw new ResponseStatusException(HttpStatus.CONFLICT, "El libro tiene préstamos registrados");
        libros.delete(libro);
    }

    public void calcularDisponibles(LibroModel libro) {
        long activos = prestamos.countByLibro_IsbnAndFechaDevolucionIsNull(libro.getIsbn());
        libro.setEjemplaresDisponibles(libro.getNumeroEjemplares() - (int) activos);
    }

    public static String normalizarIsbn(String isbn) {
        if (isbn == null) throw new IllegalArgumentException("El ISBN es obligatorio");
        String limpio = isbn.replace("-", "").replace(" ", "").toUpperCase();
        if (!limpio.matches("[0-9]{13}|[0-9]{9}[0-9X]")) throw new IllegalArgumentException("El ISBN debe tener 10 o 13 caracteres válidos");
        return limpio;
    }

    private void validarAnio(LibroModel libro) {
        if (libro.getAnioPublicacion() > Year.now().getValue()) throw new IllegalArgumentException("El año de publicación no puede ser futuro");
    }
}
