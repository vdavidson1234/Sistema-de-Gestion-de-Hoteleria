# Sistema de Gestión Hotelera y Servicios para Huéspedes
## Integrantes del grupo

* **Victoria Davidson** - Legajo: 1181369
* **Juan Cruz Borrás** - Legajo: 1186835
* **Lola Cuartas** - Legajo: 1190036

## Descripción breve del sistema

El Sistema de Gestión Hotelera y Servicios para Huéspedes es una aplicación desarrollada en Java con enfoque orientado a objetos. Su objetivo principal es centralizar y organizar los procesos más importantes de la administración hotelera, incluyendo habitaciones, huéspedes, reservas, acompañantes, estadías, servicios adicionales, pagos, descuentos y cierre de check-out.

El sistema permite registrar habitaciones y huéspedes, consultar disponibilidad, crear reservas individuales o grupales, agregar ocupantes por habitación, confirmar reservas mediante una seña mínima del 25%, iniciar estadías, cargar servicios adicionales, calcular el costo total, aplicar políticas de precio y descuentos, registrar pagos, calcular saldos pendientes y finalizar estadías liberando la habitación correspondiente.

La aplicación cuenta con una interfaz gráfica desarrollada con Java Swing y utiliza persistencia de datos mediante PostgreSQL en Docker. Esto permite guardar y recuperar información entre distintas ejecuciones del programa, evitando que los datos dependan únicamente de la memoria de ejecución.

## Funcionalidades principales

* Registro de huéspedes.
* Búsqueda de huéspedes por DNI.
* Validación de datos personales de huéspedes y acompañantes.
* Administración de habitaciones.
* Manejo de estados de habitación: disponible, reservada, ocupada, limpieza, mantenimiento y bloqueada.
* Consulta de disponibilidad según fechas y cantidad de personas.
* Creación de reservas individuales.
* Creación de reservas grupales con varias habitaciones.
* Registro de ocupantes por habitación.
* Confirmación de reservas mediante seña mínima del 25%.
* Cancelación de reservas.
* Extensión de reservas.
* Registro de estadías a partir de reservas confirmadas.
* Carga de servicios adicionales como desayuno, spa, lavandería y cochera.
* Cálculo del costo total de una estadía.
* Aplicación de políticas tarifarias.
* Aplicación de descuentos.
* Registro de pagos.
* Selección de métodos de pago.
* Cálculo de saldo pendiente.
* Finalización de check-out.
* Consulta de reservas e histórico.
* Persistencia de datos con PostgreSQL.

## Estructura del proyecto

El proyecto se encuentra organizado en paquetes para separar responsabilidades y facilitar el mantenimiento del código.

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

## Base de datos

El sistema utiliza PostgreSQL como motor de base de datos. La base se ejecuta mediante Docker, utilizando el archivo `docker-compose.yml`.

La persistencia permite guardar y recuperar información relacionada con:

* Habitaciones.
* Huéspedes.
* Reservas.
* Ocupantes.
* Estadías.
* Servicios adicionales.
* Pagos.

La comunicación entre Java y PostgreSQL se realiza mediante JDBC.

La capa de persistencia se encuentra separada del resto del sistema para evitar que la interfaz gráfica o las clases del dominio dependan directamente de consultas SQL.

Clases principales relacionadas con persistencia:

* `HotelRepository`: define el contrato de persistencia utilizado por la aplicación.
* `GestorPersistenciaHotelera`: coordina la carga y guardado de datos.
* `Database`: encapsula la conexión con PostgreSQL.
* `HotelPersistence`: adapta los objetos del sistema al modelo relacional de la base de datos.

## Patrones de diseño aplicados

### Factory Method / Simple Factory

Se aplica mediante la clase `HabitacionFactory`.

Este patrón centraliza la creación de habitaciones, evitando que la lógica de construcción quede repetida en distintas partes del sistema.

Clases relacionadas:

* `HabitacionFactory`
* `Habitacion`
* `TipoHabitacion`
* `GestorHabitaciones`

### Strategy para políticas de precio y descuentos

Se aplica en el paquete `strategy`.

Permite cambiar la forma de calcular precios y descuentos sin modificar la clase principal encargada del cálculo.

Clases relacionadas con políticas de precio:

* `PoliticaPrecio`
* `PoliticaPrecioTemporadaAlta`
* `PoliticaPrecioTemporadaMedia`
* `PoliticaPrecioTemporadaBaja`

Clases relacionadas con descuentos:

* `DescuentoStrategy`
* `DescuentoSinDescuento`
* `DescuentoClienteFrecuente`
* `DescuentoPromocionEspecial`
* `DescuentoConvenioEmpresarial`

### Strategy para métodos de pago

Se aplica en el paquete `pagos`.

Permite procesar distintos métodos de pago bajo una misma estructura.

Clases relacionadas:

* `MetodoPago`
* `PagoEfectivo`
* `PagoTarjeta`
* `PagoTransferencia`
* `PagoOnlineSimulado`

### State

Se aplica para manejar el ciclo de vida de una reserva.

Una reserva puede estar pendiente, confirmada, cancelada o finalizada. Cada estado define qué acciones son válidas.

Clases relacionadas:

* `EstadoReservaComportamiento`
* `ReservaPendienteState`
* `ReservaConfirmadaState`
* `ReservaCanceladaState`
* `ReservaFinalizadaState`
* `Reserva`

### Decorator

Se aplica para representar servicios adicionales de forma flexible.

Permite agregar servicios como desayuno, spa, lavandería o cochera sin modificar directamente la estructura base.

Clases relacionadas:

* `ServicioBase`
* `ServicioDecorator`
* `DesayunoDecorator`
* `SpaDecorator`
* `LavanderiaDecorator`
* `CocheraDecorator`

### Adapter

Se aplica en la capa de persistencia.

Permite adaptar los objetos Java del sistema al modelo relacional de PostgreSQL, separando la lógica del negocio de los detalles técnicos de la base de datos.

Clases relacionadas:

* `HotelRepository`
* `HotelPersistence`
* `Database`
* `GestorPersistenciaHotelera`

## Principios SOLID aplicados

### SRP - Single Responsibility Principle

Cada clase tiene una responsabilidad principal clara.

Ejemplos:

* `Huesped` representa los datos de una persona.
* `Habitacion` representa la información y estado de una habitación.
* `Reserva` representa una reserva.
* `Estadia` representa la estadía real del huésped.
* `CalculadorCosto` se encarga del cálculo económico.
* `ValidadorDatosPersonales` valida datos de huéspedes y acompañantes.
* `GestorReservas` coordina operaciones de reserva.

Esta separación permite que el sistema sea más fácil de mantener.

### OCP - Open/Closed Principle

El sistema está abierto a extensión y cerrado a modificación.

Ejemplos:

* Se pueden agregar nuevas políticas de precio implementando `PoliticaPrecio`.
* Se pueden agregar nuevos descuentos implementando `DescuentoStrategy`.
* Se pueden agregar nuevos métodos de pago implementando `MetodoPago`.
* Se pueden agregar nuevos estados de reserva respetando la estructura de `EstadoReservaComportamiento`.

Esto permite ampliar el sistema sin modificar toda la lógica existente.

### LSP - Liskov Substitution Principle

Las implementaciones pueden reemplazarse entre sí sin romper el funcionamiento del sistema.

Ejemplos:

* Cualquier clase que implemente `MetodoPago` puede utilizarse como método de pago.
* Cualquier clase que implemente `PoliticaPrecio` puede utilizarse para calcular precios.
* Cualquier clase que implemente `DescuentoStrategy` puede aplicarse como descuento.
* Los estados de reserva pueden intercambiarse mediante `EstadoReservaComportamiento`.

### ISP - Interface Segregation Principle

El sistema utiliza interfaces específicas para responsabilidades concretas.

Ejemplos:

* `MetodoPago` se ocupa solamente del comportamiento de pago.
* `PoliticaPrecio` se ocupa solamente del cálculo de precio.
* `DescuentoStrategy` se ocupa solamente de aplicar descuentos.
* `EstadoReservaComportamiento` se ocupa del comportamiento de los estados de reserva.

Esto evita interfaces demasiado grandes o con métodos innecesarios.

### DIP - Dependency Inversion Principle

Las clases principales dependen de abstracciones y no de clases concretas.

Ejemplos:

* `CalculadorCosto` trabaja con `PoliticaPrecio` y `DescuentoStrategy`.
* `Pago` trabaja con `MetodoPago`.
* La persistencia se organiza mediante `HotelRepository`, evitando que la lógica principal dependa directamente de PostgreSQL o JDBC.

Esto reduce el acoplamiento y facilita cambios futuros.

## Patrones GRASP aplicados

### Controller

Se aplica en las clases gestoras, que coordinan las operaciones principales del sistema.

Ejemplos:

* `GestorReservas`
* `GestorHabitaciones`
* `GestorEstadias`
* `GestorPagos`
* `GestorPersistenciaHotelera`

Estas clases reciben solicitudes desde la interfaz y coordinan la lógica del sistema.

### Creator

Se aplica asignando la creación de objetos a las clases que poseen la información necesaria.

Ejemplos:

* `GestorReservas` crea reservas.
* `GestorEstadias` crea estadías.
* `GestorPagos` crea pagos.
* `HabitacionFactory` crea habitaciones.

### Information Expert

Se aplica cuando una responsabilidad se asigna a la clase que posee la información necesaria.

Ejemplos:

* `Hotel` conoce habitaciones, huéspedes y reservas.
* `Habitacion` conoce su estado y capacidad.
* `Reserva` conoce fechas, ocupantes y seña.
* `Estadia` conoce servicios y pagos.
* `Pago` conoce monto y método de pago.

### Low Coupling

El sistema busca reducir dependencias innecesarias.

Ejemplos:

* `CalculadorCosto` depende de interfaces y no de descuentos concretos.
* `Pago` depende de `MetodoPago`.
* `HotelGUI` delega operaciones en gestores.
* La persistencia se encuentra separada de la interfaz y del dominio.

### High Cohesion

Cada paquete y clase agrupa responsabilidades relacionadas.

Ejemplos:

* `dominio`: entidades principales.
* `control`: coordinación de casos de uso.
* `strategy`: políticas y descuentos.
* `pagos`: métodos de pago.
* `state`: estados de reserva.
* `persistence`: acceso a base de datos.

### Polymorphism

Se utiliza para manejar comportamientos variables sin usar condicionales excesivos.

Ejemplos:

* Políticas de precio.
* Descuentos.
* Métodos de pago.
* Estados de reserva.

### Pure Fabrication

Se crean clases auxiliares que no representan objetos reales del hotel, pero ayudan a mantener el sistema ordenado.

Ejemplos:

* `CalculadorCosto`
* `ValidadorDisponibilidad`
* `ValidadorDatosPersonales`
* `ReglasCliente`
* `NotificadorReserva`

### Indirection

Se utilizan clases intermedias para reducir el acoplamiento.

Ejemplos:

* Los gestores actúan entre la interfaz y el dominio.
* La capa de persistencia actúa entre la aplicación y PostgreSQL.

### Protected Variations

El sistema protege las partes que pueden variar en el futuro.

Ejemplos:

* Políticas de precio mediante `PoliticaPrecio`.
* Descuentos mediante `DescuentoStrategy`.
* Métodos de pago mediante `MetodoPago`.
* Estados de reserva mediante `EstadoReservaComportamiento`.
* Persistencia mediante `HotelRepository`.

## Distribución de tareas

### Victoria Davidson

Durante el desarrollo del proyecto participé en el análisis general del sistema, la organización del trabajo y la elaboración de la documentación. Me enfoqué en comprender el funcionamiento completo del Sistema de Gestión Hotelera y Servicios para Huéspedes, relacionando los requerimientos, los casos de uso, las clases principales, los patrones de diseño y la implementación realizada.

También participé en la revisión de las funcionalidades principales del sistema, como la gestión de habitaciones, el registro de huéspedes, la creación de reservas individuales y grupales, la carga de acompañantes, la confirmación de reservas mediante seña del 25%, el registro de estadías, la carga de servicios adicionales, el cálculo de descuentos, el registro de pagos, el saldo pendiente y el check-out.

Además, trabajé en la redacción y organización de los informes, incluyendo la introducción, descripción del problema, objetivos, requisitos funcionales, casos de uso, descripción de diagramas de secuencia, arquitectura del sistema, patrones aplicados, principios SOLID, patrones GRASP y explicación de las clases principales.

### Juan Cruz Borrás

Durante el desarrollo del proyecto participé en la parte relacionada con la base de datos y la persistencia del sistema. Mi aporte estuvo enfocado en analizar qué información debía guardarse y cómo debía organizarse para que el sistema pudiera conservar los datos entre distintas ejecuciones.

Trabajé sobre la estructura de persistencia vinculada con habitaciones, huéspedes, reservas, ocupantes, estadías, servicios adicionales y pagos. También participé en la conexión entre el proyecto Java y la base de datos PostgreSQL, utilizando Docker para levantar el motor de base de datos y JDBC para permitir la comunicación desde la aplicación.

Además, colaboré en la revisión de la arquitectura de persistencia, teniendo en cuenta que la interfaz gráfica y las clases principales del dominio no debían depender directamente de consultas SQL. Por eso, la persistencia se organizó mediante clases específicas que permiten guardar y recuperar información sin mezclar responsabilidades.

Mi aporte principal fue fortalecer la parte de datos del sistema, permitiendo que la aplicación no funcione solamente en memoria, sino que pueda acercarse más a un sistema real de gestión hotelera con información persistente.

### Lola Cuartas

Durante el desarrollo del proyecto participé en el seguimiento general del trabajo, la revisión de documentación y la organización del material final.

Colaboré en la revisión de secciones relacionadas con los casos de uso, las clases principales, los patrones aplicados y la descripción general del sistema. También participé en la lectura y control del informe para que mantuviera coherencia con las fases anteriores del trabajo y con las funcionalidades implementadas.

Además, acompañé la revisión del funcionamiento general del sistema, especialmente en lo relacionado con habitaciones, huéspedes, reservas, estadías, servicios adicionales, pagos y check-out. Esto me permitió comprender mejor cómo se relacionan las distintas partes del proyecto.


## Conclusión

El Sistema de Gestión Hotelera y Servicios para Huéspedes permite resolver una problemática concreta de administración hotelera mediante una aplicación Java orientada a objetos.

El proyecto integra interfaz gráfica, persistencia con PostgreSQL, validaciones, gestión de reservas, servicios adicionales, pagos y cierre de estadías. Además, aplica patrones de diseño, principios SOLID y patrones GRASP para lograr una estructura más clara, mantenible y extensible.

Gracias a esta organización, el sistema puede ampliarse en el futuro incorporando nuevos tipos de habitaciones, nuevos servicios, nuevas promociones, nuevos descuentos, nuevos métodos de pago o nuevas integraciones externas.
