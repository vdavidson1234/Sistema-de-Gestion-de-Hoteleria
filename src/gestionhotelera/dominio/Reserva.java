package gestionhotelera.dominio;

/**
 * Representa una reserva de habitación.
 * El estado interno se maneja con el patrón State para evitar condicionales repetidos.
 */
public class Reserva {
    private final String codigo;
    private final Huesped huesped;
    private final Habitacion habitacion;
    private final java.time.LocalDate fechaIngreso;
    private final java.time.LocalDate fechaEgreso;
    private gestionhotelera.state.EstadoReservaComportamiento estado;

    /**
     * Crea una reserva en estado pendiente.
     *
     * @param codigo identificador de la reserva
     * @param huesped huésped asociado
     * @param habitacion habitación elegida
     * @param fechaIngreso fecha de ingreso
     * @param fechaEgreso fecha de egreso
     */
    public Reserva(String codigo, Huesped huesped, Habitacion habitacion, java.time.LocalDate fechaIngreso, java.time.LocalDate fechaEgreso) {
        this.codigo = codigo;
        this.huesped = huesped;
        this.habitacion = habitacion;
        this.fechaIngreso = fechaIngreso;
        this.fechaEgreso = fechaEgreso;
        this.estado = new gestionhotelera.state.ReservaPendienteState();
    }

    /**
     * Confirma la reserva usando el comportamiento del estado actual.
     */
    public void confirmar() {
        estado.confirmar(this);
    }

    /**
     * Cancela la reserva usando el comportamiento del estado actual.
     */
    public void cancelar() {
        estado.cancelar(this);
    }

    /**
     * Finaliza la reserva usando el comportamiento del estado actual.
     */
    public void finalizar() {
        estado.finalizar(this);
    }

    /**
     * Indica si la reserva todavía bloquea la habitación.
     *
     * @return true si aún está activa
     */
    public boolean bloqueaDisponibilidad() {
        return estado.obtenerEstado() == EstadoReserva.PENDIENTE || estado.obtenerEstado() == EstadoReserva.CONFIRMADA;
    }

    /**
     * Calcula la cantidad de noches entre ingreso y egreso.
     *
     * @return noches de la reserva
     */
    public int calcularNoches() {
        long noches = java.time.temporal.ChronoUnit.DAYS.between(fechaIngreso, fechaEgreso);
        return (int) Math.max(noches, 1);
    }

    /**
     * Devuelve el código de la reserva.
     *
     * @return código interno
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Devuelve el huésped asociado.
     *
     * @return huésped
     */
    public Huesped getHuesped() {
        return huesped;
    }

    /**
     * Devuelve la habitación reservada.
     *
     * @return habitación
     */
    public Habitacion getHabitacion() {
        return habitacion;
    }

    /**
     * Devuelve la fecha de ingreso.
     *
     * @return fecha de entrada
     */
    public java.time.LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    /**
     * Devuelve la fecha de egreso.
     *
     * @return fecha de salida
     */
    public java.time.LocalDate getFechaEgreso() {
        return fechaEgreso;
    }

    /**
     * Devuelve el estado observable de la reserva.
     *
     * @return estado actual
     */
    public EstadoReserva getEstado() {
        return estado.obtenerEstado();
    }

    /**
     * Cambia el comportamiento interno del estado.
     * Este método lo usan las clases State para avanzar de estado.
     *
     * @param nuevoEstado nuevo comportamiento
     */
    public void setEstado(gestionhotelera.state.EstadoReservaComportamiento nuevoEstado) {
        this.estado = nuevoEstado;
    }

    /**
     * Devuelve un resumen legible de la reserva.
     *
     * @return texto descriptivo
     */
    public String resumen() {
        return "Reserva " + codigo + " | " + huesped.getNombreCompleto() + " | Habitación " + habitacion.getNumero() +
                " | " + fechaIngreso + " -> " + fechaEgreso + " | Estado: " + getEstado();
    }
}