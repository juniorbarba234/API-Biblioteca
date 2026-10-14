package com.api.biblioteca.controllers;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.api.biblioteca.models.LibroModel;
import com.api.biblioteca.services.LibroService;

@RestController
@RequestMapping("/api/libros")
public class LibroController {
    private final LibroService service;
    public LibroController(LibroService service) { this.service = service; }

    @GetMapping
    public List<LibroModel> listar() { return service.listar(); }

    @GetMapping("/{isbn}")
    public LibroModel obtener(@PathVariable String isbn) { return service.obtener(isbn); }

    @PostMapping
    public ResponseEntity<LibroModel> crear(@Valid @RequestBody LibroModel libro) {
        return ResponseEntity.status(201).body(service.crear(libro));
    }

    @PutMapping("/{isbn}")
    public LibroModel actualizar(@PathVariable String isbn, @Valid @RequestBody LibroModel libro) {
        return service.actualizar(isbn, libro);
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<Void> eliminar(@PathVariable String isbn) {
        service.eliminar(isbn);
        return ResponseEntity.noContent().build();
    }
}
