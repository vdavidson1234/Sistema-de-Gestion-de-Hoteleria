package gestionhotelera.decorator;

/**
 * Componente base del patrón Decorator.
 * Representa una estadía sin servicios adicionales.
 */
public class ServicioBase implements gestionhotelera.dominio.ServicioEstadia {

    /**
     * Devuelve el nombre del componente base.
     *
     * @return nombre fijo
     */
    @Override
    public String getNombre() {
        return "Servicio base";
    }

    /**
     * Devuelve la descripción del componente base.
     *
     * @return texto fijo
     */
    @Override
    public String getDescripcion() {
        return "Permanencia sin extras";
    }

    /**
     * Devuelve un precio nulo para poder sumar decoradores encima.
     *
     * @return 0.0
     */
    @Override
    public double getPrecio() {
        return 0.0;
    }
}