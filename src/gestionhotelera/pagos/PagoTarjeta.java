package gestionhotelera.pagos;

/**
 * Método de pago con tarjeta.
 */
public class PagoTarjeta implements MetodoPago {

    /**
     * Acepta el cobro con tarjeta.
     *
     * @param monto importe a cobrar
     * @return siempre true
     */
    @Override
    public boolean pagar(double monto) {
        return monto >= 0;
    }

    /**
     * Devuelve el nombre del método.
     *
     * @return texto visible
     */
    @Override
    public String getNombre() {
        return "Tarjeta";
    }
}