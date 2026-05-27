package gestionhotelera.control;

import gestionhotelera.dominio.Estadia;
import gestionhotelera.strategy.DescuentoStrategy;
import gestionhotelera.strategy.PoliticaPrecio;

/**
 * Calcula el costo final de una estadía.
 * Depende de interfaces para respetar DIP y permitir nuevas políticas sin tocar esta clase.
 */
public class CalculadorCosto {

    /**
     * Calcula el total final aplicando noches, servicios, política de precio y descuento.
     *
     * @param estadia estadía a cotizar
     * @param politicaPrecio estrategia de precio base
     * @param descuento estrategia de descuento
     * @return total final a pagar
     */
    public double calcularCosto(Estadia estadia, PoliticaPrecio politicaPrecio, DescuentoStrategy descuento) {
        double baseHabitacion = estadia.getReserva().getHabitacion().getPrecioBase() * estadia.calcularNoches();
        double totalServicios = estadia.calcularTotalServicios();
        double baseTotal = baseHabitacion + totalServicios;
        double precioAjustado = politicaPrecio.calcularPrecio(baseTotal);
        return descuento.aplicar(precioAjustado);
    }
}