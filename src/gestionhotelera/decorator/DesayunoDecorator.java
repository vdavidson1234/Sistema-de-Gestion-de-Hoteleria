package gestionhotelera.decorator;

import gestionhotelera.dominio.ServicioEstadia;

/**
 * Decorador que agrega desayuno a la estadía.
 */
public class DesayunoDecorator extends ServicioDecorator {

    /**
     * Crea el servicio de desayuno encima de otro servicio.
     *
     * @param servicioBase servicio envuelto
     */
    public DesayunoDecorator(ServicioEstadia servicioBase) {
        super(servicioBase);
    }

    /**
     * Devuelve el nombre compuesto.
     *
     * @return nombre del servicio
     */
    @Override
    public String getNombre() {
        return getServicioBase().getNombre() + " + Desayuno";
    }

    /**
     * Devuelve la descripción compuesta.
     *
     * @return texto descriptivo
     */
    @Override
    public String getDescripcion() {
        return getServicioBase().getDescripcion() + ", incluye desayuno continental";
    }

    /**
     * Devuelve el precio sumando el extra del desayuno.
     *
     * @return precio final
     */
    @Override
    public double getPrecio() {
        return getServicioBase().getPrecio() + 15000.0;
    }
}