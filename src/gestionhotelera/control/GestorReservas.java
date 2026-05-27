package gestionhotelera.control;

import java.time.LocalDate;
import java.util.List;

import gestionhotelera.dominio.Habitacion;
import gestionhotelera.dominio.Hotel;
import gestionhotelera.dominio.Huesped;
import gestionhotelera.dominio.Reserva;

/**
 * Controlador encargado de crear, confirmar y cancelar reservas.
 * También actúa como Creator porque conoce los datos necesarios para construir la reserva.
 */
public class GestorReservas {
    private final Hotel hotel;
    private final ValidadorDisponibilidad validadorDisponibilidad;
    private final NotificadorReserva notificadorReserva;

    /**
     * Crea el gestor con sus colaboradores.
     *
     * @param hotel hotel principal
     */
    public GestorReservas(Hotel hotel) {
        this.hotel = hotel;
        this.validadorDisponibilidad = new ValidadorDisponibilidad();
        this.notificadorReserva = new NotificadorReserva();
    }

    /**
     * Crea una reserva asociando huésped, habitación y fechas.
     *
     * @param huesped huésped titular
     * @param numeroHabitacion habitación elegida
     * @param fechaIngreso ingreso solicitado
     * @param fechaEgreso egreso solicitado
     * @return reserva creada
     */
    public Reserva crearReserva(Huesped huesped, int numeroHabitacion, LocalDate fechaIngreso, LocalDate fechaEgreso) {
        return crearReserva(huesped, numeroHabitacion, fechaIngreso, fechaEgreso, 1);
    }

    /**
     * Crea una reserva validando la capacidad requerida por los huéspedes.
     *
     * @param huesped huésped titular
     * @param numeroHabitacion habitación elegida
     * @param fechaIngreso ingreso solicitado
     * @param fechaEgreso egreso solicitado
     * @param personas cantidad de personas que ocuparán la habitación
     * @return reserva creada
     */
    public Reserva crearReserva(Huesped huesped, int numeroHabitacion, LocalDate fechaIngreso, LocalDate fechaEgreso, int personas) {
        List<Habitacion> disponibles = validadorDisponibilidad.validar(hotel, fechaIngreso, fechaEgreso, personas);
        Habitacion habitacionElegida = null;
        for (Habitacion habitacion : disponibles) {
            if (habitacion.getNumero() == numeroHabitacion) {
                habitacionElegida = habitacion;
                break;
            }
        }
        if (habitacionElegida == null) {
            throw new IllegalStateException("La habitación solicitada no está disponible.");
        }

        String codigo = "RES-" + System.currentTimeMillis();
        Reserva reserva = new Reserva(codigo, huesped, habitacionElegida, fechaIngreso, fechaEgreso);
        hotel.registrarReserva(reserva);
        return reserva;
    }

    /**
     * Confirma una reserva y notifica el cambio.
     *
     * @param reserva reserva a confirmar
     */
    public void confirmarReserva(Reserva reserva) {
        reserva.confirmar();
        notificadorReserva.notificarConfirmacion(reserva);
    }

    /**
     * Cancela una reserva y notifica el cambio.
     *
     * @param reserva reserva a cancelar
     */
    public void cancelarReserva(Reserva reserva) {
        reserva.cancelar();
        notificadorReserva.notificarCancelacion(reserva);
    }
}