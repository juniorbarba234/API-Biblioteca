package com.api.biblioteca.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.api.biblioteca.models.PrestamoModel;
import com.api.biblioteca.services.PrestamoService;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {
    private final PrestamoService service;
    public PrestamoController(PrestamoService service) { this.service = service; }

    @GetMapping
    public List<PrestamoModel> listar() { return service.listar(); }

    @GetMapping("/activos")
    public List<PrestamoModel> activos() { return service.activos(); }

    @GetMapping("/usuario/{id}")
    public List<PrestamoModel> porUsuario(@PathVariable Long id) { return service.porUsuario(id); }

    @GetMapping("/{id}")
    public PrestamoModel obtener(@PathVariable Long id) { return service.obtener(id); }

    @PostMapping
    public ResponseEntity<PrestamoModel> crear(@RequestBody PrestamoModel prestamo) {
        return ResponseEntity.status(201).body(service.crear(prestamo));
    }

    @PostMapping("/{id}/devolver")
    public PrestamoModel devolver(@PathVariable Long id) { return service.devolver(id); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
