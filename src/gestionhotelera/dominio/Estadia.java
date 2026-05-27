package gestionhotelera.dominio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa la permanencia real de un huésped en el hotel.
 * Guarda servicios contratados y pagos para poder calcular el total y el saldo.
 */
public class Estadia {
    private final Reserva reserva;
    private final LocalDate fechaIngresoReal;
    private final LocalDate fechaEgresoReal;
    private final List<ServicioConsumido> servicios;
    private final List<Pago> pagos;

    /**
     * Crea una estadía vinculada a una reserva.
     *
     * @param reserva reserva confirmada o en proceso
     * @param fechaIngresoReal ingreso real del huésped
     * @param fechaEgresoReal egreso real del huésped
     */
    public Estadia(Reserva reserva, LocalDate fechaIngresoReal, LocalDate fechaEgresoReal) {
        this.reserva = reserva;
        this.fechaIngresoReal = fechaIngresoReal;
        this.fechaEgresoReal = fechaEgresoReal;
        this.servicios = new ArrayList<>();
        this.pagos = new ArrayList<>();
    }

    /**
     * Agrega un servicio consumido durante la estadía.
     *
     * @param servicio servicio concreto
     */
    public void agregarServicio(ServicioConsumido servicio) {
        servicios.add(servicio);
    }

    /**
     * Registra un pago y actualiza el estado general del cobro.
     *
     * @param pago pago recibido
     */
    public void registrarPago(Pago pago) {
        pagos.add(pago);
    }

    /**
     * Calcula el costo total de los servicios consumidos.
     *
     * @return suma de servicios
     */
    public double calcularTotalServicios() {
        double total = 0.0;
        for (ServicioConsumido servicio : servicios) {
            total += servicio.getPrecio();
        }
        return total;
    }

    /**
     * Calcula el monto ya abonado.
     *
     * @return suma de pagos registrados
     */
    public double calcularTotalPagado() {
        double total = 0.0;
        for (Pago pago : pagos) {
            total += pago.getMonto();
        }
        return total;
    }

    /**
     * Calcula el saldo pendiente respecto del total final.
     *
     * @param totalFinal importe total ya calculado
     * @return saldo pendiente
     */
    public double calcularSaldoPendiente(double totalFinal) {
        return Math.max(totalFinal - calcularTotalPagado(), 0.0);
    }

    /**
     * Devuelve la lista de servicios como vista de solo lectura.
     *
     * @return servicios contratados
     */
    public List<ServicioConsumido> getServicios() {
        return Collections.unmodifiableList(servicios);
    }

    /**
     * Devuelve la lista de pagos como vista de solo lectura.
     *
     * @return pagos registrados
     */
    public List<Pago> getPagos() {
        return Collections.unmodifiableList(pagos);
    }

    /**
     * Devuelve la reserva asociada.
     *
     * @return reserva original
     */
    public Reserva getReserva() {
        return reserva;
    }

    /**
     * Devuelve la fecha real de ingreso.
     *
     * @return fecha de llegada
     */
    public LocalDate getFechaIngresoReal() {
        return fechaIngresoReal;
    }

    /**
     * Devuelve la fecha real de egreso.
     *
     * @return fecha de salida
     */
    public LocalDate getFechaEgresoReal() {
        return fechaEgresoReal;
    }

    /**
     * Devuelve la cantidad de noches reales.
     *
     * @return noches de la estadía
     */
    public int calcularNoches() {
        return reserva.calcularNoches();
    }

    /**
     * Genera un resumen simple de la estadía.
     *
     * @return texto descriptivo
     */
    public String resumen() {
        return "Estadía de " + reserva.getHuesped().getNombreCompleto() +
                " en habitación " + reserva.getHabitacion().getNumero() +
                " | noches: " + calcularNoches() +
                " | servicios: " + servicios.size() +
                " | pagos: " + pagos.size();
    }
}