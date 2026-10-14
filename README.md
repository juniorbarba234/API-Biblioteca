# API - Biblioteca

API REST para administrar una biblioteca. Permite registrar libros y usuarios, realizar préstamos, consultar los registros y devolver libros.

## Recursos de la API

| Ruta | Función |
| --- | --- |
| `/api/libros` | Registrar y consultar libros |
| `/api/libros/{isbn}` | Consultar, actualizar o eliminar un libro |
| `/api/usuarios` | Registrar y consultar usuarios |
| `/api/usuarios/{id}` | Consultar, actualizar o eliminar un usuario |
| `/api/prestamos` | Registrar y consultar préstamos |
| `/api/prestamos/{id}` | Consultar un préstamo o eliminarlo después de su devolución |
| `/api/prestamos/activos` | Consultar préstamos que siguen activos |
| `/api/prestamos/usuario/{id}` | Consultar los préstamos de un usuario |
| `/api/prestamos/{id}/devolver` | Registrar la devolución de un libro |

GET consulta, POST registra, PUT actualiza y DELETE elimina. La devolución utiliza POST.

## Funcionamiento

Cada libro se identifica mediante su ISBN y tiene un título, autor, año de publicación y número total de ejemplares. La respuesta también muestra cuántos ejemplares siguen disponibles.

El usuario tiene un identificador, nombre, apellidos, correo, contraseña, rol, estado, fecha de creación, fecha de nacimiento, teléfono y dirección. Los roles admitidos son `SOCIO`, `BIBLIOTECARIO` y `ADMIN`. Los estados son `ACTIVO` e `INACTIVO`. La contraseña se guarda con BCrypt y no se muestra en las respuestas.

Un préstamo relaciona al libro con el usuario. La fecha del préstamo se registra automáticamente. La fecha de devolución aparece como `null` mientras el préstamo está activo. Al devolver el libro, se registra la fecha actual y su ejemplar vuelve a estar disponible.

Se rechazan los préstamos si no hay ejemplares, si el usuario está inactivo o si ya tiene un préstamo activo del mismo libro. No se permite reducir el total de ejemplares por debajo de los que están prestados. Los libros y usuarios con préstamos registrados no pueden eliminarse; un préstamo activo debe devolverse antes de eliminarlo.

## Pruebas en Postman

La colección se encuentra en `postman/API - Biblioteca.postman_collection.json`. Se importa en Postman con **Import** y utiliza la dirección `http://127.0.0.1:8001`.

La carpeta **Pruebas principales** contiene las ocho solicitudes para documentar el funcionamiento. Deben enviarse en orden, desde **01 Crear libro**. Los identificadores se guardan automáticamente.

| Prueba | Resultado esperado |
| --- | --- |
| 01 Crear libro | 201 Created |
| 02 Crear usuario | 201 Created |
| 03 Registrar préstamo | 201 Created |
| 04 Consultar libros | 200 OK |
| 05 Consultar préstamos | 200 OK |
| 06 Rechazar préstamo duplicado | 409 Conflict |
| 07 Devolver libro | 200 OK |
| 08 Consultar disponibilidad después de devolver | 200 OK |

La carpeta **Pruebas adicionales** incluye consultas por identificador, actualizaciones, eliminaciones y validaciones de datos. Las solicitudes con errores provocados a propósito deben devolver el código indicado en su comprobación.

## Organización del código

`models` representa los libros, usuarios y préstamos. `repositories` consulta y guarda los datos. `services` revisa las reglas de préstamo y devolución. `controllers` recibe las solicitudes de Postman y devuelve las respuestas.
