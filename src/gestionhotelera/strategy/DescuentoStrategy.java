package gestionhotelera.strategy;

/**
 * Contrato del descuento aplicado al total.
 * La calculadora depende de esta interfaz, cumpliendo DIP.
 */
public interface DescuentoStrategy {

    /**
     * Aplica el descuento al total recibido.
     *
     * @param total importe antes del descuento
     * @return importe final con descuento
     */
    double aplicar(double total);
}