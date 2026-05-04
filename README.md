# NovaBank
## Descripción
NovaBank es un sistema de gestión bancaria implementado como una **API REST profesional** con Spring Boot. La versión actual transforma el proyecto de una aplicación de consola a un servicio backend accesible mediante HTTP desde cualquier cliente.

Sus funcionalidades actuales son las siguientes:
- Registro de clientes, cuentas y movimientos.
- Ingreso, retiro y transferencias entre cuentas.
- Consultas e historial de movimientos con filtrado por fechas.
- Autenticación segura mediante tokens JWT.
- Documentación interactiva de la API con Swagger UI.

---
## Arquitectura
Se ha refactorizado la arquitectura siguiendo una **arquitectura por capas** estándar de Spring Boot con clara separación de responsabilidades:

```
CONTROLLER → SERVICE → REPOSITORY → MODEL
```

| Capa | Responsabilidad |
|------|----------------|
| `controller` | Recibe peticiones HTTP y delega en los servicios. Sin lógica de negocio. |
| `service` | Lógica de negocio. Gestión de transacciones con `@Transactional`. |
| `repository` | Acceso a datos mediante Spring Data JPA. Sin SQL manual para CRUD básico. |
| `model` | Entidades JPA que representan las tablas de la base de datos. |
| `dto` | Objetos de transferencia que desacoplan el modelo de la API pública. |
| `security` | Filtro JWT y gestión de usuarios. |
| `exception` | Manejo centralizado de errores con `@RestControllerAdvice`. |

---
## Tecnologías utilizadas
- **Java 17**
- **Spring Boot 4.x**
- **Spring Data JPA** con Hibernate
- **Spring Security** con autenticación JWT
- **PostgreSQL**
- **springdoc-openapi** (Swagger UI)
- **Lombok** y **MapStruct**
- **JUnit 5**, **Mockito**, **Spring Boot Test**

---
## Requisitos
Para ejecutar el proyecto, necesitarás tener:
- **Java Development Kit (JDK) 17** o superior.
- **Apache Maven 3.6** o superior.
- **PostgreSQL** instalado y en ejecución.
- **Postman** (opcional, para probar los endpoints).

---
## Configuración de la base de datos
1. Crear la base de datos en PostgreSQL:

```sql
CREATE DATABASE "NovaBank";
```

2. Ejecutar el script de creación de tablas:

```bash
schema.sql
```

3. Configurar las credenciales en `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/NovaBank
    username: postgres
    password: tu_password
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
```

---
## Autenticación
La API usa autenticación **JWT Bearer**. Para acceder a los endpoints protegidos:

**1. Obtener el token:**

```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password"
}
```

**2. Usar el token en las peticiones:**

```bash
Authorization: Bearer eyJhbGciOiJIUzM4NCJ9...
```

---
## Documentación de la API
Una vez arrancada la aplicación, la documentación interactiva está disponible en:

```
http://localhost:8080/swagger-ui.html
```

Para probar endpoints protegidos desde Swagger, pulsa **Authorize** e introduce:

```
Bearer <tu_token_jwt>
```

---
## Ejecución del sistema
Para compilar el proyecto:

```bash
mvn clean compile
```

Para arrancar la aplicación:

```bash
mvn spring-boot:run
```

---
## Ejecución de tests

```bash
mvn test
```

Los tests de repositorio e integración usan **H2 en memoria** y no requieren PostgreSQL activo. Asegúrate de tener la dependencia H2 con scope `test` en el `pom.xml`.

---
## Patrones de diseño aplicados
- **Singleton** — gestión de beans por Spring IoC.
- **Factory** — `AuthenticationProvider` y `PasswordEncoder` definidos como beans en `SecurityConfig`.
- **Decorator** — cadena de filtros de Spring Security (`JwtFilter` decorando la cadena HTTP).
- **Repository** — abstracción de acceso a datos con `JpaRepository`.
- **DTO / Mapper** — desacoplamiento entre modelo de dominio y API pública.

---
## Endpoints principales

| Método | Ruta | Descripción | Auth |
|--------|------|-------------|------|
| POST | `/api/auth/login` | Obtener token JWT | Pública |
| POST | `/api/customers` | Crear cliente | JWT |
| GET | `/api/customers` | Listar clientes | JWT |
| POST | `/api/accounts` | Crear cuenta | JWT |
| POST | `/api/operations/deposit` | Realizar depósito | JWT |
| POST | `/api/operations/withdrawal` | Realizar retiro | JWT |
| POST | `/api/operations/transfer` | Transferencia entre cuentas | JWT |
| GET | `/api/accounts/{id}/transactions` | Historial de movimientos | JWT |