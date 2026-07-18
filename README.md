# Symplifica Dashboard

Prueba técnica Fullstack MID — Dashboard de empleados con gestión de beneficios
y enriquecimiento de ubicación vía la API de Nominatim (OpenStreetMap).

## Stack
- Backend: Java 17+, Spring Boot, Spring Security (JWT), JPA/Hibernate
- Frontend: VueJS 3, Vue Router, Pinia
- Base de datos: PostgreSQL corriendo mediante docker
- Integración externa: Nominatim (OpenStreetMap), formato XML

## Configuración de base de datos
1. Crear la base de datos:
   \`\`\`sql
   CREATE DATABASE symplifica-db;
   \`\`\`

2. Crear la tabla users:
   \`\`\`sql
   CREATE TABLE users (
      iduser      BIGSERIAL PRIMARY KEY,
      email       VARCHAR(150) NOT NULL UNIQUE,
      password    VARCHAR(255) NOT NULL,
      rol         VARCHAR(50) NOT NULL
   );
   \`\`\`

3. Crear la tabla empleados:
   \`\`\`sql
   CREATE TABLE empleados (
    id              BIGSERIAL PRIMARY KEY,
    nombre          VARCHAR(100) NOT NULL,
    apellido        VARCHAR(100) NOT NULL,
    email           VARCHAR(150) NOT NULL UNIQUE,
    cargo           VARCHAR(100),
    ciudad          VARCHAR(100) NOT NULL,
    direccion       VARCHAR(250),
    fecha_ingreso   DATE,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
   \`\`\`

4. Crear la tabla beneficios:
   \`\`\`sql
   CREATE TABLE beneficios (
    id                  BIGSERIAL PRIMARY KEY,
    monto               NUMERIC(12,2) NOT NULL,
    nombre_beneficio    VARCHAR(150) NOT NULL,
    id_empleado         BIGINT NOT NULL,

    CONSTRAINT fk_beneficio_empleado
        FOREIGN KEY (id_empleado)
        REFERENCES empleados(id)
        ON DELETE CASCADE
   );
    \`\`\`
   
5. Configurar(en este apartado debe colocar usuario y clave propia de su PC)
   `src/main/resources/application.properties`:
   \`\`\`properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/symplifica-db
   spring.datasource.username=postgres
   spring.datasource.password=tu_password
   jwt.secret=cambia-esto-en-produccion
   jwt.expiration-ms=86400000
   \`\`\`

## Ejecutar el backend
\`\`\`bash
cd backend
./mvnw spring-boot:run
\`\`\`
API disponible en `http://localhost:8080`

## Ejecutar el frontend
\`\`\`bash
cd frontend
npm install
npm run dev
\`\`\`
App disponible en `http://localhost:5173`

## Ejecutar el backend y el frontend al mismo tiempo(desde la raiz del proyecto)
\`\`\`bash
npm start
\`\`\`

## Ejecutar tests
\`\`\`bash
cd backend
./mvnw test
\`\`\`

## Ejecutar con Docker
\`\`\`bash
docker compose up --build
\`\`\`
- Frontend: http://localhost:5173
- Backend: http://localhost:8080

## ingresar al contenedor desde el CMD
\`\`\`bash
docker exec -it symplifica-db psql -U symplifica -d symplifica-db
\`\`\`

## Configuración de correo (SMTP)
\`\`\`properties
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=tu-correo@gmail.com
MAIL_PASSWORD=tu-app-password
\`\`\`