package gestionhotelera.control;

import gestionhotelera.dominio.Estadia;
import gestionhotelera.dominio.Hotel;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Estado persistible del hotel: datos principales y estadias cargadas.
 */
public class HotelSnapshot {
    private static final String DEFAULT_HOTEL_NAME = "Hotel Aurora";
    private static final String DEFAULT_HOTEL_ADDRESS = "Av. Central 123";

    private final Hotel hotel;
    private final Map<String, Estadia> estadiasPorReserva;

    public HotelSnapshot(Hotel hotel, Map<String, Estadia> estadiasPorReserva) {
        this.hotel = hotel;
        this.estadiasPorReserva = new LinkedHashMap<>(estadiasPorReserva);
    }

    public static HotelSnapshot empty() {
        return new HotelSnapshot(new Hotel(DEFAULT_HOTEL_NAME, DEFAULT_HOTEL_ADDRESS), new LinkedHashMap<>());
    }

    public Hotel getHotel() {
        return hotel;
    }

    public Map<String, Estadia> getEstadiasPorReserva() {
        return new LinkedHashMap<>(estadiasPorReserva);
    }
}
