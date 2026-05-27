package gestionhotelera.pagos;

/**
 * Método de pago en efectivo.
 */
public class PagoEfectivo implements MetodoPago {

    /**
     * Acepta el cobro en efectivo.
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
        return "Efectivo";
    }
}