package com.api.biblioteca;

import java.time.LocalDate;
import java.util.concurrent.*;
import java.util.List;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.api.biblioteca.models.*;
import com.api.biblioteca.repositories.*;
import com.api.biblioteca.services.*;

@SpringBootTest
class BibliotecaIntegrationTest {
    @Autowired LibroService libros;
    @Autowired UsuarioService usuarios;
    @Autowired PrestamoService prestamos;
    @Autowired LibroRepository libroRepository;
    @Autowired UsuarioRepository usuarioRepository;
    @Autowired PrestamoRepository prestamoRepository;

    @BeforeEach
    void limpiar() {
        prestamoRepository.deleteAll();
        libroRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    void prestamoYDevolucionControlanLosEjemplaresYLasFechas() {
        LibroModel libro = libro();
        UsuarioModel usuario = usuario("ana@example.com");
        PrestamoModel prestamo = prestamos.crear(solicitud(libro, usuario));
        assertEquals(LocalDate.now(), prestamo.getFechaPrestamo());
        assertNull(prestamo.getFechaDevolucion());
        assertEquals(0, libros.obtener(libro.getIsbn()).getEjemplaresDisponibles());
        assertEquals(409, assertThrows(ResponseStatusException.class, () -> prestamos.crear(solicitud(libro, usuario))).getStatusCode().value());
        prestamos.devolver(prestamo.getId());
        assertEquals(LocalDate.now(), prestamos.obtener(prestamo.getId()).getFechaDevolucion());
        assertEquals(1, libros.obtener(libro.getIsbn()).getEjemplaresDisponibles());
        assertThrows(ResponseStatusException.class, () -> prestamos.devolver(prestamo.getId()));
    }

    @Test
    void rechazaOtroUsuarioCuandoNoHayEjemplares() {
        LibroModel libro = libro();
        UsuarioModel ana = usuario("ana@example.com");
        UsuarioModel luis = usuario("luis@example.com");
        prestamos.crear(solicitud(libro, ana));
        ResponseStatusException error = assertThrows(ResponseStatusException.class, () -> prestamos.crear(solicitud(libro, luis)));
        assertEquals("No hay ejemplares disponibles", error.getReason());
        assertEquals(1, prestamoRepository.count());
    }

    @Test
    void conservaLaContrasenaAlActualizarYLaGuardaConBCrypt() {
        UsuarioModel usuario = usuario("ana@example.com");
        String hash = usuario.getPassword();
        assertNotEquals("BibliotecaDemo2026!", hash);
        assertTrue(new BCryptPasswordEncoder().matches("BibliotecaDemo2026!", hash));
        UsuarioModel datos = datosUsuario("ana@example.com");
        datos.setNombre("Ana actualizada");
        datos.setPassword(null);
        usuarios.actualizar(usuario.getId(), datos);
        assertEquals(hash, usuarioRepository.findById(usuario.getId()).orElseThrow().getPassword());
    }

    @Test
    void noPermiteEliminarNiReducirEjemplaresDeUnLibroPrestado() {
        LibroModel libro = libro();
        UsuarioModel usuario = usuario("ana@example.com");
        PrestamoModel prestamo = prestamos.crear(solicitud(libro, usuario));
        assertThrows(ResponseStatusException.class, () -> libros.eliminar(libro.getIsbn()));
        assertThrows(ResponseStatusException.class, () -> usuarios.eliminar(usuario.getId()));
        assertThrows(ResponseStatusException.class, () -> prestamos.eliminar(prestamo.getId()));
        libro.setNumeroEjemplares(0);
        assertThrows(ResponseStatusException.class, () -> libros.actualizar(libro.getIsbn(), libro));
        assertEquals(1, libroRepository.findById(libro.getIsbn()).orElseThrow().getNumeroEjemplares());
    }

    @Test
    void rechazaUsuariosInactivosYReferenciasInexistentes() {
        LibroModel libro = libro();
        UsuarioModel usuario = usuario("ana@example.com");
        UsuarioModel datos = datosUsuario(usuario.getEmail());
        datos.setEstado(UsuarioModel.Estado.INACTIVO);
        usuarios.actualizar(usuario.getId(), datos);
        assertThrows(ResponseStatusException.class, () -> prestamos.crear(solicitud(libro, usuario)));
        LibroModel inexistente = new LibroModel();
        inexistente.setIsbn("9780000000003");
        assertEquals(404, assertThrows(ResponseStatusException.class, () -> prestamos.crear(solicitud(inexistente, usuario))).getStatusCode().value());
    }

    @Test
    void dosSolicitudesSimultaneasNoPrestanElMismoEjemplar() throws Exception {
        LibroModel libro = libro();
        List<UsuarioModel> socios = List.of(usuario("ana@example.com"), usuario("luis@example.com"));
        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch inicio = new CountDownLatch(1);
        try {
            var resultados = socios.stream().map(socio -> pool.submit(() -> {
                inicio.await();
                try { prestamos.crear(solicitud(libro, socio)); return true; }
                catch (ResponseStatusException error) { assertEquals(409, error.getStatusCode().value()); return false; }
            })).toList();
            inicio.countDown();
            int exitos = 0;
            for (var resultado : resultados) if (resultado.get(10, TimeUnit.SECONDS)) exitos++;
            assertEquals(1, exitos);
            assertEquals(1, prestamoRepository.count());
        } finally { pool.shutdownNow(); }
    }

    private LibroModel libro() {
        LibroModel libro = new LibroModel();
        libro.setIsbn("9780000000002");
        libro.setTitulo("Libro de prueba");
        libro.setAutor("Autor de prueba");
        libro.setAnioPublicacion(2020);
        libro.setNumeroEjemplares(1);
        return libros.crear(libro);
    }

    private UsuarioModel usuario(String email) { return usuarios.crear(datosUsuario(email)); }

    private UsuarioModel datosUsuario(String email) {
        UsuarioModel usuario = new UsuarioModel();
        usuario.setNombre("Ana");
        usuario.setApellidos("Prueba");
        usuario.setEmail(email);
        usuario.setPassword("BibliotecaDemo2026!");
        return usuario;
    }

    private PrestamoModel solicitud(LibroModel libro, UsuarioModel usuario) {
        PrestamoModel prestamo = new PrestamoModel();
        prestamo.setLibro(libro);
        prestamo.setUsuario(usuario);
        return prestamo;
    }
}
