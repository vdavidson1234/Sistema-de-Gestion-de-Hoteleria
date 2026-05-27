package gestionhotelera.decorator;

import gestionhotelera.dominio.ServicioEstadia;

/**
 * Decorador que agrega lavandería a la estadía.
 */
public class LavanderiaDecorator extends ServicioDecorator {

    /**
     * Crea el servicio de lavandería encima de otro servicio.
     *
     * @param servicioBase servicio envuelto
     */
    public LavanderiaDecorator(ServicioEstadia servicioBase) {
        super(servicioBase);
    }

    /**
     * Devuelve el nombre compuesto.
     *
     * @return nombre del servicio
     */
    @Override
    public String getNombre() {
        return getServicioBase().getNombre() + " + Lavandería";
    }

    /**
     * Devuelve la descripción compuesta.
     *
     * @return texto descriptivo
     */
    @Override
    public String getDescripcion() {
        return getServicioBase().getDescripcion() + ", incluye lavado y planchado";
    }

    /**
     * Devuelve el precio sumando el extra de lavandería.
     *
     * @return precio final
     */
    @Override
    public double getPrecio() {
        return getServicioBase().getPrecio() + 12000.0;
    }
}