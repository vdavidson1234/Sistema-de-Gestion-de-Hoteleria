package gestionhotelera.dominio;

/**
 * Servicio de estadia con precio fijo, cantidad y total calculado.
 */
public class ServicioAdicional implements ServicioEstadia {
    private final String nombre;
    private final String descripcion;
    private final double precioUnitario;
    private final int cantidad;

    public ServicioAdicional(String nombre, String descripcion, double precioUnitario, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad del servicio debe ser mayor a cero.");
        }
        if (precioUnitario < 0) {
            throw new IllegalArgumentException("El precio unitario del servicio no puede ser negativo.");
        }
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public double getPrecio() {
        return precioUnitario * cantidad;
    }

    @Override
    public int getCantidad() {
        return cantidad;
    }

    @Override
    public double getPrecioUnitario() {
        return precioUnitario;
    }
}
