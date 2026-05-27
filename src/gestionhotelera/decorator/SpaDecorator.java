package gestionhotelera.decorator;

import gestionhotelera.dominio.ServicioEstadia;

/**
 * Decorador que agrega acceso a spa.
 */
public class SpaDecorator extends ServicioDecorator {

    /**
     * Crea el servicio de spa encima de otro servicio.
     *
     * @param servicioBase servicio envuelto
     */
    public SpaDecorator(ServicioEstadia servicioBase) {
        super(servicioBase);
    }

    /**
     * Devuelve el nombre compuesto.
     *
     * @return nombre del servicio
     */
    @Override
    public String getNombre() {
        return getServicioBase().getNombre() + " + Spa";
    }

    /**
     * Devuelve la descripción compuesta.
     *
     * @return texto descriptivo
     */
    @Override
    public String getDescripcion() {
        return getServicioBase().getDescripcion() + ", incluye acceso al spa";
    }

    /**
     * Devuelve el precio sumando el extra del spa.
     *
     * @return precio final
     */
    @Override
    public double getPrecio() {
        return getServicioBase().getPrecio() + 22000.0;
    }
}