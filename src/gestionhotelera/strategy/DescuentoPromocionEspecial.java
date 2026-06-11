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

    @Override
    public String getNombre() {
        return "Promoción especial";
    }

    @Override
    public double getPorcentaje() {
        return 20.0;
    }

    @Override
    public String getTipoClienteRequerido() {
        return "Promocional";
    }
}
