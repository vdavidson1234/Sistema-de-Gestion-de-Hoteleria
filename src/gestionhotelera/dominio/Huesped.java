package gestionhotelera.dominio;

/**
 * Representa a una persona hospedada en el hotel.
 * Esta clase solo guarda y expone datos básicos del huésped.
 */
public class Huesped {
    private final String nombre;
    private final String apellido;
    private final String dni;
    private final String telefono;
    private final String email;
    private final String tipoHuesped;

    /**
     * Crea un huésped con sus datos principales.
     *
     * @param nombre nombre de pila
     * @param apellido apellido
     * @param dni documento
     * @param telefono número de contacto
     * @param email correo electrónico
     * @param tipoHuesped clasificación interna o comercial
     */
    public Huesped(String nombre, String apellido, String dni, String telefono, String email, String tipoHuesped) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.telefono = telefono;
        this.email = email;
        this.tipoHuesped = tipoHuesped;
    }

    /**
     * Devuelve el nombre completo del huésped.
     *
     * @return nombre y apellido juntos
     */
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    /**
     * Devuelve el DNI del huésped.
     *
     * @return documento de identidad
     */
    public String getDni() {
        return dni;
    }

    /**
     * Devuelve el tipo de huésped.
     *
     * @return etiqueta comercial o administrativa
     */
    public String getTipoHuesped() {
        return tipoHuesped;
    }

    /**
     * Describe el huésped en formato legible.
     *
     * @return texto resumen
     */
    public String resumen() {
        return getNombreCompleto() + " | DNI: " + dni + " | Tel: " + telefono + " | Email: " + email;
    }
}