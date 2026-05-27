package gestionhotelera.control;

import java.time.LocalDate;

import gestionhotelera.dominio.Estadia;
import gestionhotelera.dominio.Reserva;
import gestionhotelera.dominio.ServicioConsumido;
import gestionhotelera.dominio.StaySummary;
import gestionhotelera.strategy.DescuentoStrategy;
import gestionhotelera.strategy.PoliticaPrecio;

/**
 * Controlador encargado de registrar estadías y sumar servicios.
 * También centraliza el cálculo del costo final.
 */
public class GestorEstadias {

    /**
     * Crea el gestor de estadías.
     */
    public GestorEstadias() {
    }

    /**
     * Registra una estadía para una reserva confirmada.
     *
     * @param reserva reserva asociada
     * @param fechaIngreso ingreso real
     * @param fechaEgreso egreso real
     * @return resumen con la estadía creada
     */
    public StaySummary registrarEstadia(Reserva reserva, LocalDate fechaIngreso, LocalDate fechaEgreso) {
        if (reserva.getEstado() != gestionhotelera.dominio.EstadoReserva.CONFIRMADA) {
            throw new IllegalStateException("Solo se puede registrar una estadía a partir de una reserva confirmada.");
        }
        Estadia estadia = new Estadia(reserva, fechaIngreso, fechaEgreso);
        return new StaySummary(estadia);
    }

    /**
     * Cierra la estadía y marca la reserva como finalizada.
     *
     * @param estadia estadía que termina
     */
    public void finalizarEstadia(Estadia estadia) {
        estadia.getReserva().finalizar();
    }

    /**
     * Agrega un servicio a la estadía.
     *
     * @param estadia estadía activa
     * @param servicio servicio consumido
     */
    public void agregarServicio(Estadia estadia, ServicioConsumido servicio) {
        estadia.agregarServicio(servicio);
    }

    /**
     * Calcula el costo final combinando política de precio y descuento.
     *
     * @param estadia estadía a evaluar
     * @param politicaPrecio estrategia de precio
     * @param descuento estrategia de descuento
     * @return total final
     */
    public double calcularCostoTotal(Estadia estadia, PoliticaPrecio politicaPrecio, DescuentoStrategy descuento) {
        CalculadorCosto calculadorCosto = new CalculadorCosto();
        return calculadorCosto.calcularCosto(estadia, politicaPrecio, descuento);
    }
}