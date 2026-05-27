package gestionhotelera.state;

import gestionhotelera.dominio.EstadoReserva;
import gestionhotelera.dominio.Reserva;

/**
 * Estado final de una reserva que ya pasó a estadía y fue cerrada.
 */
public class ReservaFinalizadaState implements EstadoReservaComportamiento {

    /**
     * No permite volver a confirmar una reserva finalizada.
     *
     * @param reserva reserva en edición
     */
    @Override
    public void confirmar(Reserva reserva) {
        throw new IllegalStateException("No se puede confirmar una reserva finalizada.");
    }

    /**
     * No permite cancelar una reserva finalizada.
     *
     * @param reserva reserva en edición
     */
    @Override
    public void cancelar(Reserva reserva) {
        throw new IllegalStateException("No se puede cancelar una reserva finalizada.");
    }

    /**
     * No modifica el estado porque ya está cerrada.
     *
     * @param reserva reserva en edición
     */
    @Override
    public void finalizar(Reserva reserva) {
        throw new IllegalStateException("La reserva ya está finalizada.");
    }

    /**
     * Devuelve el estado finalizado.
     *
     * @return estado observable
     */
    @Override
    public EstadoReserva obtenerEstado() {
        return EstadoReserva.FINALIZADA;
    }
}