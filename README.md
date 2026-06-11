# Sistema de Gestión Hotelera

Proyecto Java orientado a objetos para una cadena hotelera. La aplicación implementa un flujo funcional de reservas, estadías, servicios, pagos, histórico y persistencia PostgreSQL en Docker.

## Estructura

- `src/gestionhotelera/App.java`: punto de entrada de la aplicación.
- `src/gestionhotelera/ui`: interfaz Swing.
- `src/gestionhotelera/dominio`: entidades principales del negocio.
- `src/gestionhotelera/control`: controladores GRASP, cálculo de costos, reglas comerciales y validaciones.
- `src/gestionhotelera/control/HotelRepository.java`: puerto de persistencia usado por la aplicación.
- `src/gestionhotelera/control/GestorPersistenciaHotelera.java`: gestor que aísla la UI de la infraestructura SQL.
- `src/gestionhotelera/factory`: creación de habitaciones.
- `src/gestionhotelera/decorator`: servicios adicionales de estadía.
- `src/gestionhotelera/state`: estados de la reserva.
- `src/gestionhotelera/strategy`: políticas de precio y descuentos.
- `src/gestionhotelera/pagos`: métodos de pago intercambiables.
- `src/gestionhotelera/persistence`: adapter JDBC PostgreSQL que implementa el puerto de persistencia.
- `docs/schema.sql`: esquema SQL de PostgreSQL.
- `docs/er_diagram.svg`: diagrama entidad-relación alineado al esquema PostgreSQL.
- `docs/CAMBIOS_RECIENTES.md`: resumen de cambios funcionales y técnicos para el equipo.
- `docker-compose.yml`: base PostgreSQL lista para Docker.
- `pom.xml`: configuración Maven y dependencia del driver JDBC de PostgreSQL.

## Compilar y ejecutar

El proyecto usa Maven para resolver dependencias,  incluido el driver JDBC de PostgreSQL.

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

La guía completa está en `docs/PERSISTENCE_POSTGRESQL.md`.

La comunicación con PostgreSQL aplica el patrón Adapter:

- `HotelRepository` es el puerto usado por la aplicación.
- `HotelPersistence` es el adapter JDBC PostgreSQL.
- `Database` encapsula conexión e inicialización de esquema.
- `GestorPersistenciaHotelera` coordina carga/guardado sin exponer SQL a la GUI.
- `HotelGUI` no conoce PostgreSQL, JDBC ni el driver.

## Flujo de reservas

- Una reserva grupal puede incluir varias habitaciones bajo el mismo titular.
- Cada habitación reservada registra sus ocupantes.
- Cada tipo de habitación tiene capacidad y precio base fijo por noche. El precio se calcula por habitación completa, no por ocupantes reales.
- Tipos cargados: simple, doble, triple, familiar, suite, lujo y penthouse.
- Para confirmar se exige una seña mínima del 25% del total de habitación por noches.
- La seña queda incluida como importe abonado al calcular el saldo de la estadía.
- La reserva puede extenderse si la habitación no tiene otra reserva activa en los días solicitados.
- El cierre de pago permite finalizar la estadía y liberar la habitación.
- El combo de pagos solo permite seleccionar estadías activas con reserva confirmada.
- La GUI incluye seguimiento histórico de reservas con estado, estadía, política tarifaria, descuento y pagos.
- Las habitaciones manejan estados operativos reales: disponible, reservada, ocupada, limpieza, mantenimiento y bloqueada.
- Las habitaciones con histórico pueden darse de baja lógica mediante `activa=false` sin perder reservas finalizadas.
- Una habitación dada de baja puede reactivarse registrando nuevamente el mismo número y el mismo tipo.
- Al cargar datos viejos, capacidad y precio se normalizan según el tipo de habitación para evitar inconsistencias.
- La GUI permite buscar huéspedes por DNI para reutilizar datos ya registrados.
- La GUI valida nombres, DNI, teléfono y email antes de registrar huéspedes u ocupantes.
- Los servicios tienen precio unitario fijo y cantidad.
- El tipo de cliente afecta servicios bonificados: empresario obtiene cochera gratis; convenio empresarial obtiene cochera y desayuno gratis.
- Las políticas tarifarias son temporada media, alta y baja.
- Los descuentos disponibles son cliente frecuente, promoción especial y convenio empresarial.
- La política tarifaria y el descuento aplicados se guardan en la estadía para conservar el histórico.

## Patrones y principios aplicados

- Factory Method / Simple Factory: creación centralizada de habitaciones.
- Adapter: `HotelPersistence` adapta el puerto `HotelRepository` a PostgreSQL/JDBC.
- Decorator: agregación flexible de desayuno, spa, cochera y lavandería.
- Strategy: políticas de precio y descuentos reemplazables.
- State: ciclo de vida de una reserva.
- SRP: cada clase mantiene una sola responsabilidad clara.
- OCP: nuevos tipos de habitación, servicios o descuentos se agregan con poco impacto.
- DIP: el calculador depende de interfaces, no de clases concretas.
- GRASP Controller, Creator e Information Expert: presentes en los gestores y entidades.
