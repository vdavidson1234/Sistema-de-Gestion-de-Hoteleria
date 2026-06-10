# Sistema de Gestion Hotelera

Proyecto Java orientado a objetos para una cadena hotelera. La base implementa una demo funcional del flujo principal de reservas, estadias, servicios y pagos.

## Estructura

- `src/gestionhotelera/App.java`: punto de entrada de la aplicacion.
- `src/gestionhotelera/ui`: interfaz Swing.
- `src/gestionhotelera/dominio`: entidades principales del negocio.
- `src/gestionhotelera/control`: controladores GRASP y calculo de costos.
- `src/gestionhotelera/control/HotelRepository.java`: puerto de persistencia usado por la aplicacion.
- `src/gestionhotelera/control/GestorPersistenciaHotelera.java`: gestor que aisla la UI de la infraestructura SQL.
- `src/gestionhotelera/factory`: creacion de habitaciones.
- `src/gestionhotelera/decorator`: servicios adicionales de estadia.
- `src/gestionhotelera/state`: estados de la reserva.
- `src/gestionhotelera/strategy`: politicas de precio y descuentos.
- `src/gestionhotelera/pagos`: metodos de pago intercambiables.
- `src/gestionhotelera/persistence`: adapter JDBC PostgreSQL que implementa el puerto de persistencia.
- `docs/schema.sql`: esquema SQL de PostgreSQL.
- `docker-compose.yml`: base PostgreSQL lista para Docker.
- `pom.xml`: configuracion Maven y dependencia del driver JDBC de PostgreSQL.

## Compilar y ejecutar

El proyecto usa Maven para resolver dependencias, incluido el driver JDBC de PostgreSQL.

```powershell
mvn compile
mvn exec:java
```

Para ejecutar la demo por consola:

```powershell
mvn exec:java -Dexec.args=demo
```

## Base de datos

El motor recomendado e implementado es PostgreSQL 16 en Docker. Para levantarlo:

```powershell
docker compose up -d
```

Credenciales por defecto:

- Base: `hotel_db`
- Usuario: `hotel_user`
- Password: `hotel_password`
- JDBC URL: `jdbc:postgresql://localhost:5432/hotel_db`

La guia completa esta en `docs/PERSISTENCE_POSTGRESQL.md`.

## Patrones y principios aplicados

- Factory Method / Simple Factory: creacion centralizada de habitaciones.
- Decorator: agregacion flexible de desayuno, spa, cochera y lavanderia.
- Strategy: politicas de precio y descuentos reemplazables.
- State: ciclo de vida de una reserva.
- SRP: cada clase mantiene una sola responsabilidad clara.
- OCP: nuevos tipos de habitacion, servicios o descuentos se agregan con poco impacto.
- DIP: el calculador depende de interfaces, no de clases concretas.
- GRASP Controller, Creator e Information Expert: presentes en los gestores y entidades.
