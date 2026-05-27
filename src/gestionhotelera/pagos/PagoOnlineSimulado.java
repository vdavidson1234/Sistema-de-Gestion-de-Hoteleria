package gestionhotelera.pagos;

/**
 * Método de pago online simulado para futuras integraciones.
 */
public class PagoOnlineSimulado implements MetodoPago {

    /**
     * Acepta el cobro como si viniera de una pasarela real.
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
        return "Pago online simulado";
    }
}