package gestionhotelera.strategy;

/**
 * Política que incrementa el precio por temporada alta.
 */
public class PoliticaPrecioTemporadaAlta implements PoliticaPrecio {

    /**
     * Aplica un recargo del 20 por ciento.
     *
     * @param base importe original
     * @return importe con recargo
     */
    @Override
    public double calcularPrecio(double base) {
        return base * 1.20;
    }
}