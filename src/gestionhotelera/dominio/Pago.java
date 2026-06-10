package gestionhotelera.dominio;

import java.time.LocalDateTime;

/**
 * Representa un pago registrado durante una estadía.
 * Guarda el monto, la fecha, el método y el estado resultante.
 */
public class Pago {
    private final double monto;
    private final LocalDateTime fechaPago;
    private final gestionhotelera.pagos.MetodoPago metodoPago;
    private EstadoPago estadoPago;

    /**
     * Crea un pago con fecha automática y estado pendiente inicial.
     *
     * @param monto valor abonado
     * @param metodoPago estrategia de cobro usada
     */
    public Pago(double monto, gestionhotelera.pagos.MetodoPago metodoPago) {
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.fechaPago = LocalDateTime.now();
        this.estadoPago = EstadoPago.PENDIENTE;
    }

    public Pago(double monto, gestionhotelera.pagos.MetodoPago metodoPago, LocalDateTime fechaPago, EstadoPago estadoPago) {
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.fechaPago = fechaPago;
        this.estadoPago = estadoPago;
    }

    /**
     * Procesa el pago usando el método concreto elegido.
     *
     * @return true si el cobro fue aceptado
     */
    public boolean procesar() {
        boolean confirmado = metodoPago.pagar(monto);
        estadoPago = confirmado ? EstadoPago.PAGADO : EstadoPago.PENDIENTE;
        return confirmado;
    }

    /**
     * Devuelve el monto del pago.
     *
     * @return importe abonado
     */
    public double getMonto() {
        return monto;
    }

    /**
     * Devuelve la fecha y hora del pago.
     *
     * @return fecha de procesamiento
     */
    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    /**
     * Devuelve el método de pago usado.
     *
     * @return estrategia concreta
     */
    public gestionhotelera.pagos.MetodoPago getMetodoPago() {
        return metodoPago;
    }

    /**
     * Devuelve el estado del pago.
     *
     * @return estado actual
     */
    public EstadoPago getEstadoPago() {
        return estadoPago;
    }
}
