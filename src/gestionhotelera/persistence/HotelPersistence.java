package gestionhotelera.persistence;

import gestionhotelera.dominio.Habitacion;
import gestionhotelera.dominio.Hotel;
import gestionhotelera.dominio.Huesped;
import gestionhotelera.dominio.Reserva;
import gestionhotelera.dominio.TipoHabitacion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;

/**
 * Utilidad de persistencia para guardar/cargar datos básicos del hotel en SQLite.
 * La implementación actual maneja habitaciones, huespedes y reservas (campos mínimos).
 */
public class HotelPersistence {
    private final Database db;

    public HotelPersistence(Database db) {
        this.db = db;
    }

    /** Inicializa el esquema de la base de datos (crea tablas si no existen). */
    public void initialize() throws SQLException {
        db.initializeSchema();
    }

    public void saveHotel(Hotel hotel) throws SQLException {
        try (Connection c = db.getConnection()) {
            c.setAutoCommit(false);

            try (PreparedStatement upsertHab = c.prepareStatement(
                    "REPLACE INTO habitaciones(numero, capacidad, precio_base, tipo, estado) VALUES (?, ?, ?, ?, ?)")) {
                for (Habitacion h : hotel.getHabitaciones()) {
                    upsertHab.setInt(1, h.getNumero());
                    upsertHab.setInt(2, h.getCapacidad());
                    upsertHab.setDouble(3, h.getPrecioBase());
                    upsertHab.setString(4, h.getTipo().name());
                    upsertHab.setString(5, h.getEstado().name());
                    upsertHab.addBatch();
                }
                upsertHab.executeBatch();
            }

            // reservas + huéspedes
            try (PreparedStatement findHuesped = c.prepareStatement(
                    "SELECT id FROM huespedes WHERE dni = ?")) {
                try (PreparedStatement insertHuesped = c.prepareStatement(
                        "INSERT INTO huespedes(nombre, apellido, dni, telefono, email, tipo) VALUES (?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
                    try (PreparedStatement upsertReserva = c.prepareStatement(
                            "REPLACE INTO reservas(codigo, huesped_id, habitacion_num, fecha_ingreso, fecha_egreso, estado) VALUES (?, ?, ?, ?, ?, ?)")) {

                        for (Reserva r : hotel.getReservas()) {
                            Huesped h = r.getHuesped();

                            // buscar o insertar huésped
                            findHuesped.setString(1, h.getDni());
                            ResultSet rs = findHuesped.executeQuery();
                            int huespedId = -1;
                            if (rs.next()) {
                                huespedId = rs.getInt(1);
                            } else {
                                // La clase Huesped expone nombre completo, dni y tipo; dejamos otros campos vacíos
                                insertHuesped.setString(1, h.getNombreCompleto());
                                insertHuesped.setString(2, "");
                                insertHuesped.setString(3, h.getDni());
                                insertHuesped.setString(4, "");
                                insertHuesped.setString(5, "");
                                insertHuesped.setString(6, h.getTipoHuesped());
                                insertHuesped.executeUpdate();
                                try (ResultSet gk = insertHuesped.getGeneratedKeys()) {
                                    if (gk.next()) {
                                        huespedId = gk.getInt(1);
                                    }
                                }
                            }

                            upsertReserva.setString(1, r.getCodigo());
                            upsertReserva.setInt(2, huespedId);
                            upsertReserva.setInt(3, r.getHabitacion().getNumero());
                            upsertReserva.setString(4, r.getFechaIngreso().toString());
                            upsertReserva.setString(5, r.getFechaEgreso().toString());
                            upsertReserva.setString(6, r.getEstado().name());
                            upsertReserva.addBatch();
                        }
                        upsertReserva.executeBatch();
                    }
                }
            }

            c.commit();
        }
    }

    public Hotel loadHotel() throws SQLException {
        Hotel hotel = new Hotel("Hotel Aurora", "Av. Central 123");

        try (Connection c = db.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement("SELECT numero, capacidad, precio_base, tipo, estado FROM habitaciones")) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int numero = rs.getInt(1);
                    int capacidad = rs.getInt(2);
                    double precio = rs.getDouble(3);
                    TipoHabitacion tipo = TipoHabitacion.valueOf(rs.getString(4));
                    Habitacion h = new Habitacion(numero, capacidad, precio, tipo);
                    // estado
                    try {
                        h.cambiarEstado(gestionhotelera.dominio.EstadoHabitacion.valueOf(rs.getString(5)));
                    } catch (IllegalArgumentException ex) {
                        // ignore unknown state
                    }
                    hotel.agregarHabitacion(h);
                }
            }

            // cargar huéspedes y reservas
            HashMap<Integer, Huesped> huespedMap = new HashMap<>();
            try (PreparedStatement ps = c.prepareStatement("SELECT id, nombre, apellido, dni, telefono, email, tipo FROM huespedes")) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt(1);
                    Huesped h = new Huesped(rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7));
                    huespedMap.put(id, h);
                }
            }

            try (PreparedStatement ps = c.prepareStatement("SELECT codigo, huesped_id, habitacion_num, fecha_ingreso, fecha_egreso, estado FROM reservas")) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String codigo = rs.getString(1);
                    int huespedId = rs.getInt(2);
                    int habitacionNum = rs.getInt(3);
                    LocalDate ingreso = LocalDate.parse(rs.getString(4));
                    LocalDate egreso = LocalDate.parse(rs.getString(5));
                    Huesped h = huespedMap.get(huespedId);
                    Habitacion hab = hotel.buscarHabitacion(habitacionNum);
                    if (h != null && hab != null) {
                        Reserva r = new Reserva(codigo, h, hab, ingreso, egreso);
                        // No intentamos reconstruir el objeto State detalladamente;
                        // la reserva se cargará con su comportamiento por defecto.
                        hotel.registrarReserva(r);
                    }
                }
            }
        }

        return hotel;
    }
}
