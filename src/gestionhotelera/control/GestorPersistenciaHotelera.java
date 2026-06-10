package gestionhotelera.control;

import gestionhotelera.dominio.Estadia;
import gestionhotelera.dominio.Hotel;
import java.util.Map;

/**
 * Controlador de aplicacion para cargar y guardar el estado del hotel.
 * Encapsula el puerto de persistencia para que la UI no conozca adaptadores ni bases de datos.
 */
public class GestorPersistenciaHotelera {
    private final HotelRepository repository;
    private boolean disponible;
    private String ultimoMensaje;

    public GestorPersistenciaHotelera() {
        this(null);
    }

    public GestorPersistenciaHotelera(HotelRepository repository) {
        this.repository = repository;
        this.disponible = false;
        this.ultimoMensaje = "Persistencia externa deshabilitada; se usa memoria local.";
    }

    public HotelSnapshot cargarEstadoInicial() {
        if (repository == null) {
            disponible = false;
            ultimoMensaje = "Persistencia externa deshabilitada; se usa memoria local.";
            return HotelSnapshot.empty();
        }

        try {
            HotelSnapshot snapshot = repository.loadSnapshot();
            disponible = true;
            ultimoMensaje = "Persistencia externa conectada.";
            return snapshot;
        } catch (PersistenciaException ex) {
            disponible = false;
            ultimoMensaje = "Persistencia externa no disponible; se usa memoria local. Detalle: " + ex.getMessage();
            return HotelSnapshot.empty();
        }
    }

    public String guardarSnapshot(Hotel hotel, Map<String, Estadia> estadiasPorReserva) {
        if (!disponible || repository == null) {
            return null;
        }

        try {
            repository.saveSnapshot(hotel, estadiasPorReserva);
            return null;
        } catch (PersistenciaException ex) {
            ultimoMensaje = "No se pudo guardar el estado del hotel: " + ex.getMessage();
            return ultimoMensaje;
        }
    }

    public boolean estaDisponible() {
        return disponible;
    }

    public String getUltimoMensaje() {
        return ultimoMensaje;
    }
}
