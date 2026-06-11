package gestionhotelera.factory;

import gestionhotelera.dominio.Habitacion;
import gestionhotelera.dominio.TipoHabitacion;

/**
 * Fábrica simple para crear habitaciones según su tipo.
 * Centraliza la lógica de construcción para respetar OCP y evitar duplicación.
 */
public class HabitacionFactory {

    /**
     * Crea una habitación con la configuración correcta según el tipo indicado.
     *
     * @param numero número de habitación
     * @param capacidad capacidad máxima
     * @param precioBase precio base por noche
     * @param tipo tipo de habitación
     * @return habitación lista para registrar
     */
    public Habitacion crearHabitacion(int numero, int capacidad, double precioBase, TipoHabitacion tipo) {
        return new Habitacion(numero, tipo.getCapacidadEstandar(), tipo.getPrecioBase(), tipo);
    }
}
