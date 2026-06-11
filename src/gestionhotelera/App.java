package gestionhotelera;

import gestionhotelera.control.GestorEstadias;
import gestionhotelera.control.GestorHabitaciones;
import gestionhotelera.control.GestorPagos;
import gestionhotelera.control.GestorPersistenciaHotelera;
import gestionhotelera.control.GestorReservas;
import gestionhotelera.decorator.DesayunoDecorator;
import gestionhotelera.decorator.LavanderiaDecorator;
import gestionhotelera.decorator.ServicioBase;
import gestionhotelera.decorator.SpaDecorator;
import gestionhotelera.dominio.Habitacion;
import gestionhotelera.dominio.Hotel;
import gestionhotelera.dominio.Huesped;
import gestionhotelera.dominio.Reserva;
import gestionhotelera.dominio.ServicioEstadia;
import gestionhotelera.dominio.StaySummary;
import gestionhotelera.dominio.TipoHabitacion;
import gestionhotelera.factory.HabitacionFactory;
import gestionhotelera.pagos.PagoTarjeta;
import gestionhotelera.persistence.Database;
import gestionhotelera.persistence.HotelPersistence;
import gestionhotelera.strategy.DescuentoClienteFrecuente;
import gestionhotelera.strategy.PoliticaPrecioTemporadaMedia;
import gestionhotelera.ui.HotelGUI;
import java.time.LocalDate;

/**
 * Punto de entrada de la aplicación.
 * Este programa crea un escenario de demostración del sistema hotelero
 * y muestra cómo se conectan los patrones y principios del proyecto.
 */
public class App {

    /**
     * Ejecuta la aplicacion.
     *
     * @param args argumentos de línea de comandos, no utilizados en esta demo
     */
    public static void main(String[] args) {
        if (args.length > 0 && "demo".equalsIgnoreCase(args[0])) {
            ejecutarDemo();
            return;
        }

        HotelGUI.mostrar(crearGestorPersistencia());
    }

    private static GestorPersistenciaHotelera crearGestorPersistencia() {
        return new GestorPersistenciaHotelera(new HotelPersistence(Database.fromEnvironment()));
    }

    private static void ejecutarDemo() {
        Hotel hotel = new Hotel("Hotel Aurora", "Av. Central 123");
        HabitacionFactory habitacionFactory = new HabitacionFactory();
        GestorHabitaciones gestorHabitaciones = new GestorHabitaciones(hotel, habitacionFactory);
        GestorReservas gestorReservas = new GestorReservas(hotel);
        GestorEstadias gestorEstadias = new GestorEstadias();
        GestorPagos gestorPagos = new GestorPagos();

        Habitacion habitacionSimple = gestorHabitaciones.crearYRegistrarHabitacion(101,
                TipoHabitacion.SIMPLE.getCapacidadEstandar(), TipoHabitacion.SIMPLE.getPrecioBase(), TipoHabitacion.SIMPLE);
        Habitacion habitacionDoble = gestorHabitaciones.crearYRegistrarHabitacion(202,
                TipoHabitacion.DOBLE.getCapacidadEstandar(), TipoHabitacion.DOBLE.getPrecioBase(), TipoHabitacion.DOBLE);
        gestorHabitaciones.crearYRegistrarHabitacion(303,
                TipoHabitacion.SUITE.getCapacidadEstandar(), TipoHabitacion.SUITE.getPrecioBase(), TipoHabitacion.SUITE);

        Huesped huesped = new Huesped("Ana", "Pereyra", "12345678", "3412345678", "ana@mail.com", "Cliente frecuente");

        LocalDate ingreso = LocalDate.now().plusDays(1);
        LocalDate egreso = ingreso.plusDays(3);

        Reserva reserva = gestorReservas.crearReserva(huesped, habitacionDoble.getNumero(), ingreso, egreso, 2);
        reserva.registrarSena(reserva.calcularSenaRequerida(), "Tarjeta");
        gestorReservas.confirmarReserva(reserva);

        servicioDemo(gestorEstadias, gestorPagos, reserva);

        System.out.println("\n--- Resumen final ---");
        System.out.println(hotel.resumen());
        System.out.println("Habitación de ejemplo disponible: " + habitacionSimple.estaDisponible());
    }

    /**
     * Ejecuta el flujo de estadía, servicios, cálculo y pago para la demostración.
     *
     * @param gestorEstadias controlador de estadías
     * @param gestorPagos controlador de pagos
     * @param reserva reserva confirmada que se convertirá en estadía
     */
    private static void servicioDemo(GestorEstadias gestorEstadias, GestorPagos gestorPagos, Reserva reserva) {
        StaySummary resumen = gestorEstadias.registrarEstadia(reserva, reserva.getFechaIngreso(), reserva.getFechaEgreso());

        ServicioEstadia desayuno = new DesayunoDecorator(new ServicioBase());
        ServicioEstadia spa = new SpaDecorator(new ServicioBase());
        ServicioEstadia lavanderia = new LavanderiaDecorator(new ServicioBase());

        gestorEstadias.agregarServicio(resumen.getEstadia(), desayuno);
        gestorEstadias.agregarServicio(resumen.getEstadia(), spa);
        gestorEstadias.agregarServicio(resumen.getEstadia(), lavanderia);

        double total = gestorEstadias.calcularCostoTotal(
                resumen.getEstadia(),
                new PoliticaPrecioTemporadaMedia(),
                new DescuentoClienteFrecuente());

        double saldo = resumen.getEstadia().calcularSaldoPendiente(total);
        gestorPagos.registrarPago(resumen.getEstadia(), saldo, new PagoTarjeta());
        gestorEstadias.finalizarEstadia(resumen.getEstadia());

        System.out.println("\n--- Detalle de estadía ---");
        System.out.println(resumen.descripcion());
        System.out.println("Total calculado: " + total);
        System.out.println("Saldo pendiente: " + resumen.getEstadia().calcularSaldoPendiente(total));
    }
}
