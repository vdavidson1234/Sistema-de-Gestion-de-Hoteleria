package gestionhotelera.strategy;

/**
 * Descuento para promociones puntuales.
 */
public class DescuentoPromocionEspecial implements DescuentoStrategy {

    /**
     * Aplica un 20 por ciento de descuento.
     *
     * @param total importe base
     * @return importe final
     */
    @Override
    public double aplicar(double total) {
        return total * 0.80;
    }
}