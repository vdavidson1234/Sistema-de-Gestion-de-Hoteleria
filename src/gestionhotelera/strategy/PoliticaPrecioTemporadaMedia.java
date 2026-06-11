package gestionhotelera.strategy;

/**
 * Politica base para temporada media, sin recargos ni rebajas.
 */
public class PoliticaPrecioTemporadaMedia implements PoliticaPrecio {
    @Override
    public double calcularPrecio(double base) {
        return base;
    }

    @Override
    public String getNombre() {
        return "Temporada media";
    }

    @Override
    public double getPorcentajeAjuste() {
        return 0.0;
    }
}
