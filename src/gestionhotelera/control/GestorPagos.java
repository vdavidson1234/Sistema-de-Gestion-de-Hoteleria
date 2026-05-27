package gestionhotelera.control;

import gestionhotelera.dominio.Estadia;
import gestionhotelera.dominio.Pago;
import gestionhotelera.pagos.MetodoPago;

/**
 * Controlador encargado de registrar pagos.
 * Encapsula la creación del pago y su asociación a la estadía.
 */
public class GestorPagos {

    /**
     * Registra un pago en una estadía usando el método de pago indicado.
     *
     * @param estadia estadía que recibe el pago
     * @param monto monto abonado
     * @param metodoPago método elegido
     * @return pago guardado
     */
    public Pago registrarPago(Estadia estadia, double monto, MetodoPago metodoPago) {
        Pago pago = new Pago(monto, metodoPago);
        pago.procesar();
        estadia.registrarPago(pago);
        return pago;
    }
}