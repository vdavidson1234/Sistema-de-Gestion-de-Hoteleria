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

    @Override
    public String getNombre() {
        return "Cliente frecuente";
    }

    @Override
    public double getPorcentaje() {
        return 10.0;
    }

    @Override
    public String getTipoClienteRequerido() {
        return "Cliente frecuente";
    }
}
