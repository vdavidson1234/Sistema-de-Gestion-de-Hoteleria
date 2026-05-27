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

## Ejecución

Compilar y ejecutar desde la raíz del proyecto:

```powershell
javac -d bin (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
java -cp bin gestionhotelera.App
```

La demo crea habitaciones, registra un huésped, confirma una reserva, abre una estadía, agrega servicios y calcula el costo final con pago parcial y cierre de la estadía.
## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).
