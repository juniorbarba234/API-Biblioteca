package com.api.biblioteca.controllers;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.api.biblioteca.models.UsuarioModel;
import com.api.biblioteca.services.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService service;
    public UsuarioController(UsuarioService service) { this.service = service; }

    @GetMapping
    public List<UsuarioModel> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public UsuarioModel obtener(@PathVariable Long id) { return service.obtener(id); }

    @PostMapping
    public ResponseEntity<UsuarioModel> crear(@Valid @RequestBody UsuarioModel usuario) {
        return ResponseEntity.status(201).body(service.crear(usuario));
    }

    @PutMapping("/{id}")
    public UsuarioModel actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioModel usuario) {
        return service.actualizar(id, usuario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
