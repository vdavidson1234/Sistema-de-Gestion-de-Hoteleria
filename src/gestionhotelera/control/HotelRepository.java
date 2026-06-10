package gestionhotelera.control;

import gestionhotelera.dominio.Estadia;
import gestionhotelera.dominio.Hotel;
import java.util.Map;

/**
 * Puerto de persistencia del sistema hotelero.
 * La capa de control depende de esta abstraccion, no de una base de datos concreta.
 */
public interface HotelRepository {
    HotelSnapshot loadSnapshot() throws PersistenciaException;

    void saveSnapshot(Hotel hotel, Map<String, Estadia> estadiasPorReserva) throws PersistenciaException;
}
