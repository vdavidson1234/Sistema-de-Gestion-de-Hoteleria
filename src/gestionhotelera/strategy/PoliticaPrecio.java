package gestionhotelera.strategy;

/**
 * Contrato del patrón Strategy para modificar el precio base.
 * Permite cambiar la política sin alterar la calculadora de costos.
 */
public interface PoliticaPrecio {

    /**
     * Calcula el precio ajustado a partir de un valor base.
     *
     * @param base importe original
     * @return importe ajustado
     */
    double calcularPrecio(double base);
}