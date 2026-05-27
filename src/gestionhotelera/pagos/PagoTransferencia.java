package gestionhotelera.pagos;

/**
 * Método de pago por transferencia bancaria.
 */
public class PagoTransferencia implements MetodoPago {

    /**
     * Acepta el cobro por transferencia.
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
        return "Transferencia";
    }
}