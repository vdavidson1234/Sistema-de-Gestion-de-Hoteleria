package gestionhotelera.strategy;

/**
 * Descuento para acuerdos comerciales con empresas.
 */
public class DescuentoConvenioEmpresarial implements DescuentoStrategy {
    @Override
    public double aplicar(double total) {
        return total * 0.88;
    }

    @Override
    public String getNombre() {
        return "Convenio empresarial";
    }

    @Override
    public double getPorcentaje() {
        return 12.0;
    }

    @Override
    public String getTipoClienteRequerido() {
        return "Convenio empresarial";
    }
}
