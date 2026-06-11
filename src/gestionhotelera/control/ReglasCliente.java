package gestionhotelera.control;

import gestionhotelera.dominio.Huesped;
import gestionhotelera.strategy.DescuentoStrategy;
import java.text.Normalizer;
import java.util.Locale;

/**
 * Centraliza beneficios y restricciones comerciales asociadas al tipo de cliente.
 */
public class ReglasCliente {

    public double precioUnitarioServicio(Huesped titular, String nombreServicio, double precioLista) {
        return servicioBonificado(titular, nombreServicio) ? 0.0 : precioLista;
    }

    public boolean servicioBonificado(Huesped titular, String nombreServicio) {
        String tipoCliente = normalizar(titular == null ? null : titular.getTipoHuesped());
        String servicio = normalizar(nombreServicio);

        if (tipoCliente.contains("convenio empresarial")) {
            return "cochera".equals(servicio) || "desayuno".equals(servicio);
        }
        if (tipoCliente.contains("empresario") || tipoCliente.contains("corporativo")) {
            return "cochera".equals(servicio);
        }
        return false;
    }

    public String motivoBonificacionServicio(Huesped titular, String nombreServicio) {
        if (!servicioBonificado(titular, nombreServicio)) {
            return null;
        }
        return titular.getTipoHuesped();
    }

    public void validarDescuentoAplicable(Huesped titular, DescuentoStrategy descuento) {
        String tipoCliente = normalizar(titular == null ? null : titular.getTipoHuesped());
        String tipoRequerido = descuento.getTipoClienteRequerido();
        if (tipoRequerido == null || tipoRequerido.isBlank()) {
            return;
        }
        if (!tipoCliente.contains(normalizar(tipoRequerido))) {
            throw new IllegalStateException("El descuento de " + descuento.getNombre().toLowerCase(Locale.ROOT)
                    + " solo puede aplicarse a titulares registrados como " + tipoRequerido + ".");
        }
    }

    public String describirBeneficios(Huesped titular) {
        String tipoCliente = normalizar(titular == null ? null : titular.getTipoHuesped());
        if (tipoCliente.contains("convenio empresarial")) {
            return "Cochera y desayuno bonificados";
        }
        if (tipoCliente.contains("empresario") || tipoCliente.contains("corporativo")) {
            return "Cochera bonificada";
        }
        return "Sin beneficios automáticos";
    }

    public String requisitoDescuento(DescuentoStrategy descuento) {
        String tipoRequerido = descuento.getTipoClienteRequerido();
        if (tipoRequerido == null || tipoRequerido.isBlank()) {
            return "";
        }
        return " | Requiere titular " + tipoRequerido;
    }

    private String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String sinAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinAcentos.toLowerCase(Locale.ROOT).trim();
    }
}
