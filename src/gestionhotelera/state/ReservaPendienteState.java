package gestionhotelera.state;

import gestionhotelera.dominio.EstadoReserva;
import gestionhotelera.dominio.Reserva;

/**
 * Estado inicial de una reserva.
 */
public class ReservaPendienteState implements EstadoReservaComportamiento {

    /**
     * Confirma la reserva y pasa al siguiente estado.
     *
     * @param reserva reserva en edición
     */
    @Override
    public void confirmar(Reserva reserva) {
        if (!reserva.tieneSenaSuficiente()) {
            throw new IllegalStateException("Para confirmar la reserva se debe registrar una seña mínima del 25%.");
        }
        reserva.setEstado(new ReservaConfirmadaState());
        reserva.getHabitacion().cambiarEstado(gestionhotelera.dominio.EstadoHabitacion.RESERVADA);
    }

    /**
     * Cancela la reserva pendiente.
     *
     * @param reserva reserva en edición
     */
    @Override
    public void cancelar(Reserva reserva) {
        reserva.setEstado(new ReservaCanceladaState());
        reserva.getHabitacion().cambiarEstado(gestionhotelera.dominio.EstadoHabitacion.DISPONIBLE);
    }

    /**
     * No se puede finalizar una reserva que todavía no se confirmó.
     *
     * @param reserva reserva en edición
     */
    @Override
    public void finalizar(Reserva reserva) {
        throw new IllegalStateException("Una reserva pendiente no puede finalizarse.");
    }

    /**
     * Devuelve el estado pendiente.
     *
     * @return estado observable
     */
    @Override
    public EstadoReserva obtenerEstado() {
        return EstadoReserva.PENDIENTE;
    }
}
