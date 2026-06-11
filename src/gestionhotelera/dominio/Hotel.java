package gestionhotelera.dominio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa el hotel completo.
 * Agrupa habitaciones y reservas, y ofrece consultas de disponibilidad.
 */
public class Hotel {
    private final String nombre;
    private final String direccion;
    private final List<Huesped> huespedes;
    private final List<Habitacion> habitaciones;
    private final List<Reserva> reservas;

    /**
     * Crea un hotel vacío.
     *
     * @param nombre nombre comercial
     * @param direccion dirección física
     */
    public Hotel(String nombre, String direccion) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.huespedes = new ArrayList<>();
        this.habitaciones = new ArrayList<>();
        this.reservas = new ArrayList<>();
    }

    public void registrarHuesped(Huesped huesped) {
        if (huesped == null || huesped.getDni() == null || huesped.getDni().isBlank()) {
            return;
        }
        Huesped existente = buscarHuespedPorDni(huesped.getDni());
        if (existente == null) {
            huespedes.add(huesped);
        }
    }

    /**
     * Agrega una habitación al catálogo del hotel.
     *
     * @param habitacion habitación nueva
     */
    public void agregarHabitacion(Habitacion habitacion) {
        Habitacion existente = buscarHabitacion(habitacion.getNumero());
        if (existente != null) {
            if (existente.estaActiva()) {
                throw new IllegalStateException("Ya existe una habitacion activa con ese numero.");
            }
            existente.reactivar();
            return;
        }
        habitaciones.add(habitacion);
    }

    public void eliminarHabitacion(int numero) {
        Habitacion habitacion = buscarHabitacion(numero);
        if (habitacion == null) {
            throw new IllegalStateException("No existe una habitacion con ese numero.");
        }
        boolean tieneHistorial = false;
        for (Reserva reserva : reservas) {
            if (reserva.getHabitacion().getNumero() == numero) {
                if (reserva.bloqueaDisponibilidad()) {
                    throw new IllegalStateException("No se puede eliminar una habitacion con reservas pendientes o confirmadas.");
                }
                tieneHistorial = true;
            }
        }
        if (tieneHistorial) {
            habitacion.darDeBaja();
        } else {
            habitaciones.remove(habitacion);
        }
    }

    /**
     * Registra una reserva dentro del hotel.
     *
     * @param reserva reserva creada por el sistema
     */
    public void registrarReserva(Reserva reserva) {
        registrarHuesped(reserva.getHuesped());
        for (Huesped ocupante : reserva.getOcupantes()) {
            registrarHuesped(ocupante);
        }
        reservas.add(reserva);
    }

    public Huesped buscarHuespedPorDni(String dni) {
        if (dni == null) {
            return null;
        }
        for (Huesped huesped : huespedes) {
            if (dni.trim().equalsIgnoreCase(huesped.getDni())) {
                return huesped;
            }
        }
        return null;
    }

    /**
     * Busca una habitación por número.
     *
     * @param numero número de habitación
     * @return habitación encontrada o null si no existe
     */
    public Habitacion buscarHabitacion(int numero) {
        for (Habitacion habitacion : habitaciones) {
            if (habitacion.getNumero() == numero) {
                return habitacion;
            }
        }
        return null;
    }

    /**
     * Consulta las habitaciones disponibles para un rango de fechas y un cupo de personas.
     * El criterio combina estado físico de la habitación y reservas activas que se superponen.
     *
     * @param fechaIngreso fecha de entrada
     * @param fechaEgreso fecha de salida
     * @param personas cantidad de personas
     * @return habitaciones que pueden reservarse
     */
    public List<Habitacion> consultarDisponibilidad(LocalDate fechaIngreso, LocalDate fechaEgreso, int personas) {
        List<Habitacion> disponibles = new ArrayList<>();
        for (Habitacion habitacion : habitaciones) {
            if (habitacion.getCapacidad() < personas || !habitacion.admiteReservas()) {
                continue;
            }
            boolean ocupadaPorReserva = false;
            for (Reserva reserva : reservas) {
                if (reserva.getHabitacion().getNumero() == habitacion.getNumero() && reserva.bloqueaDisponibilidad()) {
                    boolean solapa = fechaIngreso.isBefore(reserva.getFechaEgreso()) && fechaEgreso.isAfter(reserva.getFechaIngreso());
                    if (solapa) {
                        ocupadaPorReserva = true;
                        break;
                    }
                }
            }
            if (!ocupadaPorReserva) {
                disponibles.add(habitacion);
            }
        }
        return disponibles;
    }

    /**
     * Devuelve las habitaciones registradas.
     *
     * @return lista no modificable
     */
    public List<Habitacion> getHabitaciones() {
        List<Habitacion> activas = new ArrayList<>();
        for (Habitacion habitacion : habitaciones) {
            if (habitacion.estaActiva()) {
                activas.add(habitacion);
            }
        }
        return Collections.unmodifiableList(activas);
    }

    public List<Habitacion> getTodasLasHabitaciones() {
        return Collections.unmodifiableList(habitaciones);
    }

    public List<Huesped> getHuespedes() {
        return Collections.unmodifiableList(huespedes);
    }

    /**
     * Devuelve las reservas registradas.
     *
     * @return lista no modificable
     */
    public List<Reserva> getReservas() {
        return Collections.unmodifiableList(reservas);
    }

    /**
     * Devuelve un resumen legible del hotel.
     *
     * @return texto descriptivo
     */
    public String resumen() {
        return nombre + " - " + direccion + " | habitaciones: " + habitaciones.size() + " | reservas: " + reservas.size();
    }
}
