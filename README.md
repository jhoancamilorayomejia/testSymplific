# Symplifica Dashboard

Prueba técnica Fullstack MID — Dashboard de empleados con gestión de beneficios
y enriquecimiento de ubicación vía la API de Nominatim (OpenStreetMap).

# La aplicación permite:

- Gestión de empleados.
- Gestión de beneficios asociados a cada empleado.
- Consulta de ubicación mediante la API de **Nominatim (OpenStreetMap)**.
- Autenticación mediante **JWT**.
- Recuperación de contraseña mediante correo electrónico (funcionalidad adicional).
- Orquestación de los servicios **Backend** y **Frontend** mediante **Docker Compose**.

---

# Tecnologías utilizadas

- **Backend:** Java 17+, Spring Boot, Spring Security (JWT), JPA/Hibernate
- **Frontend:** Vue.js 3, Vue Router, Pinia
- **Base de datos:** PostgreSQL (Docker)
- **Integración externa:** Nominatim (OpenStreetMap) - XML

---

# Configuración de la base de datos

## 1. Crear la base de datos

```sql
CREATE DATABASE symplifica-db;
```

---

## 2. Crear la tabla `users`

```sql
CREATE TABLE users (
    iduser BIGSERIAL PRIMARY KEY,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(50) NOT NULL,
    reset_token VARCHAR(255),
    reset_token_expiry TIMESTAMP
);
```

---

## 3. Crear la tabla `empleados`

```sql
CREATE TABLE empleados (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    cargo VARCHAR(100),
    ciudad VARCHAR(100) NOT NULL,
    direccion VARCHAR(250),
    fecha_ingreso DATE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
```

---

## 4. Crear la tabla `beneficios`

```sql
CREATE TABLE beneficios (
    id BIGSERIAL PRIMARY KEY,
    monto NUMERIC(12,2) NOT NULL,
    nombre_beneficio VARCHAR(150) NOT NULL,
    id_empleado BIGINT NOT NULL,

    CONSTRAINT fk_beneficio_empleado
        FOREIGN KEY (id_empleado)
        REFERENCES empleados(id)
        ON DELETE CASCADE
);
```

---
# Configuración del backend

Editar el archivo:

```
backend/src/main/resources/application.properties
```

Configurar el usuario y la contraseña de PostgreSQL(Docker) según tu equipo:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/symplifica-db
spring.datasource.username=postgres
spring.datasource.password=tu_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

# Ejecutar el backend

```bash
cd backend
./mvnw spring-boot:run
```

Disponible en:

```
http://localhost:8080
```

---

# Ejecutar el frontend

```bash
cd frontend
npm install
npm run dev
```

Disponible en:

```
http://localhost:5173
```

---

# Ejecutar backend y frontend simultáneamente

Desde la raíz del proyecto:

```bash
npm start
```

La aplicación estará disponible en:

```
http://localhost:5173
```

---

# Ejecutar las pruebas

```bash
cd backend
./mvnw test
```

---

# Ejecutar con Docker

El proyecto incluye un archivo `docker-compose.yml` que permite orquestar automáticamente los servicios del **Backend** y **Frontend**.

Para iniciar la aplicación, desde la raíz del proyecto ejecute:

```bash
docker compose up --build
```

Una vez finalice la construcción de las imágenes, los servicios estarán disponibles en:

- Frontend: http://localhost:5173
- Backend: http://localhost:8080

Al iniciar la aplicación desde frontend podrá crear un usuario desde la pantalla de registro e iniciar sesión inmediatamente.

> **Nota:** Para probar la funcionalidad de recuperación de contraseña es recomendable utilizar un correo electrónico válido, ya que el sistema enviará el enlace de recuperación mediante SMTP.

---

# Acceder a PostgreSQL desde el contenedor

```bash
docker exec -it symplifica-db psql -U symplifica -d symplifica-db
```

---

# Configuración de correo (SMTP) *(Adicional)*

Se agregó esta funcionalidad para permitir la recuperación de contraseña mediante correo electrónico.

Para ello se utilizan las columnas de la tabla users:

- `reset_token`
- `reset_token_expiry`

Estas almacenan el token de recuperación y la fecha de expiración del mismo.

Para utilizar esta funcionalidad es necesario generar una **App Password** desde una cuenta de Gmail y configurar las siguientes propiedades:

```properties
spring.mail.host=${MAIL_HOST:smtp.gmail.com}
spring.mail.port=${MAIL_PORT:587}
spring.mail.username=${MAIL_USERNAME:tu-correo@gmail.com}
spring.mail.password=${MAIL_PASSWORD:tu-app-password}

spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```