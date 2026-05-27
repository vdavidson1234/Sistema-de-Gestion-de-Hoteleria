package gestionhotelera.dominio;

/**
 * Enum que identifica los tipos de habitación que soporta el sistema.
 * Se usa junto con la fábrica para crear habitaciones sin repetir lógica.
 */
public enum TipoHabitacion {
    SIMPLE,
    DOBLE,
    SUITE
}