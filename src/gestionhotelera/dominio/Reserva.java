package gestionhotelera.dominio;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa una reserva de una habitacion.
 * Varias reservas pueden compartir grupoCodigo cuando se hacen juntas.
 */
public class Reserva {
    public static final double PORCENTAJE_SENA = 0.25;

    private final String codigo;
    private final String grupoCodigo;
    private final Huesped huesped;
    private final Habitacion habitacion;
    private final LocalDate fechaIngreso;
    private LocalDate fechaEgreso;
    private final List<Huesped> ocupantes;
    private double senaPagada;
    private String metodoSena;
    private gestionhotelera.state.EstadoReservaComportamiento estado;

    public Reserva(String codigo, Huesped huesped, Habitacion habitacion, LocalDate fechaIngreso, LocalDate fechaEgreso) {
        this(codigo, codigo, huesped, habitacion, fechaIngreso, fechaEgreso, null);
    }

    public Reserva(String codigo, String grupoCodigo, Huesped huesped, Habitacion habitacion, LocalDate fechaIngreso,
            LocalDate fechaEgreso, List<Huesped> ocupantes) {
        this.codigo = codigo;
        this.grupoCodigo = grupoCodigo == null || grupoCodigo.isBlank() ? codigo : grupoCodigo;
        this.huesped = huesped;
        this.habitacion = habitacion;
        this.fechaIngreso = fechaIngreso;
        this.fechaEgreso = fechaEgreso;
        this.ocupantes = new ArrayList<>();
        if (ocupantes == null || ocupantes.isEmpty()) {
            agregarOcupante(huesped);
        } else {
            for (Huesped ocupante : ocupantes) {
                agregarOcupante(ocupante);
            }
        }
        this.senaPagada = 0.0;
        this.metodoSena = "";
        this.estado = new gestionhotelera.state.ReservaPendienteState();
    }

    public void confirmar() {
        estado.confirmar(this);
    }

    public void cancelar() {
        estado.cancelar(this);
    }

    public void finalizar() {
        estado.finalizar(this);
    }

    public void extenderHasta(LocalDate nuevaFechaEgreso) {
        if (nuevaFechaEgreso == null || !nuevaFechaEgreso.isAfter(fechaEgreso)) {
            throw new IllegalArgumentException("La nueva fecha de egreso debe ser posterior al egreso actual.");
        }
        this.fechaEgreso = nuevaFechaEgreso;
    }

    public boolean bloqueaDisponibilidad() {
        return estado.obtenerEstado() == EstadoReserva.PENDIENTE || estado.obtenerEstado() == EstadoReserva.CONFIRMADA;
    }

    public int calcularNoches() {
        long noches = ChronoUnit.DAYS.between(fechaIngreso, fechaEgreso);
        return (int) Math.max(noches, 1);
    }

    public double calcularTotalHabitacion() {
        return habitacion.getPrecioBase() * calcularNoches();
    }

    public double calcularSenaRequerida() {
        return calcularTotalHabitacion() * PORCENTAJE_SENA;
    }

    public void registrarSena(double monto, String metodo) {
        if (monto < calcularSenaRequerida()) {
            throw new IllegalArgumentException("La seña debe cubrir al menos el 25% de la habitación.");
        }
        this.senaPagada = monto;
        this.metodoSena = metodo == null ? "" : metodo;
    }

    public void restaurarSena(double monto, String metodo) {
        this.senaPagada = Math.max(monto, 0.0);
        this.metodoSena = metodo == null ? "" : metodo;
    }

    public boolean tieneSenaSuficiente() {
        return senaPagada >= calcularSenaRequerida();
    }

    public String getCodigo() {
        return codigo;
    }

    public String getGrupoCodigo() {
        return grupoCodigo;
    }

    public Huesped getHuesped() {
        return huesped;
    }

    public Habitacion getHabitacion() {
        return habitacion;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public LocalDate getFechaEgreso() {
        return fechaEgreso;
    }

    public EstadoReserva getEstado() {
        return estado.obtenerEstado();
    }

    public void setEstado(gestionhotelera.state.EstadoReservaComportamiento nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public List<Huesped> getOcupantes() {
        return Collections.unmodifiableList(ocupantes);
    }

    public int getCantidadPersonas() {
        return ocupantes.size();
    }

    public void agregarOcupante(Huesped ocupante) {
        if (ocupante == null || ocupante.getDni() == null || ocupante.getDni().isBlank()) {
            return;
        }
        for (Huesped existente : ocupantes) {
            if (ocupante.getDni().equalsIgnoreCase(existente.getDni())) {
                return;
            }
        }
        ocupantes.add(ocupante);
    }

    public String ocupantesResumen() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ocupantes.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(ocupantes.get(i).getNombreCompleto());
        }
        return sb.toString();
    }

    public double getSenaPagada() {
        return senaPagada;
    }

    public String getMetodoSena() {
        return metodoSena;
    }

    public String resumen() {
        return "Reserva " + codigo + " | Grupo " + grupoCodigo + " | " + huesped.getNombreCompleto()
                + " | Habitación " + habitacion.getNumero()
                + " | " + fechaIngreso + " -> " + fechaEgreso
                + " | Ocupantes: " + ocupantesResumen()
                + " | Estado: " + getEstado();
    }
}
