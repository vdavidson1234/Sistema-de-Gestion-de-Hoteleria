package gestionhotelera.control;

import gestionhotelera.dominio.Reserva;

/**
 * Notificador simple por consola para mostrar cambios importantes de reservas.
 * En una etapa futura podría reemplazarse por correo o mensajería.
 */
public class NotificadorReserva {

    /**
     * Notifica la confirmación de una reserva.
     *
     * @param reserva reserva confirmada
     */
    public void notificarConfirmacion(Reserva reserva) {
        System.out.println("Reserva confirmada: " + reserva.resumen());
    }

    /**
     * Notifica la cancelación de una reserva.
     *
     * @param reserva reserva cancelada
     */
    public void notificarCancelacion(Reserva reserva) {
        System.out.println("Reserva cancelada: " + reserva.resumen());
    }
}