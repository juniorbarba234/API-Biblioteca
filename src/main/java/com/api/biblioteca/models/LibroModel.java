package com.api.biblioteca.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "libros")
public class LibroModel {
    @Id
    @NotBlank
    @Column(length = 13)
    private String isbn;

    @NotBlank @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String titulo;

    @NotBlank @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String autor;

    @Min(1)
    @Column(nullable = false)
    private int anioPublicacion;

    @Min(0)
    @Column(nullable = false)
    private int numeroEjemplares;

    @Transient
    private int ejemplaresDisponibles;

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public int getAnioPublicacion() { return anioPublicacion; }
    public void setAnioPublicacion(int anioPublicacion) { this.anioPublicacion = anioPublicacion; }
    public int getNumeroEjemplares() { return numeroEjemplares; }
    public void setNumeroEjemplares(int numeroEjemplares) { this.numeroEjemplares = numeroEjemplares; }
    public int getEjemplaresDisponibles() { return ejemplaresDisponibles; }
    public void setEjemplaresDisponibles(int ejemplaresDisponibles) { this.ejemplaresDisponibles = ejemplaresDisponibles; }
}
