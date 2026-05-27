package gestionhotelera.state;

import gestionhotelera.dominio.EstadoReserva;
import gestionhotelera.dominio.Reserva;

/**
 * Interfaz del patrón State para el ciclo de vida de una reserva.
 * Cada estado define qué acciones son válidas y cómo evoluciona la reserva.
 */
public interface EstadoReservaComportamiento {

    /**
     * Intenta confirmar la reserva.
     *
     * @param reserva reserva que está cambiando
     */
    void confirmar(Reserva reserva);

    /**
     * Intenta cancelar la reserva.
     *
     * @param reserva reserva que está cambiando
     */
    void cancelar(Reserva reserva);

    /**
     * Intenta finalizar la reserva.
     *
     * @param reserva reserva que está cambiando
     */
    void finalizar(Reserva reserva);

    /**
     * Devuelve el estado observable.
     *
     * @return estado actual
     */
    EstadoReserva obtenerEstado();
}