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

2.Crear tabla users



