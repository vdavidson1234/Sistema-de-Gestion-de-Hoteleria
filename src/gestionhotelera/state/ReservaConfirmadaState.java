package gestionhotelera.state;

import gestionhotelera.dominio.EstadoReserva;
import gestionhotelera.dominio.Reserva;

/**
 * Estado de una reserva ya confirmada.
 */
public class ReservaConfirmadaState implements EstadoReservaComportamiento {

    /**
     * Confirmar de nuevo no cambia el estado.
     *
     * @param reserva reserva en edición
     */
    @Override
    public void confirmar(Reserva reserva) {
        throw new IllegalStateException("La reserva ya está confirmada.");
    }

    /**
     * Cancela la reserva confirmada y libera la habitación.
     *
     * @param reserva reserva en edición
     */
    @Override
    public void cancelar(Reserva reserva) {
        reserva.setEstado(new ReservaCanceladaState());
        reserva.getHabitacion().cambiarEstado(gestionhotelera.dominio.EstadoHabitacion.DISPONIBLE);
    }

    /**
     * Finaliza la reserva luego de la estadía.
     *
     * @param reserva reserva en edición
     */
    @Override
    public void finalizar(Reserva reserva) {
        reserva.setEstado(new ReservaFinalizadaState());
        reserva.getHabitacion().cambiarEstado(gestionhotelera.dominio.EstadoHabitacion.DISPONIBLE);
    }

    /**
     * Devuelve el estado confirmado.
     *
     * @return estado observable
     */
    @Override
    public EstadoReserva obtenerEstado() {
        return EstadoReserva.CONFIRMADA;
    }
}