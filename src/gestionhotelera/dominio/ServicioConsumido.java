package gestionhotelera.dominio;

/**
 * Contrato mínimo para cualquier servicio agregado a una estadía.
 * Permite tratar desayuno, spa, cochera o lavandería de la misma manera.
 */
public interface ServicioConsumido {

    /**
     * Devuelve el nombre del servicio.
     *
     * @return nombre visible
     */
    String getNombre();

    /**
     * Devuelve una descripción del servicio.
     *
     * @return explicación breve
     */
    String getDescripcion();

    /**
     * Devuelve el precio final del servicio.
     *
     * @return costo total
     */
    double getPrecio();
}