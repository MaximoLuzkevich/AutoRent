# AutoRent

AutoRent es una API RESTful desarrollada con Java y Spring Boot para gestionar una plataforma de alquiler de autos entre usuarios. El sistema permite registrar usuarios, administrar roles, publicar autos, crear reservas, registrar pagos, gestionar perfiles de propietarios, cargar imagenes de autos y publicar reviews.

El proyecto fue realizado como Trabajo Practico Final de Programacion III, con foco en arquitectura backend, persistencia de datos, validaciones, manejo de errores, documentacion, autenticacion/autorizacion y uso de Git/GitHub. Tambien incluye un frontend simple realizado con HTML, Bootstrap y JavaScript para probar los flujos principales desde el navegador.

## Tecnologias utilizadas

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA
- Spring Security
- MySQL
- Maven
- Lombok
- Bean Validation
- Swagger / OpenAPI
- JWT para autenticacion
- HTML, CSS, Bootstrap y JavaScript para el frontend simple

## Repositorio

Repositorio de GitHub:

```text
https://github.com/MaximoLuzkevich/AutoRent
```

## Estructura general

El backend esta organizado en capas para separar responsabilidades:

```text
AutoRent/src/main/java/com/AutoRent/Backend
  config/       Configuracion de Spring Security, CORS y OpenAPI
  controller/   Endpoints REST expuestos por la API
  dto/          Objetos usados para requests y responses
  exception/    Excepciones propias y manejador global de errores
  model/        Entidades JPA que representan el dominio
  repository/   Acceso a datos mediante Spring Data JPA
  security/     Logica de JWT, filtro de autenticacion y UserDetailsService
  service/      Logica de negocio del sistema
```

El frontend estatico se encuentra en:

```text
AutoRent/src/main/resources/static
```

Incluye pantallas de login, registro, inicio de alquileres, ficha de auto, reservas, pagos, perfil, vistas de propietario, vistas de administrador, formulario para convertirse en propietario y formulario para publicar/modificar autos.

La idea principal es que los controllers reciban las peticiones HTTP, los services resuelvan las reglas de negocio, los repositories accedan a la base de datos y los DTOs eviten exponer directamente las entidades.

## Entidades principales

- `Usuario`: persona registrada en el sistema.
- `Rol`: permisos del usuario dentro de la plataforma.
- `PerfilPropietario`: datos extra de un usuario que publica autos.
- `Auto`: vehiculo disponible para alquiler.
- `CategoriaAuto`: categoria del auto, por ejemplo economico, premium, SUV o electrico.
- `ImagenAuto`: imagenes asociadas a un auto.
- `Reserva`: alquiler solicitado por un cliente para un auto y rango de fechas.
- `Pago`: pago asociado a una reserva.
- `Review`: opinion y calificacion de un cliente sobre un auto.

## Roles del sistema

El sistema maneja tres roles:

- `CLIENTE`: puede registrarse, iniciar sesion, consultar autos, crear reservas, registrar pagos y publicar reviews.
- `PROPIETARIO`: puede publicar autos, modificar sus publicaciones, gestionar imagenes y consultar reservas asociadas a sus autos.
- `ADMINISTRADOR`: puede consultar usuarios, administrar roles, verificar propietarios, aprobar/rechazar pagos y acceder a informacion administrativa.

## Base de datos

La API utiliza MySQL. El script de creacion de base de datos se encuentra en:

```text
db_autoRent.sql
```

El script incluye la creacion de tablas principales y datos iniciales para roles y categorias.

Configuracion local actual:

```properties
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/db_autoRent}
spring.datasource.username=${DB_USER:root}
spring.datasource.password=${DB_PASSWORD:123456789}
spring.jpa.hibernate.ddl-auto=${DDL_AUTO:validate}
jwt.secret=${JWT_SECRET:AutoRentClaveSecretaParaFirmarTokensJwtCambiarEnProduccion2026}
```

Para ejecutar el proyecto localmente, primero se debe crear la base ejecutando el script SQL en MySQL.

Para despliegue se recomienda configurar variables de entorno:

| Variable | Descripcion |
|---|---|
| `DB_URL` | URL JDBC de la base de datos MySQL |
| `DB_USER` | Usuario de la base de datos |
| `DB_PASSWORD` | Password de la base de datos |
| `JWT_SECRET` | Clave secreta para firmar tokens JWT |
| `JWT_EXPIRATION_MILLIS` | Duracion del token en milisegundos |
| `DDL_AUTO` | Estrategia de Hibernate, por defecto `validate` |
| `SHOW_SQL` | Mostrar SQL en consola, por defecto `false` |
| `CORS_ALLOWED_ORIGINS` | Origenes permitidos separados por coma |
| `MERCADOPAGO_ACCESS_TOKEN` | Token de Mercado Pago para generar links de Checkout Pro |

## Como ejecutar localmente

1. Clonar el repositorio:

```bash
git clone https://github.com/MaximoLuzkevich/AutoRent.git
cd AutoRent/AutoRent
```

2. Crear la base de datos en MySQL usando el archivo:

```text
../db_autoRent.sql
```

3. Verificar la configuracion de `application.properties`:

```text
AutoRent/src/main/resources/application.properties
```

4. Ejecutar la aplicacion:

```bash
mvn spring-boot:run
```

5. La aplicacion queda disponible en:

```text
http://localhost:8080
```

Pantallas principales del frontend:

```text
Login: http://localhost:8080/login.html
Registro: http://localhost:8080/registro.html
Inicio: http://localhost:8080/cliente-inicio.html
Ficha de auto: http://localhost:8080/auto-detalle.html?id=1
Mis reservas: http://localhost:8080/cliente-reservas.html
Mis pagos: http://localhost:8080/cliente-pagos.html
Mis autos: http://localhost:8080/propietario-autos.html
Solicitudes de reserva: http://localhost:8080/propietario-solicitudes.html
Propietarios admin: http://localhost:8080/admin-propietarios.html
Pagos admin: http://localhost:8080/admin-pagos.html
```

Importante: el frontend debe abrirse desde `http://localhost:8080/...` con Spring Boot levantado. No se recomienda abrir los archivos HTML directamente desde el explorador, porque las llamadas `fetch` necesitan comunicarse con la API del backend.

## Swagger / OpenAPI

La documentacion navegable de endpoints esta disponible en:

```text
http://localhost:8080/swagger-ui.html
```

Tambien puede accederse a la especificacion OpenAPI en:

```text
http://localhost:8080/v3/api-docs
```

Swagger permite visualizar y probar los endpoints desde el navegador.

La configuracion OpenAPI incluye seguridad Bearer JWT. Para probar endpoints protegidos desde Swagger:

1. Iniciar sesion con `POST /api/usuarios/login`.
2. Copiar el valor de `token`.
3. Presionar el boton `Authorize`.
4. Pegar el token JWT.
5. Ejecutar endpoints protegidos.

## Autenticacion y autorizacion

El sistema usa Spring Security con JWT.

Endpoints publicos principales:

- `POST /api/usuarios/registro`
- `POST /api/usuarios/login`
- `GET /api/autos`
- `GET /api/autos/{idAuto}`
- `GET /api/categorias`
- `GET /api/reviews`
- Swagger/OpenAPI

Para iniciar sesion:

```http
POST /api/usuarios/login
Content-Type: application/json
```

Body:

```json
{
  "email": "usuario@test.com",
  "password": "123456"
}
```

Respuesta esperada:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipoToken": "Bearer",
  "usuario": {
    "idUsuario": 1,
    "nombre": "Usuario Demo",
    "email": "usuario@test.com",
    "telefono": "1122334455",
    "fechaRegistro": "2026-06-13T15:00:00",
    "activo": true,
    "roles": ["CLIENTE"]
  }
}
```

Para consumir endpoints protegidos se debe enviar el token en el header:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

La autorizacion restringe acciones segun los roles `CLIENTE`, `PROPIETARIO` y `ADMINISTRADOR`.

## Endpoints principales

### Usuarios

- `POST /api/usuarios/registro`: registrar usuario.
- `POST /api/usuarios/login`: iniciar sesion y obtener token JWT.
- `POST /api/usuarios/logout`: cerrar sesion del lado del cliente eliminando el token JWT.
- `GET /api/usuarios/me`: consultar los datos y roles del usuario autenticado.
- `PUT /api/usuarios/me`: modificar nombre, email y telefono del usuario autenticado.
- `DELETE /api/usuarios/me`: dar de baja el usuario autenticado.
- `GET /api/usuarios`: listar usuarios.
- `GET /api/usuarios/{idUsuario}`: buscar usuario por ID.
- `GET /api/usuarios/email/{email}`: buscar usuario por email.
- `GET /api/usuarios/rol/{rol}`: listar usuarios por rol.
- `DELETE /api/usuarios/{idUsuario}`: desactivar usuario.
- `PUT /api/usuarios/{idUsuario}/roles/{rol}`: agregar rol a usuario.

### Autos

- `POST /api/autos/propietario/{idPropietario}`: publicar auto.
- `POST /api/autos/me`: publicar auto usando el usuario autenticado.
- `GET /api/autos`: listar autos activos.
- `GET /api/autos/filtrar`: filtrar autos por ciudad, marca, categoria, precio maximo, pasajeros, transmision o combustible.
- `GET /api/autos/disponibles`: buscar autos disponibles por ciudad, fecha de inicio y fecha de fin.
- `GET /api/autos/me`: listar autos publicados por el propietario autenticado.
- `GET /api/autos/me/estado/{activo}`: listar autos propios activos o inactivos.
- `GET /api/autos/me/categoria/{categoria}`: listar autos propios por categoria.
- `GET /api/autos/{idAuto}`: buscar auto por ID.
- `GET /api/autos/propietario/{idPropietario}`: listar autos de un propietario.
- `GET /api/autos/categoria/{categoria}`: filtrar por categoria.
- `GET /api/autos/marca/{marca}`: filtrar por marca.
- `GET /api/autos/ciudad/{ciudad}`: filtrar por ciudad.
- `PUT /api/autos/{idAuto}/propietario/{idPropietario}`: modificar auto.
- `PUT /api/autos/{idAuto}/me`: modificar un auto propio usando el usuario autenticado.
- `DELETE /api/autos/{idAuto}/propietario/{idPropietario}`: desactivar auto.
- `DELETE /api/autos/{idAuto}/me`: desactivar un auto propio usando el usuario autenticado.

### Reservas

- `POST /api/reservas/cliente/{idCliente}`: crear reserva.
- `POST /api/reservas/me`: crear reserva usando el usuario autenticado.
- `GET /api/reservas/{idReserva}`: buscar reserva.
- `GET /api/reservas/cliente/{idCliente}`: listar reservas de un cliente.
- `GET /api/reservas/me`: listar reservas del usuario autenticado.
- `GET /api/reservas/me/estado/{estado}`: listar reservas propias por estado.
- `GET /api/reservas/me/fechas/{desde}/{hasta}`: listar reservas propias por rango de fechas.
- `GET /api/reservas/me/propietario/pendientes`: listar reservas pendientes recibidas por autos propios.
- `GET /api/reservas/propietario/{idPropietario}`: listar reservas de autos de un propietario.
- `GET /api/reservas/estado/{estado}`: listar reservas por estado.
- `PUT /api/reservas/{idReserva}/confirmar`: confirmar reserva, solo administrador.
- `PUT /api/reservas/{idReserva}/estado/confirmada`: confirmar reserva, solo administrador.
- `PUT /api/reservas/{idReserva}/cancelar`: cancelar reserva, cliente o administrador.
- `PUT /api/reservas/{idReserva}/estado/cancelada`: cancelar reserva, cliente o administrador.
- `PUT /api/reservas/{idReserva}/finalizar`: finalizar reserva, solo administrador.
- `PUT /api/reservas/{idReserva}/estado/finalizada`: finalizar reserva, solo administrador.

### Pagos

- `POST /api/pagos`: registrar pago.
- `GET /api/pagos`: listar todos los pagos, solo administrador.
- `GET /api/pagos/me`: listar pagos del usuario autenticado como cliente.
- `GET /api/pagos/me/propietario`: listar pagos recibidos por autos del usuario autenticado como propietario.
- `GET /api/pagos/reserva/{idReserva}`: listar pagos por reserva.
- `GET /api/pagos/cliente/{idCliente}`: listar pagos de un cliente.
- `GET /api/pagos/propietario/{idPropietario}`: listar pagos de un propietario.
- `GET /api/pagos/estado/{estado}`: listar pagos por estado.
- `GET /api/pagos/fechas/{desde}/{hasta}`: listar pagos por rango de fechas.
- `PUT /api/pagos/{idPago}/aprobar`: aprobar pago.
- `PUT /api/pagos/{idPago}/estado/aprobado`: aprobar pago.
- `PUT /api/pagos/{idPago}/rechazar`: rechazar pago.
- `PUT /api/pagos/{idPago}/estado/rechazado`: rechazar pago.

### Propietarios

- `POST /api/propietarios/{idUsuario}`: crear perfil de propietario.
- `POST /api/propietarios/me`: crear perfil de propietario para el usuario autenticado.
- `PUT /api/propietarios/{idUsuario}`: modificar perfil.
- `PUT /api/propietarios/me`: modificar el perfil del usuario autenticado.
- `DELETE /api/propietarios/{idUsuario}`: dar de baja un perfil de propietario.
- `DELETE /api/propietarios/me`: dar de baja el perfil de propietario del usuario autenticado.
- `PUT /api/propietarios/{idUsuario}/verificar`: verificar propietario.
- `GET /api/propietarios/{idUsuario}`: buscar perfil por usuario.
- `GET /api/propietarios/me`: buscar el perfil del usuario autenticado.
- `GET /api/propietarios/verificados/{verificado}`: listar por estado de verificacion.
- `GET /api/propietarios/activos/{activo}`: listar propietarios activos o dados de baja.
- `GET /api/propietarios/ciudad/{ciudad}`: filtrar por ciudad.
- `GET /api/propietarios/provincia/{provincia}`: filtrar por provincia.
- `GET /api/propietarios/nombre/{nombre}`: filtrar por nombre de usuario.

### Imagenes de autos

- `POST /api/autos/{idAuto}/imagenes`: agregar imagen.
- `GET /api/autos/{idAuto}/imagenes`: listar imagenes de un auto.
- `GET /api/autos/{idAuto}/imagenes/principal`: obtener imagen principal.
- `DELETE /api/autos/{idAuto}/imagenes/{idImagen}`: eliminar imagen.

### Reviews

- `POST /api/reviews/cliente/{idCliente}`: crear review.
- `POST /api/reviews/me`: crear review usando el usuario autenticado.
- `GET /api/reviews`: listar reviews.
- `GET /api/reviews/{idReview}`: buscar review por ID.
- `GET /api/reviews/auto/{idAuto}`: listar reviews de un auto.
- `DELETE /api/reviews/{idReview}`: eliminar review.

### Categorias y roles

- `GET /api/categorias`: listar categorias.
- `GET /api/categorias/{nombre}`: buscar categoria.
- `GET /api/roles`: listar roles.
- `GET /api/roles/{nombreRol}`: buscar rol.

## Ejemplos de requests

### Registro

```http
POST /api/usuarios/registro
Content-Type: application/json
```

```json
{
  "nombre": "Cliente Demo",
  "email": "cliente@test.com",
  "password": "123456",
  "telefono": "1122334455"
}
```

### Crear reserva

```http
POST /api/reservas/cliente/1
Authorization: Bearer TOKEN
Content-Type: application/json
```

```json
{
  "idAuto": 1,
  "fechaInicio": "2026-07-01",
  "fechaFin": "2026-07-05"
}
```

### Buscar autos disponibles

```http
GET /api/autos/disponibles?ciudad=Cordoba&fechaInicio=2026-07-01&fechaFin=2026-07-05
```

Tambien se pueden sumar filtros opcionales como `precioMax`, `pasajeros`, `categoria`, `transmision`, `combustible` o `marca`.

### Registrar pago

```http
POST /api/pagos
Authorization: Bearer TOKEN
Content-Type: application/json
```

```json
{
  "idReserva": 1,
  "monto": 40000,
  "metodoPago": "TARJETA",
  "titularTarjeta": "Cliente Demo",
  "numeroTarjeta": "4509953566233704",
  "vencimientoTarjeta": "12/28",
  "codigoSeguridad": "123"
}
```

Los datos de tarjeta se validan para simular el pago, pero no se guardan como datos sensibles en la base.

Para pagar con Mercado Pago:

```json
{
  "idReserva": 1,
  "monto": 40000,
  "metodoPago": "MERCADO_PAGO"
}
```

La respuesta incluye un `linkPago` generado por Mercado Pago mediante Checkout Pro. Para que funcione, se debe configurar `MERCADOPAGO_ACCESS_TOKEN` como variable de entorno antes de iniciar Spring Boot. Para pruebas debe usarse una credencial de prueba de Mercado Pago.

En esta version, el sistema guarda el pago como `PENDIENTE` y el administrador puede aprobarlo desde la vista de pagos admin. Como mejora futura se podria agregar un webhook para que Mercado Pago avise automaticamente cuando el pago fue aprobado.

## Validaciones y manejo de errores

El proyecto utiliza Bean Validation para validar los datos recibidos en DTOs y entidades. Algunos ejemplos:

- Campos obligatorios con `@NotBlank` y `@NotNull`.
- Formato de email con `@Email`.
- Longitudes maximas con `@Size`.
- Valores numericos minimos con `@Min` y `@DecimalMin`.

Tambien cuenta con excepciones personalizadas y un `GlobalExceptionHandler` para devolver respuestas claras:

- `IdNoEncontradoException`: recurso inexistente.
- `DatoDuplicadoException`: datos repetidos, por ejemplo email o patente.
- `ParametroIncorrectoException`: datos invalidos para una regla de negocio.
- `LoginRequeridoException`: credenciales invalidas o usuario inactivo.
- `PermisoInsuficienteException`: usuario sin permisos para una accion.

## Reglas de negocio destacadas

- Un usuario nuevo se registra como `CLIENTE`.
- Las passwords se guardan encriptadas con BCrypt.
- Solo usuarios con rol `PROPIETARIO` o `ADMINISTRADOR` pueden publicar autos.
- Al dar de baja un perfil de propietario, se desactiva el perfil y se quita el rol `PROPIETARIO`.
- No puede registrarse un email duplicado.
- No puede registrarse una patente duplicada.
- La busqueda de autos disponibles valida ciudad y fechas.
- No puede crearse una reserva si la fecha de fin no es posterior a la fecha de inicio.
- No puede crearse una reserva con fecha de inicio anterior al dia actual.
- No puede crearse una reserva si ya existe otra reserva superpuesta para el mismo auto.
- Un propietario no puede reservar su propio auto.
- Las reservas manejan estados como `PENDIENTE`, `CONFIRMADA`, `CANCELADA` y `FINALIZADA`.
- El propietario no aprueba reservas manualmente: el sistema valida disponibilidad automaticamente.
- El propietario puede consultar las reservas recibidas por sus autos.
- La reserva queda `PENDIENTE` cuando se crea y pasa a `CONFIRMADA` cuando se aprueba un pago valido.
- Solo el administrador puede confirmar o finalizar reservas manualmente.
- El cliente o el administrador pueden cancelar reservas, salvo que esten `FINALIZADA`.
- Los pagos manejan estados como `PENDIENTE`, `APROBADO` y `RECHAZADO`.
- El monto de un pago debe coincidir con el total de la reserva.
- Un cliente solo puede pagar reservas propias.
- Los pagos con tarjeta requieren datos basicos de tarjeta, pero esos datos no se guardan.
- Los pagos con Mercado Pago crean una preferencia de Checkout Pro y devuelven el link de pago cuando el token esta configurado.
- Cliente, propietario y administrador solo pueden consultar pagos que les correspondan, salvo reportes administrativos.
- No puede registrarse mas de un pago pendiente o aprobado para la misma reserva.
- Solo se pueden aprobar o rechazar pagos `PENDIENTE`.
- Al aprobar un pago valido, la reserva queda `CONFIRMADA` si estaba `PENDIENTE`.
- Un cliente solo puede crear reviews sobre autos que tuvo en reservas `FINALIZADA`.
- Los endpoints recomendados con `/me` usan el usuario autenticado por JWT y evitan confiar en IDs enviados por URL.
- Solo el propietario de un auto o un administrador puede modificar sus datos o imagenes.
- Solo el autor de una review o un administrador puede eliminarla.

## Tests

El proyecto incluye una prueba de carga de contexto:

```text
AutoRent/src/test/java/com/AutoRent/AutoRentApplicationTests.java
```

Tambien incluye tests unitarios de services para reglas de negocio importantes:

- Login con password incorrecta.
- Reserva de auto propio.
- Pago con monto distinto al total de la reserva.
- Pago de una reserva ajena.
- Pago con tarjeta sin datos obligatorios.
- Pago con Mercado Pago y link de Checkout Pro.
- Aprobacion de pago sin rol administrador.
- Consulta de pagos de otro propietario.
- Consulta de reserva ajena.
- Reserva superpuesta para un auto.
- Email duplicado al registrar usuario.
- Login correcto con generacion de token.
- Patente duplicada al publicar auto.
- Busqueda de autos disponibles con fechas invalidas.
- Modificacion de auto ajeno.
- Review sin reserva finalizada.
- Eliminacion de review ajena.

Para ejecutar los tests:

```bash
mvn test
```

Los tests usan el perfil `test` con una base H2 en memoria, por lo que no dependen de tener MySQL local levantado. Actualmente queda pendiente ampliar la cobertura con mas casos de integracion.

## Usuarios de prueba

El script `db_autoRent.sql` incluye usuarios de prueba para facilitar la correccion y la defensa.

Todos usan la password:

```text
123456
```

| Rol principal | Email | Password |
|---|---|---|
| Cliente | `cliente@test.com` | `123456` |
| Propietario | `propietario@test.com` | `123456` |
| Administrador | `admin@test.com` | `123456` |
| Administrador y propietario | `adminprop@test.com` | `123456` |

El script tambien crea perfiles de propietario, autos, reservas, pagos, reviews e imagenes demo.

Autos demo:

| Propietario | Auto | Patente | Ciudad |
|---|---|---|---|
| `propietario@test.com` | Toyota Corolla | `DEMO123` | Buenos Aires |
| `propietario@test.com` | Ford EcoSport | `PROP456` | Cordoba |
| `adminprop@test.com` | Chevrolet Onix | `ADM111` | Buenos Aires |
| `adminprop@test.com` | Tesla Model 3 | `ADM222` | Rosario |

El script deja reservas y pagos ya cargados para probar historiales. Tambien deja una reserva pendiente sin pago para probar el alta de un pago desde la vista de pagos del cliente.

## Despliegue local

Para la entrega, el proyecto puede ejecutarse localmente levantando MySQL y Spring Boot.

Pasos resumidos:

1. Levantar MySQL.
2. Ejecutar `db_autoRent.sql`.
3. Iniciar Spring Boot con `mvn spring-boot:run`.
4. Abrir `http://localhost:8080/login.html`.
5. Probar con los usuarios demo.

Si se desea probar Mercado Pago, antes de iniciar Spring Boot se debe configurar la variable de entorno:

```powershell
$env:MERCADOPAGO_ACCESS_TOKEN="ACCESS_TOKEN_DE_PRUEBA"
```

Luego se inicia la aplicacion desde esa misma terminal.

## Deploy externo

Un deploy externo podria realizarse en Render, Railway u otra plataforma similar, usando una base de datos MySQL remota.

Variables necesarias para un deploy:

- `DB_URL`
- `DB_USER`
- `DB_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRATION_MILLIS`
- `DDL_AUTO`
- `CORS_ALLOWED_ORIGINS`
- `MERCADOPAGO_ACCESS_TOKEN`, si se desea usar Mercado Pago

Checklist para deploy:

- Crear una base de datos MySQL remota.
- Ejecutar `db_autoRent.sql` sobre la base remota.
- Configurar las variables de entorno.
- Levantar la aplicacion.
- Verificar `/swagger-ui.html`.
- Probar login y un endpoint protegido con token.

## Autor

Proyecto desarrollado de forma individual.

```text
Maximo Luzkevich
```

## Aclaraciones para la correccion

- El proyecto esta pensado principalmente como backend API REST.
- Se incluye un frontend simple para demostrar los flujos principales.
- La documentacion tecnica de endpoints se encuentra disponible mediante Swagger/OpenAPI.
- La autenticacion se realiza mediante JWT.
- La autorizacion se basa en roles.
- El script SQL contiene la estructura de base de datos y datos iniciales.
- Mercado Pago esta preparado mediante Checkout Pro usando access token por variable de entorno. La aprobacion automatica por webhook queda como mejora futura.
- Algunas mejoras pendientes recomendadas son ampliar tests de integracion y completar un deploy externo.
