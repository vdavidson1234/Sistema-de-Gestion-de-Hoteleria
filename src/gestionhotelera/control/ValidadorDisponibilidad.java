package gestionhotelera.control;

import java.time.LocalDate;
import java.util.List;

import gestionhotelera.dominio.Habitacion;
import gestionhotelera.dominio.Hotel;

/**
 * Servicio de apoyo para validar disponibilidad antes de crear reservas.
 * Se mantiene separado para respetar SRP.
 */
public class ValidadorDisponibilidad {

    /**
     * Consulta habitaciones disponibles.
     *
     * @param hotel hotel a consultar
     * @param fechaIngreso ingreso solicitado
     * @param fechaEgreso egreso solicitado
     * @param personas cantidad de personas
     * @return lista de habitaciones disponibles
     */
    public List<Habitacion> validar(Hotel hotel, LocalDate fechaIngreso, LocalDate fechaEgreso, int personas) {
        return hotel.consultarDisponibilidad(fechaIngreso, fechaEgreso, personas);
    }
}