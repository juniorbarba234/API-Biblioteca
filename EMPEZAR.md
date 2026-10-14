# API - Biblioteca

## Abrir el código

Desde CMD:

```cmd
cd /d "C:\Users\junio\Documents\Codex\2026-10-01\te\outputs\API-Biblioteca"
code .
```

## Iniciar la API

MySQL debe estar encendido. El proyecto necesita Java 21 o superior.

Desde la terminal de Visual Studio Code:

```powershell
.\EJECUTAR.cmd
```

Espera el mensaje **Started BibliotecaApplication** y deja esa ventana abierta. La dirección de esta API es `http://127.0.0.1:8001` y la base se llama `api_biblioteca`.

Si necesitas configurar la conexión de MySQL, abre `CONFIGURAR.cmd`, escribe el usuario y la contraseña y guarda. Estos datos se conservan únicamente en el archivo local `application-local.properties`, excluido de GitHub.

## Preparar Postman

1. Abre Postman y pulsa **Import**.
2. Selecciona `API - Biblioteca.postman_collection.json` dentro de la carpeta `postman`.
3. Abre la colección **API - Biblioteca**.
4. Abre **Pruebas principales**.
5. Ejecuta las solicitudes en orden. En las solicitudes POST, pulsa **Body** antes de la captura para mostrar los datos enviados.

## Agregar las capturas al documento

Cada captura debe mostrar el método, la dirección, el código de respuesta y los datos importantes. Las siguientes descripciones se pegan solo después de recibir el resultado indicado.

### Prueba 1 Registro de libro

Abre **01 Crear libro** y pulsa **Send**. Debe responder **201 Created**.

Texto: Se registró un libro con su ISBN, título, autor, año de publicación y número de ejemplares. La API respondió con 201 Created y devolvió los datos registrados.

### Prueba 2 Registro de usuario

Abre **02 Crear usuario** y pulsa **Send**. Debe responder **201 Created**.

Texto: Se registró un usuario de la biblioteca. La API respondió con 201 Created y devolvió sus datos con un identificador. La contraseña no se muestra en la respuesta.

### Prueba 3 Registro de préstamo

Abre **03 Registrar préstamo** y pulsa **Send**. Debe responder **201 Created**. Muestra `fechaPrestamo` y `fechaDevolucion` en la captura.

Texto: Se registró un préstamo que relaciona al usuario con el libro. La API respondió con 201 Created, asignó la fecha actual del préstamo y dejó la fecha de devolución vacía mientras sigue activo.

### Prueba 4 Consulta de libros

Abre **04 Consultar libros** y pulsa **Send**. Debe responder **200 OK**. Busca el libro de esta prueba y muestra `numeroEjemplares` y `ejemplaresDisponibles`.

Texto: Se consultaron los libros registrados. La API respondió con 200 OK. El libro utilizado en el préstamo conserva un ejemplar total y muestra cero ejemplares disponibles.

### Prueba 5 Consulta de préstamos

Abre **05 Consultar préstamos** y pulsa **Send**. Debe responder **200 OK**.

Texto: Se consultaron los préstamos registrados. La API respondió con 200 OK y mostró el libro, el usuario y las fechas del préstamo.

### Prueba 6 Validación de préstamo duplicado

Abre **06 Rechazar préstamo duplicado** y pulsa **Send**. Debe responder **409 Conflict** con el mensaje de préstamo activo.

Texto: Se intentó registrar otro préstamo del mismo libro para el mismo usuario. La API respondió con 409 Conflict e indicó que el usuario ya tiene un préstamo activo de ese libro.

### Prueba 7 Devolución de libro

Abre **07 Devolver libro** y pulsa **Send**. Debe responder **200 OK**. Muestra `fechaDevolucion` en la captura.

Texto: Se registró la devolución del libro prestado. La API respondió con 200 OK y añadió la fecha de devolución al préstamo.

### Prueba 8 Disponibilidad después de devolver

Abre **08 Consultar disponibilidad después de devolver** y pulsa **Send**. Debe responder **200 OK** con `ejemplaresDisponibles` igual a 1.

Texto: Se consultó el libro después de su devolución. La API respondió con 200 OK y mostró un ejemplar disponible, comprobando que puede prestarse nuevamente.

## Cómo explicar el proyecto

“Mi API registra libros y usuarios. Un préstamo relaciona al usuario con un libro. El sistema revisa que haya ejemplares disponibles y guarda la fecha actual. Mientras el libro no se devuelve, la fecha de devolución está vacía. Al devolverlo, registra la fecha y permite prestar ese ejemplar otra vez.”
