package com.api.biblioteca.services;

import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.api.biblioteca.models.UsuarioModel;
import com.api.biblioteca.repositories.*;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarios;
    private final PrestamoRepository prestamos;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UsuarioService(UsuarioRepository usuarios, PrestamoRepository prestamos) {
        this.usuarios = usuarios;
        this.prestamos = prestamos;
    }

    public List<UsuarioModel> listar() { return usuarios.findAll(); }

    public UsuarioModel obtener(Long id) {
        return usuarios.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no existe"));
    }

    @Transactional
    public UsuarioModel crear(UsuarioModel usuario) {
        usuario.setId(null);
        validarEstado(usuario);
        usuario.setEmail(usuario.getEmail().trim().toLowerCase(Locale.ROOT));
        if (usuarios.existsByEmail(usuario.getEmail())) throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado");
        if (usuario.getPassword() == null || usuario.getPassword().isBlank()) throw new IllegalArgumentException("La contraseña es obligatoria");
        usuario.setPassword(encoder.encode(usuario.getPassword()));
        return usuarios.saveAndFlush(usuario);
    }

    @Transactional
    public UsuarioModel actualizar(Long id, UsuarioModel datos) {
        UsuarioModel usuario = usuarios.buscarParaActualizar(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no existe"));
        validarEstado(datos);
        String correo = datos.getEmail().trim().toLowerCase(Locale.ROOT);
        if (usuarios.existsByEmailAndIdNot(correo, id)) throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado");
        usuario.setNombre(datos.getNombre());
        usuario.setApellidos(datos.getApellidos());
        usuario.setEmail(correo);
        usuario.setRol(datos.getRol());
        usuario.setEstado(datos.getEstado());
        usuario.setFechaNacimiento(datos.getFechaNacimiento());
        usuario.setTelefono(datos.getTelefono());
        usuario.setDireccion(datos.getDireccion());
        if (datos.getPassword() != null && !datos.getPassword().isBlank()) usuario.setPassword(encoder.encode(datos.getPassword()));
        return usuarios.saveAndFlush(usuario);
    }

    @Transactional
    public void eliminar(Long id) {
        UsuarioModel usuario = usuarios.buscarParaActualizar(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no existe"));
        if (prestamos.existsByUsuario_Id(id)) throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario tiene préstamos registrados");
        usuarios.delete(usuario);
    }

    private void validarEstado(UsuarioModel usuario) {
        if (usuario.getEstado() == null || usuario.getRol() == null) throw new IllegalArgumentException("El rol y el estado son obligatorios");
    }
}
