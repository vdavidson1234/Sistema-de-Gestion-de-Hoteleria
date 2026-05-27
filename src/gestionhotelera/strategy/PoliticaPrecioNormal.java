package gestionhotelera.strategy;

/**
 * Política de precio normal sin recargos ni rebajas.
 */
public class PoliticaPrecioNormal implements PoliticaPrecio {

    /**
     * Devuelve el mismo importe base.
     *
     * @param base importe original
     * @return importe sin cambios
     */
    @Override
    public double calcularPrecio(double base) {
        return base;
    }
}