package gestionhotelera.pagos;

/**
 * Interfaz del método de pago.
 * El sistema puede intercambiar entre efectivo, tarjeta, transferencia o pago simulado.
 */
public interface MetodoPago {

    /**
     * Intenta procesar el pago.
     *
     * @param monto importe a cobrar
     * @return true si el cobro se considera aceptado
     */
    boolean pagar(double monto);

    /**
     * Devuelve el nombre visible del método.
     *
     * @return nombre del método
     */
    String getNombre();
}