package gestionhotelera.strategy;

/**
 * Descuento para huéspedes frecuentes.
 */
public class DescuentoClienteFrecuente implements DescuentoStrategy {

    /**
     * Aplica un 10 por ciento de descuento.
     *
     * @param total importe base
     * @return importe final
     */
    @Override
    public double aplicar(double total) {
        return total * 0.90;
    }
}