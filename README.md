# Sistema de Gestión Hotelera

Proyecto Java orientado a objetos para una cadena hotelera.
La base implementa una demo funcional del flujo principal de reservas, estadías, servicios y pagos.

## Estructura

- `src/gestionhotelera/App.java`: punto de entrada de la demostración.
- `src/gestionhotelera/dominio`: entidades principales del negocio.
- `src/gestionhotelera/factory`: creación de habitaciones con Factory Method / Simple Factory.
- `src/gestionhotelera/decorator`: servicios adicionales de estadía con Decorator.
- `src/gestionhotelera/state`: estados de la reserva con State.
- `src/gestionhotelera/strategy`: políticas de precio y descuentos con Strategy.
- `src/gestionhotelera/pagos`: métodos de pago intercambiables.
- `src/gestionhotelera/control`: controladores GRASP y cálculo de costos.

## Patrones y principios aplicados

- Factory Method / Simple Factory: creación centralizada de habitaciones.
- Decorator: agregación flexible de desayuno, spa, cochera y lavandería.
- Strategy: políticas de precio y descuentos reemplazables.
- State: ciclo de vida de una reserva.
- SRP: cada clase mantiene una sola responsabilidad clara.
- OCP: nuevos tipos de habitación, servicios o descuentos se agregan con poco impacto.
- DIP: el calculador depende de interfaces, no de clases concretas.
- GRASP Controller, Creator e Information Expert: presentes en los gestores y entidades.

