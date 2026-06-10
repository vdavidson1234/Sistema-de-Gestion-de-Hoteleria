package gestionhotelera.control;

/**
 * Error de carga o guardado del estado del hotel.
 * Evita filtrar excepciones tecnicas de infraestructura hacia la UI.
 */
public class PersistenciaException extends Exception {
    public PersistenciaException(String message, Throwable cause) {
        super(message, cause);
    }
}
