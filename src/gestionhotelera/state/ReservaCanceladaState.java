package gestionhotelera.state;

import gestionhotelera.dominio.EstadoReserva;
import gestionhotelera.dominio.Reserva;

/**
 * Estado terminal de reserva cancelada.
 */
public class ReservaCanceladaState implements EstadoReservaComportamiento {

    /**
     * No permite reconfirmar una reserva cancelada.
     *
     * @param reserva reserva en edición
     */
    @Override
    public void confirmar(Reserva reserva) {
        throw new IllegalStateException("No se puede confirmar una reserva cancelada.");
    }

    /**
     * La cancelación repetida no se permite.
     *
     * @param reserva reserva en edición
     */
    @Override
    public void cancelar(Reserva reserva) {
        throw new IllegalStateException("La reserva ya está cancelada.");
    }

    /**
     * No se puede finalizar una reserva cancelada.
     *
     * @param reserva reserva en edición
     */
    @Override
    public void finalizar(Reserva reserva) {
        throw new IllegalStateException("No se puede finalizar una reserva cancelada.");
    }

    /**
     * Devuelve el estado cancelado.
     *
     * @return estado observable
     */
    @Override
    public EstadoReserva obtenerEstado() {
        return EstadoReserva.CANCELADA;
    }
}