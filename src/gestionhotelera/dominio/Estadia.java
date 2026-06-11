package gestionhotelera.dominio;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import gestionhotelera.strategy.DescuentoStrategy;
import gestionhotelera.strategy.PoliticaPrecio;

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
    private String politicaPrecioNombre;
    private double politicaPrecioPorcentaje;
    private String descuentoNombre;
    private double descuentoPorcentaje;
    private String descuentoTipoClienteRequerido;

    /**
     * Crea una estadía vinculada a una reserva.
     *
     * @param reserva reserva confirmada o en proceso
     * @param fechaIngresoReal ingreso real del huésped
     * @param fechaEgresoReal egreso real del huésped
     */
    public Estadia(Reserva reserva, LocalDate fechaIngresoReal, LocalDate fechaEgresoReal) {
        if (fechaIngresoReal == null || fechaEgresoReal == null || !fechaEgresoReal.isAfter(fechaIngresoReal)) {
            throw new IllegalArgumentException("La fecha de egreso real debe ser posterior al ingreso real.");
        }
        this.reserva = reserva;
        this.fechaIngresoReal = fechaIngresoReal;
        this.fechaEgresoReal = fechaEgresoReal;
        this.servicios = new ArrayList<>();
        this.pagos = new ArrayList<>();
        this.politicaPrecioNombre = "Temporada media";
        this.politicaPrecioPorcentaje = 0.0;
        this.descuentoNombre = "Sin descuento";
        this.descuentoPorcentaje = 0.0;
        this.descuentoTipoClienteRequerido = "";
    }

    public void aplicarCondicionesComerciales(PoliticaPrecio politicaPrecio, DescuentoStrategy descuento) {
        if (politicaPrecio == null || descuento == null) {
            throw new IllegalArgumentException("Debe indicarse política tarifaria y descuento.");
        }
        restaurarCondicionesComerciales(
                politicaPrecio.getNombre(),
                politicaPrecio.getPorcentajeAjuste(),
                descuento.getNombre(),
                descuento.getPorcentaje(),
                descuento.getTipoClienteRequerido());
    }

    public void restaurarCondicionesComerciales(String politicaPrecioNombre, double politicaPrecioPorcentaje,
            String descuentoNombre, double descuentoPorcentaje, String descuentoTipoClienteRequerido) {
        this.politicaPrecioNombre = textoOValor(politicaPrecioNombre, "Temporada media");
        this.politicaPrecioPorcentaje = politicaPrecioPorcentaje;
        this.descuentoNombre = textoOValor(descuentoNombre, "Sin descuento");
        this.descuentoPorcentaje = descuentoPorcentaje;
        this.descuentoTipoClienteRequerido = textoOValor(descuentoTipoClienteRequerido, "");
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
        double total = reserva.getSenaPagada();
        for (Pago pago : pagos) {
            total += pago.getMonto();
        }
        return total;
    }

    public double calcularPagosRegistrados() {
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

    public String getPoliticaPrecioNombre() {
        return politicaPrecioNombre;
    }

    public double getPoliticaPrecioPorcentaje() {
        return politicaPrecioPorcentaje;
    }

    public String getDescuentoNombre() {
        return descuentoNombre;
    }

    public double getDescuentoPorcentaje() {
        return descuentoPorcentaje;
    }

    public String getDescuentoTipoClienteRequerido() {
        return descuentoTipoClienteRequerido;
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
        long noches = ChronoUnit.DAYS.between(fechaIngresoReal, fechaEgresoReal);
        return (int) Math.max(noches, 1);
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

    private String textoOValor(String texto, String valorDefault) {
        return texto == null || texto.isBlank() ? valorDefault : texto;
    }
}
