package gestionhotelera.dominio;

/**
 * Enum que identifica los tipos de habitación que soporta el sistema.
 * Se usa junto con la fábrica para crear habitaciones sin repetir lógica.
 */
public enum TipoHabitacion {
    SIMPLE("Simple", 1, 65000.0),
    DOBLE("Doble", 2, 105000.0),
    TRIPLE("Triple", 3, 145000.0),
    FAMILIAR("Familiar", 4, 190000.0),
    SUITE("Suite", 2, 260000.0),
    LUJO("Lujo", 3, 340000.0),
    PENTHOUSE("Penthouse", 4, 620000.0);

    private final String nombreVisible;
    private final int capacidadEstandar;
    private final double precioBase;

    TipoHabitacion(String nombreVisible, int capacidadEstandar, double precioBase) {
        this.nombreVisible = nombreVisible;
        this.capacidadEstandar = capacidadEstandar;
        this.precioBase = precioBase;
    }

    public String getNombreVisible() {
        return nombreVisible;
    }

    public int getCapacidadEstandar() {
        return capacidadEstandar;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    @Override
    public String toString() {
        return nombreVisible + " | cap. " + capacidadEstandar + " | $ " + String.format("%.2f", precioBase);
    }
}
