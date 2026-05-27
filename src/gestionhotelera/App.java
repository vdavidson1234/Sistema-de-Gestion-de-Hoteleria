package gestionhotelera;

import java.time.LocalDate;

import gestionhotelera.control.GestorEstadias;
import gestionhotelera.control.GestorHabitaciones;
import gestionhotelera.control.GestorPagos;
import gestionhotelera.control.GestorReservas;
import gestionhotelera.decorator.DesayunoDecorator;
import gestionhotelera.decorator.LavanderiaDecorator;
import gestionhotelera.decorator.ServicioBase;
import gestionhotelera.dominio.ServicioEstadia;
import gestionhotelera.decorator.SpaDecorator;
import gestionhotelera.dominio.Habitacion;
import gestionhotelera.dominio.Hotel;
import gestionhotelera.dominio.Huesped;
import gestionhotelera.dominio.Reserva;
import gestionhotelera.dominio.StaySummary;
import gestionhotelera.dominio.TipoHabitacion;
import gestionhotelera.factory.HabitacionFactory;
import gestionhotelera.pagos.PagoTarjeta;
import gestionhotelera.strategy.DescuentoClienteFrecuente;
import gestionhotelera.strategy.PoliticaPrecioNormal;

/**
 * Punto de entrada de la aplicación.
 * Este programa crea un escenario de demostración del sistema hotelero
 * y muestra cómo se conectan los patrones y principios del proyecto.
 */
public class App {

    /**
     * Ejecuta una demostración simple del sistema hotelero.
     *
     * @param args argumentos de línea de comandos, no utilizados en esta demo
     */
    public static void main(String[] args) {
        Hotel hotel = new Hotel("Hotel Aurora", "Av. Central 123");
        HabitacionFactory habitacionFactory = new HabitacionFactory();
        GestorHabitaciones gestorHabitaciones = new GestorHabitaciones(hotel, habitacionFactory);
        GestorReservas gestorReservas = new GestorReservas(hotel);
        GestorEstadias gestorEstadias = new GestorEstadias();
        GestorPagos gestorPagos = new GestorPagos();

        Habitacion habitacionSimple = gestorHabitaciones.crearYRegistrarHabitacion(101, 2, 45_000, TipoHabitacion.SIMPLE);
        Habitacion habitacionDoble = gestorHabitaciones.crearYRegistrarHabitacion(202, 3, 68_000, TipoHabitacion.DOBLE);
        gestorHabitaciones.crearYRegistrarHabitacion(303, 4, 95_000, TipoHabitacion.SUITE);

        Huesped huesped = new Huesped("Ana", "Pereyra", "12345678", "3412345678", "ana@mail.com", "Frecuente");

        LocalDate ingreso = LocalDate.now().plusDays(1);
        LocalDate egreso = ingreso.plusDays(3);

        Reserva reserva = gestorReservas.crearReserva(huesped, habitacionDoble.getNumero(), ingreso, egreso, 2);
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
                new PoliticaPrecioNormal(),
                new DescuentoClienteFrecuente());

        gestorPagos.registrarPago(resumen.getEstadia(), total / 2.0, new PagoTarjeta());
        gestorPagos.registrarPago(resumen.getEstadia(), total / 2.0, new PagoTarjeta());
        gestorEstadias.finalizarEstadia(resumen.getEstadia());

        System.out.println("\n--- Detalle de estadía ---");
        System.out.println(resumen.descripcion());
        System.out.println("Total calculado: " + total);
        System.out.println("Saldo pendiente: " + resumen.getEstadia().calcularSaldoPendiente(total));
    }
}