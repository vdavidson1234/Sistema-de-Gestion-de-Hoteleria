package gestionhotelera.decorator;

/**
 * Decorador abstracto que envuelve otro servicio.
 * Cada subclase agrega un extra de precio y texto.
 */
public abstract class ServicioDecorator implements gestionhotelera.dominio.ServicioEstadia {
    private final gestionhotelera.dominio.ServicioEstadia servicioBase;

    /**
     * Crea un decorador apuntando al servicio que se va a ampliar.
     *
     * @param servicioBase servicio envuelto
     */
    protected ServicioDecorator(gestionhotelera.dominio.ServicioEstadia servicioBase) {
        this.servicioBase = servicioBase;
    }

    /**
     * Devuelve el servicio envuelto para sumar información sobre él.
     *
     * @return servicio envuelto
     */
    protected gestionhotelera.dominio.ServicioEstadia getServicioBase() {
        return servicioBase;
    }
}