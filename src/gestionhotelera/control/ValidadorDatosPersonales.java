package gestionhotelera.control;

/**
 * Reglas de validación para datos básicos de huéspedes.
 */
public class ValidadorDatosPersonales {
    private static final String EMAIL_REGEX = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
    private static final String TEXTO_CON_NUMEROS_REGEX = ".*\\d.*";
    private static final String DNI_REGEX = "\\d{6,12}";

    public void validarHuesped(String nombre, String apellido, String dni, String telefono, String email) {
        validarNombre("nombre", nombre);
        validarNombre("apellido", apellido);
        validarDni(dni);
        validarTelefono(telefono);
        validarEmail(email);
    }

    public void validarNombre(String campo, String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El " + campo + " es obligatorio.");
        }
        if (valor.matches(TEXTO_CON_NUMEROS_REGEX)) {
            throw new IllegalArgumentException("El " + campo + " no puede contener números.");
        }
    }

    public void validarDni(String dni) {
        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El DNI es obligatorio.");
        }
        if (!dni.trim().matches(DNI_REGEX)) {
            throw new IllegalArgumentException("El DNI debe contener solo números y tener entre 6 y 12 dígitos.");
        }
    }

    public void validarTelefono(String telefono) {
        if (telefono == null || telefono.isBlank()) {
            return;
        }
        if (telefono.matches(".*[A-Za-zÁÉÍÓÚÜÑáéíóúüñ].*")) {
            throw new IllegalArgumentException("El teléfono no puede contener letras.");
        }
    }

    public void validarEmail(String email) {
        if (email == null || email.isBlank()) {
            return;
        }
        if (!email.trim().matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("El email debe tener un formato válido.");
        }
    }
}
