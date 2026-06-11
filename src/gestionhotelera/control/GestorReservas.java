package gestionhotelera.control;

import gestionhotelera.dominio.Habitacion;
import gestionhotelera.dominio.Hotel;
import gestionhotelera.dominio.Huesped;
import gestionhotelera.dominio.Reserva;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controlador encargado de crear, confirmar y cancelar reservas.
 */
public class GestorReservas {
    private final Hotel hotel;
    private final ValidadorDisponibilidad validadorDisponibilidad;
    private final NotificadorReserva notificadorReserva;

    public GestorReservas(Hotel hotel) {
        this.hotel = hotel;
        this.validadorDisponibilidad = new ValidadorDisponibilidad();
        this.notificadorReserva = new NotificadorReserva();
    }

    public Reserva crearReserva(Huesped huesped, int numeroHabitacion, LocalDate fechaIngreso, LocalDate fechaEgreso) {
        return crearReserva(huesped, numeroHabitacion, fechaIngreso, fechaEgreso, 1);
    }

    public Reserva crearReserva(Huesped huesped, int numeroHabitacion, LocalDate fechaIngreso, LocalDate fechaEgreso, int personas) {
        List<Huesped> ocupantes = new ArrayList<>();
        ocupantes.add(huesped);
        return crearReserva(huesped, numeroHabitacion, fechaIngreso, fechaEgreso, ocupantes,
                "GRP-" + System.currentTimeMillis(), personas);
    }

    public Reserva crearReserva(Huesped huesped, int numeroHabitacion, LocalDate fechaIngreso, LocalDate fechaEgreso,
            List<Huesped> ocupantes, String grupoCodigo) {
        int personas = ocupantes == null || ocupantes.isEmpty() ? 1 : ocupantes.size();
        return crearReserva(huesped, numeroHabitacion, fechaIngreso, fechaEgreso, ocupantes, grupoCodigo, personas);
    }

    public List<Reserva> crearReservasGrupo(Huesped titular, Map<Integer, List<Huesped>> ocupantesPorHabitacion,
            LocalDate fechaIngreso, int noches) {
        if (ocupantesPorHabitacion == null || ocupantesPorHabitacion.isEmpty()) {
            throw new IllegalArgumentException("Debe indicarse al menos una habitación.");
        }
        if (noches <= 0) {
            throw new IllegalArgumentException("La cantidad de noches debe ser mayor a cero.");
        }

        String grupoCodigo = "GRP-" + System.currentTimeMillis();
        LocalDate fechaEgreso = fechaIngreso.plusDays(noches);
        List<Reserva> reservas = new ArrayList<>();

        for (Map.Entry<Integer, List<Huesped>> entry : ocupantesPorHabitacion.entrySet()) {
            List<Huesped> ocupantes = new ArrayList<>(entry.getValue());
            if (ocupantes.isEmpty()) {
                throw new IllegalArgumentException("Cada habitación debe tener al menos un ocupante.");
            }
            validarHabitacionDisponible(entry.getKey(), fechaIngreso, fechaEgreso, ocupantes.size());
        }

        int secuencia = 1;
        for (Map.Entry<Integer, List<Huesped>> entry : ocupantesPorHabitacion.entrySet()) {
            List<Huesped> ocupantes = new ArrayList<>(entry.getValue());
            String codigo = grupoCodigo + "-" + secuencia;
            Reserva reserva = crearReserva(titular, entry.getKey(), fechaIngreso, fechaEgreso, ocupantes, grupoCodigo,
                    ocupantes.size(), codigo);
            reservas.add(reserva);
            secuencia++;
        }

        return reservas;
    }

    private void validarHabitacionDisponible(int numeroHabitacion, LocalDate fechaIngreso, LocalDate fechaEgreso, int personas) {
        List<Habitacion> disponibles = validadorDisponibilidad.validar(hotel, fechaIngreso, fechaEgreso, personas);
        for (Habitacion habitacion : disponibles) {
            if (habitacion.getNumero() == numeroHabitacion) {
                return;
            }
        }
        throw new IllegalStateException("La habitación " + numeroHabitacion + " no está disponible para la reserva.");
    }

    private Reserva crearReserva(Huesped huesped, int numeroHabitacion, LocalDate fechaIngreso, LocalDate fechaEgreso,
            List<Huesped> ocupantes, String grupoCodigo, int personas) {
        return crearReserva(huesped, numeroHabitacion, fechaIngreso, fechaEgreso, ocupantes, grupoCodigo, personas,
                "RES-" + System.currentTimeMillis());
    }

    private Reserva crearReserva(Huesped huesped, int numeroHabitacion, LocalDate fechaIngreso, LocalDate fechaEgreso,
            List<Huesped> ocupantes, String grupoCodigo, int personas, String codigo) {
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

        if (ocupantes == null || ocupantes.isEmpty()) {
            ocupantes = new ArrayList<>();
            ocupantes.add(huesped);
        }
        if (ocupantes.size() > habitacionElegida.getCapacidad()) {
            throw new IllegalStateException("La cantidad de ocupantes supera la capacidad de la habitación.");
        }

        Reserva reserva = new Reserva(codigo, grupoCodigo, huesped, habitacionElegida, fechaIngreso, fechaEgreso, ocupantes);
        hotel.registrarReserva(reserva);
        return reserva;
    }

    public void confirmarReservasConSena(List<Reserva> reservas, double montoTotal, String metodoPago) {
        double requerido = calcularSenaTotal(reservas);
        if (montoTotal < requerido) {
            throw new IllegalArgumentException("La seña total debe cubrir al menos el 25% de las habitaciones.");
        }

        for (Reserva reserva : reservas) {
            reserva.registrarSena(reserva.calcularSenaRequerida(), metodoPago);
            confirmarReserva(reserva);
        }
    }

    public double calcularSenaTotal(List<Reserva> reservas) {
        double total = 0.0;
        for (Reserva reserva : reservas) {
            total += reserva.calcularSenaRequerida();
        }
        return total;
    }

    public void confirmarReserva(Reserva reserva) {
        reserva.confirmar();
        notificadorReserva.notificarConfirmacion(reserva);
    }

    public void cancelarReserva(Reserva reserva) {
        reserva.cancelar();
        notificadorReserva.notificarCancelacion(reserva);
    }

    public void extenderReserva(Reserva reserva, LocalDate nuevaFechaEgreso) {
        if (reserva == null) {
            throw new IllegalArgumentException("Debe indicarse una reserva.");
        }
        if (nuevaFechaEgreso == null || !nuevaFechaEgreso.isAfter(reserva.getFechaEgreso())) {
            throw new IllegalArgumentException("La nueva fecha de egreso debe ser posterior al egreso actual.");
        }
        if (!reserva.bloqueaDisponibilidad()) {
            throw new IllegalStateException("Solo se pueden extender reservas pendientes o confirmadas.");
        }

        for (Reserva otra : hotel.getReservas()) {
            if (otra == reserva || !otra.bloqueaDisponibilidad()) {
                continue;
            }
            if (otra.getHabitacion().getNumero() != reserva.getHabitacion().getNumero()) {
                continue;
            }
            boolean solapa = reserva.getFechaEgreso().isBefore(otra.getFechaEgreso())
                    && nuevaFechaEgreso.isAfter(otra.getFechaIngreso());
            if (solapa) {
                throw new IllegalStateException("No se puede extender: la habitación tiene otra reserva en esos días.");
            }
        }

        reserva.extenderHasta(nuevaFechaEgreso);
    }
}
