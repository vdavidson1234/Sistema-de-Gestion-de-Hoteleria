# Persistencia PostgreSQL en Docker

Este proyecto usa PostgreSQL como motor SQL compartido. La aplicación se comunica con la base usando un puerto y un adapter JDBC:

`HotelGUI` -> `GestorPersistenciaHotelera` -> `HotelRepository` -> `HotelPersistence` -> `Database` -> PostgreSQL JDBC -> contenedor Docker.

La GUI no instancia ni conoce `Database`, `HotelPersistence`, JDBC ni PostgreSQL. `App` funciona como composition root: arma el adapter concreto y se lo entrega al gestor de persistencia. Este diseño aplica Adapter y DIP: el backend trabaja contra el puerto `HotelRepository`, mientras `HotelPersistence` traduce entre dominio y SQL.

## Levantar la base

Desde la raíz del proyecto:

```powershell
docker compose up -d
docker compose ps
```

El contenedor publica PostgreSQL en `localhost:5432` y crea la base con `docs/schema.sql` la primera vez que arranca el volumen.
Si el volumen ya existía, `Database.initializeSchema()` agrega las columnas/tablas nuevas necesarias al iniciar la aplicación.

Credenciales por defecto:

- Base: `hotel_db`
- Usuario: `hotel_user`
- Password: `hotel_password`
- JDBC URL local: `jdbc:postgresql://localhost:5432/hotel_db`

Para reiniciar desde cero, detener y borrar el volumen:

```powershell
docker compose down -v
docker compose up -d
```

## Ejecutar la aplicación con Maven

El proyecto declara el driver JDBC de PostgreSQL en `pom.xml`, así que Maven lo descarga y lo agrega al classpath automáticamente.

Ejemplo por PowerShell:

```powershell
$env:DB_URL = 'jdbc:postgresql://localhost:5432/hotel_db'
$env:DB_USER = 'hotel_user'
$env:DB_PASSWORD = 'hotel_password'

mvn compile
mvn exec:java
```

Para ejecutar la demo por consola:

```powershell
mvn exec:java -Dexec.args=demo
```

Si la aplicación se ejecuta desde otra máquina, cambiar `localhost` por la IP o hostname de la máquina donde corre Docker:

```powershell
$env:DB_URL = 'jdbc:postgresql://192.168.1.50:5432/hotel_db'
```

## Configuración soportada

El adapter PostgreSQL usa `Database`, que lee estas variables de entorno:

- `DB_URL`
- `DB_USER`
- `DB_PASSWORD`

También acepta propiedades de Java equivalentes:

- `hotel.db.url`
- `hotel.db.user`
- `hotel.db.password`

Si no se configuran, usa los valores por defecto del `docker-compose.yml`.

## Qué se persiste

El puerto `HotelRepository` define las operaciones de carga/guardado. El adapter `HotelPersistence` implementa ese puerto y guarda/carga:

- habitaciones
- baja lógica de habitaciones mediante `habitaciones.activa`, conservando reservas históricas
- estado operativo de habitaciones (`DISPONIBLE`, `RESERVADA`, `OCUPADA`, `LIMPIEZA`, `MANTENIMIENTO`, `BLOQUEADA`)
- normalización de capacidad y precio de habitaciones según `TipoHabitacion` al cargar datos legados
- huéspedes
- reservas
- grupos de reserva por `grupo_codigo`
- ocupantes de cada habitación reservada en `reserva_ocupantes`
- seña requerida, seña pagada y método de seña
- estadias
- política tarifaria y descuento aplicados a cada estadía, para histórico
- servicios consumidos con cantidad y precio unitario
- pagos

`GestorPersistenciaHotelera` intenta cargar el estado al iniciar. Si el contenedor o el driver JDBC no están disponibles, informa el problema a la GUI y sigue funcionando en memoria para no bloquear la demo.

## Reglas relevantes de persistencia

- `habitaciones.numero` sigue siendo clave primaria.
- Si una habitación se elimina sin historial, se borra del catálogo en memoria y se sincroniza con la base.
- Si tiene reservas finalizadas o canceladas, se conserva la fila y se marca `activa=false` para no romper claves foráneas históricas.
- Si luego se registra una habitación con el mismo número y mismo tipo, se reactiva el registro existente.
- Las habitaciones inactivas no aparecen en el catálogo operativo ni en la selección de nuevas reservas.
- Las reservas finalizadas no aparecen en el combo de pagos.
- Las columnas de política/descuento en `estadias` preservan el criterio comercial usado al calcular o cerrar una estadía.
- Al cargar habitaciones, el dominio corrige capacidad y precio base según el tipo actual para evitar datos viejos inconsistentes.

## Archivos principales

- `docker-compose.yml`: servicio PostgreSQL y volumen persistente.
- `pom.xml`: dependencia `org.postgresql:postgresql` y configuración de compilación.
- `docs/schema.sql`: esquema PostgreSQL inicial.
- `docs/er_diagram.svg`: diagrama entidad-relación del esquema PostgreSQL actual.
- `src/gestionhotelera/control/HotelRepository.java`: puerto de persistencia.
- `src/gestionhotelera/control/GestorPersistenciaHotelera.java`: controlador de aplicación que usa el puerto.
- `src/gestionhotelera/persistence/Database.java`: conexión JDBC configurable.
- `src/gestionhotelera/persistence/HotelPersistence.java`: adapter PostgreSQL entre el puerto y SQL.
