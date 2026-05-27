package gestionhotelera.strategy;

/**
 * Descuento para temporadas de baja demanda.
 */
public class DescuentoTemporadaBaja implements DescuentoStrategy {

    /**
     * Aplica un 15 por ciento de descuento.
     *
     * @param total importe base
     * @return importe final
     */
    @Override
    public double aplicar(double total) {
        return total * 0.85;
    }
}