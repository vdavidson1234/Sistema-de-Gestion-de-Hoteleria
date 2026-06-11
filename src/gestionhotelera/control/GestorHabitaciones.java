package gestionhotelera.control;

import gestionhotelera.dominio.Habitacion;
import gestionhotelera.dominio.EstadoHabitacion;
import gestionhotelera.dominio.Hotel;
import gestionhotelera.dominio.TipoHabitacion;
import gestionhotelera.factory.HabitacionFactory;

/**
 * Controlador encargado de alta y gestión de habitaciones.
 * Aplica el rol de Controller del patrón GRASP.
 */
public class GestorHabitaciones {
    private final Hotel hotel;
    private final HabitacionFactory habitacionFactory;

    /**
     * Crea el gestor con sus dependencias.
     *
     * @param hotel hotel donde se registran habitaciones
     * @param habitacionFactory fábrica usada para crearlas
     */
    public GestorHabitaciones(Hotel hotel, HabitacionFactory habitacionFactory) {
        this.hotel = hotel;
        this.habitacionFactory = habitacionFactory;
    }

    /**
     * Crea una habitación y la registra en el hotel.
     *
     * @param numero número identificador
     * @param capacidad cantidad máxima de personas
     * @param precioBase tarifa base
     * @param tipo tipo de habitación
     * @return habitación creada
     */
    public Habitacion crearYRegistrarHabitacion(int numero, int capacidad, double precioBase, TipoHabitacion tipo) {
        Habitacion habitacion = habitacionFactory.crearHabitacion(numero, capacidad, precioBase, tipo);
        hotel.agregarHabitacion(habitacion);
        return habitacion;
    }

    public void cambiarEstado(int numero, EstadoHabitacion estado) {
        Habitacion habitacion = hotel.buscarHabitacion(numero);
        if (habitacion == null) {
            throw new IllegalStateException("No existe una habitacion con ese numero.");
        }
        habitacion.cambiarEstado(estado);
    }

    public void eliminarHabitacion(int numero) {
        hotel.eliminarHabitacion(numero);
    }
}
