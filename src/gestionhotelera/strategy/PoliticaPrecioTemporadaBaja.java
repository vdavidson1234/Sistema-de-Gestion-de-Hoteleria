package gestionhotelera.strategy;

/**
 * Politica que reduce la tarifa de habitacion en temporada baja.
 */
public class PoliticaPrecioTemporadaBaja implements PoliticaPrecio {
    @Override
    public double calcularPrecio(double base) {
        return base * 0.85;
    }

    @Override
    public String getNombre() {
        return "Temporada baja";
    }

    @Override
    public double getPorcentajeAjuste() {
        return -15.0;
    }
}
