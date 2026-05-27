package gestionhotelera.dominio;

/**
 * Objeto simple para devolver una estadía junto con un resumen legible.
 * Se usa en la demo para mostrar el resultado sin mezclar lógica extra en el controlador.
 */
public class StaySummary {
    private final Estadia estadia;

    /**
     * Crea el resumen envolviendo la estadía generada.
     *
     * @param estadia estadía recién registrada
     */
    public StaySummary(Estadia estadia) {
        this.estadia = estadia;
    }

    /**
     * Devuelve la estadía original.
     *
     * @return estadía asociada
     */
    public Estadia getEstadia() {
        return estadia;
    }

    /**
     * Devuelve un texto corto con el estado de la estadía.
     *
     * @return resumen descriptivo
     */
    public String descripcion() {
        return estadia.resumen();
    }
}