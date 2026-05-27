package gestionhotelera.strategy;

/**
 * Política que reduce el precio para campañas promocionales.
 */
public class PoliticaPrecioPromocional implements PoliticaPrecio {

    /**
     * Aplica un descuento del 15 por ciento al valor base.
     *
     * @param base importe original
     * @return importe promocional
     */
    @Override
    public double calcularPrecio(double base) {
        return base * 0.85;
    }
}