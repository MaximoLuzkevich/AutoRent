# AutoRent

AutoRent es un sistema de alquiler de autos desarrollado con Java y Spring Boot.
Permite registrar usuarios, publicar autos, buscar disponibilidad por ciudad y fechas, crear reservas, aprobar solicitudes, registrar pagos, cargar imagenes y dejar reviews.

El proyecto incluye un backend API REST y un frontend en HTML, CSS, Bootstrap y JavaScript para probar los flujos principales desde el navegador.

## Aplicacion desplegada

La aplicacion esta desplegada en Railway:

```text
https://autorent-production-d72a.up.railway.app
```

El backend y el frontend se sirven desde el mismo proyecto Spring Boot. La base de datos MySQL tambien esta configurada en Railway.

## Tecnologias

- Java 21
- Spring Boot 4
- Spring Data JPA
- Spring Security con JWT
- MySQL
- Maven
- Lombok
- Swagger / OpenAPI
- Mercado Pago
- Geoapify
- Cloudinary
- HTML, CSS, Bootstrap y JavaScript
- Railway

## Estructura

```text
AutoRent/src/main/java/com/AutoRent/Backend
  config/       Configuraciones generales
  controller/   Endpoints REST
  dto/          Datos de entrada y salida
  exception/    Excepciones y manejo global de errores
  model/        Entidades JPA
  repository/   Acceso a base de datos
  security/     JWT y autenticacion
  service/      Reglas de negocio
```

El frontend esta en:

```text
AutoRent/src/main/resources/static
```

## Base de datos

La base usa MySQL. En produccion se utiliza MySQL en Railway.

El script principal esta en:

```text
db_autoRent.sql
```

Ese archivo crea las tablas y carga datos iniciales para poder probar el sistema.

## Configuracion

El proyecto toma la configuracion desde `application.properties` y variables de entorno.

Variables mas importantes:

```text
DB_URL
DB_USER
DB_PASSWORD
JWT_SECRET
JWT_EXPIRATION_MILLIS
APP_BASE_URL
CORS_ALLOWED_ORIGINS
MERCADOPAGO_ACCESS_TOKEN
GEOAPIFY_API_KEY
CLOUDINARY_CLOUD_NAME
CLOUDINARY_API_KEY
CLOUDINARY_API_SECRET
```

En Railway estas variables se cargan desde la seccion `Variables` del servicio AutoRent.

Para correr localmente, si no se cargan variables, se usan los valores por defecto definidos en:

```text
AutoRent/src/main/resources/application.properties
```

En produccion se usa el perfil:

```text
SPRING_PROFILES_ACTIVE=prod
```

## Como ejecutar

1. Levantar MySQL.
2. Ejecutar `db_autoRent.sql`.
3. Entrar a la carpeta del proyecto Spring:

```bash
cd AutoRent
```

4. Iniciar la aplicacion:

```bash
mvn spring-boot:run
```

5. Abrir:

```text
http://localhost:8080/login.html
```

Para probar la version desplegada:

```text
https://autorent-production-d72a.up.railway.app/login.html
```

## Usuarios de prueba

Todos usan la password:

```text
123456
```

| Rol | Email |
|---|---|
| Cliente | `cliente@test.com` |
| Propietario | `propietario@test.com` |
| Administrador | `admin@test.com` |
| Administrador y propietario | `adminprop@test.com` |

## Funcionalidades principales

- Registro e inicio de sesion.
- Autenticacion con JWT.
- Roles de cliente, propietario y administrador.
- Busqueda de autos disponibles por ciudad y fechas.
- Autocompletado de ubicaciones con Geoapify.
- Publicacion y modificacion de autos.
- Carga de imagenes de autos con Cloudinary.
- Reservas con aprobacion del propietario.
- Pagos con efectivo, tarjeta o Mercado Pago.
- Reviews sobre autos alquilados.
- Vistas para cliente, propietario y administrador.

## Mercado Pago

Para usar Mercado Pago hay que configurar:

```text
MERCADOPAGO_ACCESS_TOKEN
APP_BASE_URL
```

El sistema crea una preferencia de pago y redirige al checkout de Mercado Pago. Cuando el pago se confirma, se actualiza el estado del pago en AutoRent.

## Deploy

El deploy se realiza desde GitHub hacia Railway. Railway toma la rama `main`, compila el proyecto Spring Boot y publica la aplicacion con una URL publica.

La configuracion de produccion esta en:

```text
AutoRent/src/main/resources/application-prod.properties
```

Si se hacen cambios en el codigo, se suben a GitHub y Railway vuelve a desplegar automaticamente.

Si se cambian datos ya cargados en la base online, se actualizan desde MySQL Workbench conectado al MySQL de Railway.

## Swagger

La documentacion de la API se puede ver en:

```text
http://localhost:8080/swagger-ui.html
```

En Railway:

```text
https://autorent-production-d72a.up.railway.app/swagger-ui.html
```

Para probar endpoints protegidos desde Swagger:

1. Iniciar sesion desde `/api/usuarios/login`.
2. Copiar el token.
3. Usar `Authorize`.
4. Pegar el token JWT.

## Tests

Para ejecutar las pruebas:

```bash
mvn test
```

Los tests usan una base H2 en memoria, por lo que no hace falta tener MySQL levantado para correrlos.

## Autor

Proyecto desarrollado de forma individual.

```text
Maximo Rocco Luzkevich Marino
```
