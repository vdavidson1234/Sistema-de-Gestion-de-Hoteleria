package gestionhotelera.decorator;

/**
 * Decorador opcional para cochera.
 */
public class CocheraDecorator extends ServicioDecorator {

    /**
     * Crea el servicio de cochera encima de otro servicio.
     *
     * @param servicioBase servicio envuelto
     */
    public CocheraDecorator(gestionhotelera.dominio.ServicioEstadia servicioBase) {
        super(servicioBase);
    }

    /**
     * Devuelve el nombre compuesto.
     *
     * @return nombre del servicio
     */
    @Override
    public String getNombre() {
        return getServicioBase().getNombre() + " + Cochera";
    }

    /**
     * Devuelve la descripción compuesta.
     *
     * @return texto descriptivo
     */
    @Override
    public String getDescripcion() {
        return getServicioBase().getDescripcion() + ", incluye cochera cubierta";
    }

    /**
     * Devuelve el precio sumando el extra de cochera.
     *
     * @return precio final
     */
    @Override
    public double getPrecio() {
        return getServicioBase().getPrecio() + 10000.0;
    }
}