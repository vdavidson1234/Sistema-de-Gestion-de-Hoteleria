package gestionhotelera.dominio;

/**
 * Representa una habitación del hotel.
 * Mantiene su número, capacidad, precio base, tipo y estado operativo.
 */
public class Habitacion {
    private final int numero;
    private final int capacidad;
    private final double precioBase;
    private EstadoHabitacion estado;
    private final TipoHabitacion tipo;
    private boolean activa;

    /**
     * Crea una habitación nueva con estado disponible por defecto.
     *
     * @param numero identificador de la habitación
     * @param capacidad cantidad máxima de huéspedes
     * @param precioBase tarifa base por noche
     * @param tipo tipo de habitación
     */
    public Habitacion(int numero, int capacidad, double precioBase, TipoHabitacion tipo) {
        this.numero = numero;
        this.capacidad = capacidad;
        this.precioBase = precioBase;
        this.tipo = tipo;
        this.estado = EstadoHabitacion.DISPONIBLE;
        this.activa = true;
    }

    /**
     * Indica si la habitación puede reservarse.
     *
     * @return true si está disponible
     */
    public boolean estaDisponible() {
        return activa && estado == EstadoHabitacion.DISPONIBLE;
    }

    public boolean admiteReservas() {
        return activa && (estado == EstadoHabitacion.DISPONIBLE || estado == EstadoHabitacion.RESERVADA);
    }

    /**
     * Cambia el estado operativo de la habitación.
     *
     * @param nuevoEstado nuevo estado a aplicar
     */
    public void cambiarEstado(EstadoHabitacion nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public void darDeBaja() {
        this.activa = false;
        this.estado = EstadoHabitacion.BLOQUEADA;
    }

    public void restaurarActiva(boolean activa) {
        this.activa = activa;
    }

    public void reactivar() {
        this.activa = true;
        this.estado = EstadoHabitacion.DISPONIBLE;
    }

    /**
     * Devuelve el número de la habitación.
     *
     * @return número identificador
     */
    public int getNumero() {
        return numero;
    }

    /**
     * Devuelve la capacidad máxima.
     *
     * @return cantidad de huéspedes permitidos
     */
    public int getCapacidad() {
        return capacidad;
    }

    /**
     * Devuelve el precio base por noche.
     *
     * @return tarifa base
     */
    public double getPrecioBase() {
        return precioBase;
    }

    /**
     * Devuelve el estado actual.
     *
     * @return estado operativo
     */
    public EstadoHabitacion getEstado() {
        return estado;
    }

    public boolean estaActiva() {
        return activa;
    }

    /**
     * Devuelve el tipo de habitación.
     *
     * @return tipo enumerado
     */
    public TipoHabitacion getTipo() {
        return tipo;
    }

    /**
     * Devuelve un resumen simple de la habitación.
     *
     * @return texto descriptivo
     */
    public String resumen() {
        return "Habitación " + numero + " [" + tipo + "] - capacidad: " + capacidad +
                " - precio base: " + precioBase + " - estado: " + estado + " - activa: " + activa;
    }
}
