package com.api.biblioteca.models;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "prestamos")
public class PrestamoModel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "isbn", nullable = false)
    private LibroModel libro;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private UsuarioModel usuario;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(nullable = false, updatable = false)
    private LocalDate fechaPrestamo;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate fechaDevolucion;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LibroModel getLibro() { return libro; }
    public void setLibro(LibroModel libro) { this.libro = libro; }
    public UsuarioModel getUsuario() { return usuario; }
    public void setUsuario(UsuarioModel usuario) { this.usuario = usuario; }
    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public void setFechaPrestamo(LocalDate fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }
    public LocalDate getFechaDevolucion() { return fechaDevolucion; }
    public void setFechaDevolucion(LocalDate fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }
}
