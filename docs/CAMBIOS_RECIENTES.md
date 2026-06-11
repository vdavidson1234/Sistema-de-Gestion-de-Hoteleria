# Cambios recientes del sistema hotelero

Este documento resume los cambios funcionales y técnicos más importantes para que el equipo pueda revisar el push sin tener que reconstruir toda la conversación.

## Arquitectura y persistencia

- Se implementó PostgreSQL en Docker como motor SQL compartido.
- La comunicación entre backend y base usa un puerto y un adapter:
  `HotelRepository` define el contrato, `HotelPersistence` lo implementa con JDBC/PostgreSQL y `Database` encapsula la conexión.
- La GUI no conoce JDBC, PostgreSQL ni clases de infraestructura. Usa `GestorPersistenciaHotelera`, que depende del puerto `HotelRepository`.
- `App` funciona como composition root: arma el adapter concreto y lo inyecta en la aplicación.
- `Database.initializeSchema()` crea tablas nuevas y agrega columnas faltantes con `ALTER TABLE ... ADD COLUMN IF NOT EXISTS`.

## Reservas, acompañantes y ocupación

- Una reserva puede incluir varias habitaciones bajo el mismo titular mediante `grupo_codigo`.
- Cada habitación reservada tiene sus propios ocupantes en `reserva_ocupantes`.
- El checkbox `Tiene acompañantes` abre automáticamente la carga de acompañantes.
- La cantidad de ocupantes ya no se carga como dato manual redundante: se calcula como titular + acompañantes registrados.
- La GUI valida que la capacidad seleccionada alcance para los ocupantes cargados.
- La lista de habitaciones disponibles en reservas se actualiza dinámicamente según fecha de ingreso, noches, estado operativo y reservas superpuestas.
- Las habitaciones en la lista de reserva se muestran de forma clara: número, tipo, capacidad, precio por noche y estado.

## Pagos, descuentos y políticas

- Para confirmar una reserva se exige una seña mínima del 25% del total de habitación por noches.
- La seña queda incluida en el total abonado de la estadía.
- Se separaron las pestañas de check-in y check-out/pagos.
- El combo de pagos solo muestra estadías con reserva `CONFIRMADA`; las reservas `FINALIZADA` ya no se pueden seleccionar para registrar pagos.
- La política tarifaria y el descuento aplicados a una estadía se persisten en la tabla `estadias` para que el histórico conserve el criterio usado.
- Los descuentos validan el tipo de cliente requerido:
  - `Cliente frecuente` habilita descuento de cliente frecuente.
  - `Promocional` habilita promoción especial.
  - `Convenio empresarial` habilita descuento de convenio empresarial.
- El tipo de cliente también afecta servicios:
  - `Empresario` o el valor legado `Corporativo`: cochera bonificada.
  - `Convenio empresarial`: cochera y desayuno bonificados.

## Habitaciones

- Los tipos de habitación tienen capacidad y precio base fijos por tipo. El precio se calcula por habitación completa, no por ocupantes reales.
- Al cargar habitaciones desde PostgreSQL se normalizan capacidad y precio según el tipo para corregir datos viejos inconsistentes.
- Estados operativos actuales:
  `DISPONIBLE`, `RESERVADA`, `OCUPADA`, `LIMPIEZA`, `MANTENIMIENTO`, `BLOQUEADA`.
- Confirmar una reserva marca la habitación como `RESERVADA`; el check-in la marca como `OCUPADA`.
- El cierre de la estadía libera la habitación.
- Se agregó baja lógica de habitaciones con `habitaciones.activa`.
- Si una habitación tiene reservas finalizadas/canceladas, se puede quitar del catálogo operativo sin borrar su histórico.
- Si se vuelve a registrar una habitación con el mismo número dado de baja, se reactiva el registro existente en lugar de crear otro y chocar con la clave primaria.

## Validaciones de datos

- Los nombres y apellidos no pueden contener números.
- El DNI debe ser numérico.
- El teléfono no puede contener letras.
- El email debe tener formato válido.
- Si una persona ya existe por DNI, la GUI permite cargar sus datos sin pedir todo de nuevo.

## Histórico y detalle

- Se agregó seguimiento histórico de reservas.
- El histórico muestra estado, estadía, política tarifaria, descuento, pagos registrados y total abonado.
- Haciendo doble clic sobre una fila de reservas, estadías o histórico se abre un detalle completo con titular, acompañantes, tipo de cliente, habitación, servicios y pagos.

## Documentación actualizada

- `README.md`: arquitectura, adapter, flujo actual y patrones.
- `docs/PERSISTENCE_POSTGRESQL.md`: guía de PostgreSQL, Docker, adapter y datos persistidos.
- `docs/schema.sql`: esquema PostgreSQL actual.
- `docs/er_diagram.svg`: diagrama entidad-relación alineado con el esquema actual.
