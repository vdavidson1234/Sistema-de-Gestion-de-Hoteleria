package gestionhotelera.strategy;

/**
 * Estrategia nula para expresar que no se aplica descuento.
 */
public class DescuentoSinDescuento implements DescuentoStrategy {
    @Override
    public double aplicar(double total) {
        return total;
    }

    @Override
    public String getNombre() {
        return "Sin descuento";
    }

    @Override
    public double getPorcentaje() {
        return 0.0;
    }
}
