Persistencia PostgreSQL en Docker

Este proyecto usa PostgreSQL como motor SQL compartido. La aplicacion se comunica con la base usando un puerto y un adapter JDBC:

`HotelGUI` -> `GestorPersistenciaHotelera` -> `HotelRepository` -> `HotelPersistence` -> `Database` -> PostgreSQL JDBC -> contenedor Docker.

La GUI no instancia ni conoce `Database`, `HotelPersistence`, JDBC ni PostgreSQL. `App` funciona como composition root: arma el adapter concreto y se lo entrega al gestor de persistencia.

## Levantar la base

Desde la raiz del proyecto:

```powershell
docker compose up -d
docker compose ps
```

El contenedor publica PostgreSQL en `localhost:5432` y crea la base con `docs/schema.sql` la primera vez que arranca el volumen.

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

## Ejecutar la aplicacion con Maven

El proyecto declara el driver JDBC de PostgreSQL en `pom.xml`, asi que Maven lo descarga y lo agrega al classpath automaticamente.

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

Si la aplicacion se ejecuta desde otra maquina, cambiar `localhost` por la IP o hostname de la maquina donde corre Docker:

```powershell
$env:DB_URL = 'jdbc:postgresql://192.168.1.50:5432/hotel_db'
```

## Configuracion soportada

El adapter PostgreSQL usa `Database`, que lee estas variables de entorno:

- `DB_URL`
- `DB_USER`
- `DB_PASSWORD`

Tambien acepta propiedades de Java equivalentes:

- `hotel.db.url`
- `hotel.db.user`
- `hotel.db.password`

Si no se configuran, usa los valores por defecto del `docker-compose.yml`.

## Que se persiste

El puerto `HotelRepository` define las operaciones de carga/guardado. El adapter `HotelPersistence` implementa ese puerto y guarda/carga:

- habitaciones
- huespedes
- reservas
- estadias
- servicios consumidos
- pagos

`GestorPersistenciaHotelera` intenta cargar el estado al iniciar. Si el contenedor o el driver JDBC no estan disponibles, informa el problema a la GUI y sigue funcionando en memoria para no bloquear la demo.

## Archivos principales

- `docker-compose.yml`: servicio PostgreSQL y volumen persistente.
- `pom.xml`: dependencia `org.postgresql:postgresql` y configuracion de compilacion.
- `docs/schema.sql`: esquema PostgreSQL inicial.
- `src/gestionhotelera/control/HotelRepository.java`: puerto de persistencia.
- `src/gestionhotelera/control/GestorPersistenciaHotelera.java`: controlador de aplicacion que usa el puerto.
- `src/gestionhotelera/persistence/Database.java`: conexion JDBC configurable.
- `src/gestionhotelera/persistence/HotelPersistence.java`: adapter PostgreSQL entre el puerto y SQL.
